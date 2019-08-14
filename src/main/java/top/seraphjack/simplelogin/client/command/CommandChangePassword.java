package top.seraphjack.simplelogin.client.command;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import top.seraphjack.simplelogin.SLConfig;
import top.seraphjack.simplelogin.network.MessageChangePassword;
import top.seraphjack.simplelogin.network.NetworkLoader;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

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
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length <= 1) {
            return getListOfStringsMatchingLastWord(args, SLConfig.client.password);
        }
        return super.getTabCompletions(server, sender, args, targetPos);
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length == 2) {
            MessageChangePassword msg = new MessageChangePassword(args[0], args[1]);
            NetworkLoader.INSTANCE.sendToServer(msg);
        } else {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
        }
    }
}
