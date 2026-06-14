package net.shadowmage.ancientwarfare.core.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketManualReload;
// TODO Phase 2: import net.shadowmage.ancientwarfare.core.util.FileUtils;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class CommandUtils {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("awutils")
            .then(Commands.literal("exportentities")
                .then(Commands.argument("fileName", StringArgumentType.string())
                    .executes(ctx -> exportEntities(ctx, StringArgumentType.getString(ctx, "fileName")))
                )
                .executes(ctx -> exportEntities(ctx, "entitylist.csv"))
            )
            .then(Commands.literal("exportbiomes")
                .then(Commands.argument("fileName", StringArgumentType.string())
                    .executes(ctx -> exportBiomes(ctx, StringArgumentType.getString(ctx, "fileName")))
                )
                .executes(ctx -> exportBiomes(ctx, "biomelist.csv"))
            )
            .then(Commands.literal("exportblocks")
                .then(Commands.argument("fileName", StringArgumentType.string())
                    .executes(ctx -> exportBlocks(ctx, StringArgumentType.getString(ctx, "fileName")))
                )
                .executes(ctx -> exportBlocks(ctx, "blocklist.csv"))
            )
            .then(Commands.literal("exportloottables")
                .then(Commands.argument("fileName", StringArgumentType.string())
                    .executes(ctx -> exportLootTables(ctx, StringArgumentType.getString(ctx, "fileName")))
                )
                .executes(ctx -> exportLootTables(ctx, "loottablelist.csv"))
            )
            .then(Commands.literal("reloadmanual")
                .executes(CommandUtils::reloadManual)
            )
            .then(Commands.literal("loadChunks")
                .then(Commands.argument("diameterInChunks", IntegerArgumentType.integer(1))
                    .executes(ctx -> loadChunks(ctx, IntegerArgumentType.getInteger(ctx, "diameterInChunks")))
                )
            )
        );
    }

    private static void exportToFile(CommandSourceStack source, File exportFile, String header, List<String> data) {
        ArrayList<String> rows = new ArrayList<>();
        rows.add(header);
        rows.addAll(data);
        // TODO Phase 2: FileUtils.exportToFile(exportFile, rows);
        source.sendSuccess(() -> Component.literal("File exported to " + exportFile.getAbsoluteFile()), false);
    }

    private static int exportEntities(CommandContext<CommandSourceStack> ctx, String fileName) {
        List<String> lines = BuiltInRegistries.ENTITY_TYPE.stream()
            .map(e -> String.join(",", BuiltInRegistries.ENTITY_TYPE.getKey(e).toString(), e.getDescriptionId()))
            .sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        exportToFile(ctx.getSource(), new File(AWCoreStatics.utilsExportPath, fileName), "Registry Name,Entity Name", lines);
        return 1;
    }

    private static int exportBiomes(CommandContext<CommandSourceStack> ctx, String fileName) {
        // Modern biomes are in a dynamic registry
        var biomeRegistry = ctx.getSource().getServer().registryAccess().registryOrThrow(Registries.BIOME);
        List<String> lines = biomeRegistry.entrySet().stream()
            .map(entry -> String.join(",", entry.getKey().location().toString(), entry.getValue().getGenerationSettings().hasFeature(null) ? "N" : "Y")) // Simplified
            .sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        exportToFile(ctx.getSource(), new File(AWCoreStatics.utilsExportPath, fileName), "Registry Name,HasFeatures", lines);
        return 1;
    }

    private static int exportBlocks(CommandContext<CommandSourceStack> ctx, String fileName) {
        List<String> lines = BuiltInRegistries.BLOCK.stream()
            .map(b -> String.join(",", BuiltInRegistries.BLOCK.getKey(b).toString(), b.getDescriptionId()))
            .sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        exportToFile(ctx.getSource(), new File(AWCoreStatics.utilsExportPath, fileName), "Registry Name,Block Name", lines);
        return 1;
    }

    private static int exportLootTables(CommandContext<CommandSourceStack> ctx, String fileName) {
        var lootRegistry = ctx.getSource().getServer().registryAccess().registryOrThrow(Registries.LOOT_TABLE);
        List<String> lines = lootRegistry.keySet().stream().map(ResourceLocation::toString).collect(Collectors.toList());
        exportToFile(ctx.getSource(), new File(AWCoreStatics.utilsExportPath, fileName), "Registry Name", lines);
        return 1;
    }

    private static int reloadManual(CommandContext<CommandSourceStack> ctx) {
        if (ctx.getSource().getEntity() instanceof ServerPlayer player) {
            NetworkHandler.sendToPlayer(player, new PacketManualReload());
        }
        return 1;
    }

    private static int loadChunks(CommandContext<CommandSourceStack> ctx, int range) {
        if (ctx.getSource().getEntity() instanceof ServerPlayer player) {
            int chunkLoadRadius = ctx.getSource().getServer().getPlayerList().getViewDistance();
            new PlayerMover().startMoving(player, chunkLoadRadius, range);
        }
        return 1;
    }

    private static class PlayerMover {
        private ServerPlayer player;
        private int chunkLoadRadius;
        private int range;
        private BlockPos originalPosition;
        private ChunkPos originalChunkPos;
        private boolean finishedMoving = true;
        private Iterator<ChunkPos> iterator;
        private int timeout = 0;

        private void startMoving(ServerPlayer player, int chunkLoadRadius, int range) {
            this.player = player;
            this.originalPosition = player.blockPosition();
            this.originalChunkPos = new ChunkPos(this.originalPosition);
            this.chunkLoadRadius = chunkLoadRadius;
            this.range = range;
            this.finishedMoving = false;
            this.iterator = getAllChunkPosStops();
            NeoForge.EVENT_BUS.register(this);
        }

        @SubscribeEvent
        public void serverTick(ServerTickEvent.Post evt) {
            if (timeout <= 0) {
                movePlayer();
                timeout = 200;
            } else {
                timeout--;
            }
        }

        private Iterator<ChunkPos> getAllChunkPosStops() {
            List<ChunkPos> stops = new ArrayList<>();
            int initialX = originalChunkPos.x - range + chunkLoadRadius;
            int initialZ = originalChunkPos.z - range + chunkLoadRadius;
            int maxX = originalChunkPos.x + range;
            int maxZ = originalChunkPos.z + range;

            for (int z = initialZ; z < maxZ; z += 2 * chunkLoadRadius) {
                for (int x = initialX; x < maxX; x += 2 * chunkLoadRadius) {
                    stops.add(new ChunkPos(x, z));
                }
            }
            return stops.iterator();
        }

        private void movePlayer() {
            if (!finishedMoving) {
                if (!iterator.hasNext()) {
                    player.teleportTo(originalPosition.getX(), originalPosition.getY(), originalPosition.getZ());
                    finishedMoving = true;
                    NeoForge.EVENT_BUS.unregister(this);
                    return;
                }
                ChunkPos chunkPos = iterator.next();
                player.teleportTo(chunkPos.getMinBlockX() + 8d, 255, chunkPos.getMinBlockZ() + 8d);
            }
        }
    }
}
