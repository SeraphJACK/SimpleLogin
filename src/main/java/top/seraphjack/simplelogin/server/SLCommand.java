package top.seraphjack.simplelogin.server;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;
import top.seraphjack.simplelogin.server.storage.SLStorage;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SLCommand extends CommandBase {
    @Override
    public String getName() {
        return "simplelogin";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/simplelogin unregister <PlayerName>\n" +
                "/simplelogin save";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) throw new WrongUsageException(getUsage(sender));
        switch (args[0]) {
            case "unregister": {
                if (args.length != 2) throw new WrongUsageException(getUsage(sender));
                PlayerLoginHandler.instance().resetPassword(args[1]);
                sender.sendMessage(new TextComponentString("Player " + args[1] + " has been unregistered."));
                break;
            }
            case "save": {
                sender.sendMessage(new TextComponentString("Saving all entries..."));
                try {
                    long start = System.currentTimeMillis();
                    SLStorage.storageProvider.save();
                    sender.sendMessage(new TextComponentString("Done! Took " + (System.currentTimeMillis() - start) + "ms."));
                } catch (IOException e) {
                    sender.sendMessage(new TextComponentString("Unable to save! Check server log for details!"));
                }
                break;
            }
            default: {
                throw new WrongUsageException(getUsage(sender));
            }
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args[0].equals("unregister")) {
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        } else if (args.length <= 1) {
            return getListOfStringsMatchingLastWord(args, Arrays.asList("unregister", "save"));
        }
        return super.getTabCompletions(server, sender, args, targetPos);
    }
}
