package top.seraphjack.simplelogin.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import top.seraphjack.simplelogin.server.PlayerLoginHandler;

public class SLCommand extends CommandBase {
    @Override
    public String getName() {
        return "simplelogin";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/simplelogin reset <PlayerName>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 0) {
            throw(new WrongUsageException("/simplelogin reset <PlayerName>"));
        }
        else {
            switch(args[0]){
                case "reset":{
                    PlayerLoginHandler.instance().resetPassword(args[1]);
                    sender.sendMessage(new TextComponentString("Player " + args[1] + " has been added to resetPassword list."));
                    break;
                }
                case "list": {
                    sender.sendMessage(new TextComponentString(PlayerLoginHandler.instance().getResetPasswordUsers()));
                    break;
                }
                default:{
                    throw(new WrongUsageException("/simplelogin reset <PlayerName>"));
                }
            }
        }
    }
}
