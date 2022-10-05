package top.seraphjack.simplelogin.client;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.seraphjack.simplelogin.network.MessageChangePassword;
import top.seraphjack.simplelogin.network.NetworkLoader;

import static net.minecraft.commands.Commands.*;

@OnlyIn(Dist.CLIENT)
public final class ChangePasswordCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var theCommand =
                literal("simplelogin")
                        .then(literal("change_password")
                                .then(argument("passwd", StringArgumentType.string())
                                        .executes(ChangePasswordCommand::changePassword)));
        dispatcher.register(theCommand);
    }

    private static int changePassword(CommandContext<CommandSourceStack> context) {
        final var to = StringArgumentType.getString(context, "passwd");
        var msg = new MessageChangePassword(PasswordHolder.instance().password(), to);
        PasswordHolder.instance().setPendingPassword(to);
        NetworkLoader.INSTANCE.sendToServer(msg);
        return Command.SINGLE_SUCCESS;
    }
}
