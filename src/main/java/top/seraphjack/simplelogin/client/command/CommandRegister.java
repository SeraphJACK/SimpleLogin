package top.seraphjack.simplelogin.client.command;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import top.seraphjack.simplelogin.client.Notifier;
import top.seraphjack.simplelogin.network.MessageLogin;
import top.seraphjack.simplelogin.network.NetworkLoader;
import top.seraphjack.simplelogin.utils.SHA256;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CommandRegister extends CommandBase {
    @Override
    public String getName() {
        return "register";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/register <Password> <Password>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length != 2) {
            throw new CommandException("invalid arguments");
        }
        if (!args[0].equals(args[1])) {
            throw new CommandException("Password does not match");
        }
        NetworkLoader.INSTANCE.sendToServer(new MessageLogin(SHA256.getHashWithSalt(args[0])));
        Notifier.instance().clearNotify();
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
