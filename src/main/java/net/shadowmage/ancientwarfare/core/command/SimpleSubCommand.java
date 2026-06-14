package net.shadowmage.ancientwarfare.core.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class SimpleSubCommand implements ISubCommand {
    private final String name;
    private final ISubCommandExecutor executor;

    public SimpleSubCommand(String name, ISubCommandExecutor executor) {
        this.name = name;
        this.executor = executor;
    }

    @Override
    public ArgumentBuilder<CommandSourceStack, ?> build() {
        return Commands.literal(name).executes(ctx -> {
            executor.execute(ctx);
            return Command.SINGLE_SUCCESS;
        });
    }

    public interface ISubCommandExecutor {
        void execute(CommandContext<CommandSourceStack> ctx);
    }
}
