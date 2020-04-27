package top.seraphjack.simplelogin.client.command;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import top.seraphjack.simplelogin.client.Notifier;
import top.seraphjack.simplelogin.network.MessageLogin;
import top.seraphjack.simplelogin.network.NetworkLoader;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CommandLogin extends CommandBase {
    @Override
    public String getName() {
        return "login";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/login password";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length != 1) {
            throw new CommandException("Invalid arguments");
        }
        NetworkLoader.INSTANCE.sendToServer(new MessageLogin(args[0]));
        Notifier.instance().clearNotify();
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
