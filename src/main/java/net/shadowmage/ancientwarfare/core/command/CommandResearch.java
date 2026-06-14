package net.shadowmage.ancientwarfare.core.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.shadowmage.ancientwarfare.core.registry.ResearchRegistry;
import net.shadowmage.ancientwarfare.core.research.ResearchTracker;

public class CommandResearch {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("awresearch")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("add")
                .then(Commands.argument("player", EntityArgument.player())
                    .then(Commands.argument("goal", StringArgumentType.string())
                        .suggests((ctx, builder) -> net.minecraft.commands.SharedSuggestionProvider.suggest(ResearchRegistry.getAllResearchGoals().stream().map(net.shadowmage.ancientwarfare.core.research.ResearchGoal::getName), builder))
                        .executes(ctx -> addResearch(ctx))
                    )
                )
            )
            .then(Commands.literal("remove")
                .then(Commands.argument("player", EntityArgument.player())
                    .then(Commands.argument("goal", StringArgumentType.string())
                        .suggests((ctx, builder) -> net.minecraft.commands.SharedSuggestionProvider.suggest(ResearchRegistry.getAllResearchGoals().stream().map(net.shadowmage.ancientwarfare.core.research.ResearchGoal::getName), builder))
                        .executes(ctx -> removeResearch(ctx))
                    )
                )
            )
            .then(Commands.literal("fill")
                .then(Commands.argument("player", EntityArgument.player())
                    .executes(ctx -> fillResearch(ctx))
                )
            )
            .then(Commands.literal("clear")
                .then(Commands.argument("player", EntityArgument.player())
                    .executes(ctx -> clearResearch(ctx))
                )
            )
        );
    }

    private static int addResearch(CommandContext<CommandSourceStack> ctx) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        String goal = StringArgumentType.getString(ctx, "goal");
        if (!ResearchRegistry.researchExists(goal)) {
            // Can't research non-existent
            return 0;
        }
        ResearchTracker.INSTANCE.addResearchFromNotes(ctx.getSource().getLevel(), player.getName().getString(), goal);
        return 1;
    }

    private static int removeResearch(CommandContext<CommandSourceStack> ctx) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        String goal = StringArgumentType.getString(ctx, "goal");
        if (!ResearchRegistry.researchExists(goal)) {
            return 0;
        }
        ResearchTracker.INSTANCE.removeResearch(ctx.getSource().getLevel(), player.getName().getString(), goal);
        return 1;
    }

    private static int fillResearch(CommandContext<CommandSourceStack> ctx) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        ResearchTracker.INSTANCE.fillResearch(ctx.getSource().getLevel(), player.getName().getString());
        return 1;
    }

    private static int clearResearch(CommandContext<CommandSourceStack> ctx) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        ResearchTracker.INSTANCE.clearResearch(ctx.getSource().getLevel(), player.getName().getString());
        return 1;
    }
}
