package net.shadowmage.ancientwarfare.core.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;

/**
 * Modern Brigadier representation of a root command.
 */
public abstract class RootCommand extends ParentCommand {

    public abstract String getName();

    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(appendSubCommands(net.minecraft.commands.Commands.literal(getName())));
    }
}
