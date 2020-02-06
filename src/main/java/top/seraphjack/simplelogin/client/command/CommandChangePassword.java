package top.seraphjack.simplelogin.client.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import top.seraphjack.simplelogin.SLConfig;
import top.seraphjack.simplelogin.network.MessageChangePassword;
import top.seraphjack.simplelogin.network.NetworkLoader;

import javax.annotation.Nullable;
import java.util.List;

public class CommandChangePassword {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("sl_changepassword")
                .requires((r) -> true)
                .then(Commands.argument("original", StringArgumentType.string()).
                        then(Commands.argument("new", StringArgumentType.string())
                                .executes((c) -> {
                                    // TODO
                                    return 1;
                                }))
                )
        );
    }
}
