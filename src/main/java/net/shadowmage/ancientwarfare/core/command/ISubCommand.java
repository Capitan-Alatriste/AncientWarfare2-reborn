package net.shadowmage.ancientwarfare.core.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;

/**
 * Modern Brigadier representation of a subcommand.
 */
public interface ISubCommand {

    /**
     * @return The brigadier argument builder to be appended to the parent command.
     */
    ArgumentBuilder<CommandSourceStack, ?> build();
}
