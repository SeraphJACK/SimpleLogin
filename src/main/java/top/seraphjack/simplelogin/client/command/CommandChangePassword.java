package top.seraphjack.simplelogin.client.command;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import top.seraphjack.simplelogin.network.MessageChangePassword;
import top.seraphjack.simplelogin.network.NetworkLoader;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CommandChangePassword extends CommandBase {
    @Override
    public String getName() {
        return "sl_changepassword";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/sl_changepassword <Old Password> <New Password>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 2) {
            MessageChangePassword msg = new MessageChangePassword(args[0], args[1]);
            NetworkLoader.INSTANCE.sendToServer(msg);
        }
        throw new CommandException("Invalid arguments.");
    }
}
