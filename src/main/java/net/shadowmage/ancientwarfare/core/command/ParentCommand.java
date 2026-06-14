package net.shadowmage.ancientwarfare.core.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;

import java.util.ArrayList;
import java.util.List;

public abstract class ParentCommand implements ISubCommand {
    private final List<ISubCommand> subCommands = new ArrayList<>();

    protected void registerSubCommand(ISubCommand subCommand) {
        subCommands.add(subCommand);
    }

    @Override
    public ArgumentBuilder<CommandSourceStack, ?> build() {
        // Return a dummy builder that attaches all children. Subclasses should override if they need a specific node.
        // Actually, for ParentCommand to work as an argument builder, we'll return a literal node builder with an empty name
        // except when used properly. Let's provide a utility method instead for subcommands.
        // RootCommand overrides this.
        var builder = net.minecraft.commands.Commands.literal("");
        for (ISubCommand subCommand : subCommands) {
            builder.then(subCommand.build());
        }
        return builder;
    }

    /**
     * Builds and appends subcommands to the given builder.
     */
    public <T extends ArgumentBuilder<CommandSourceStack, T>> T appendSubCommands(T builder) {
        for (ISubCommand subCommand : subCommands) {
            builder.then(subCommand.build());
        }
        return builder;
    }
}
