package top.seraphjack.simplelogin.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;
import top.seraphjack.simplelogin.SLConfig;
import top.seraphjack.simplelogin.SimpleLogin;
import top.seraphjack.simplelogin.network.MessageRequestLogin;
import top.seraphjack.simplelogin.network.NetworkLoader;
import top.seraphjack.simplelogin.server.storage.Position;
import top.seraphjack.simplelogin.server.storage.SLStorage;

import java.util.Arrays;

@Mod.EventBusSubscriber(value = Side.SERVER, modid = SimpleLogin.MODID)
public class ServerSideEventHandler {

    @SubscribeEvent
    public static void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
            PlayerLoginHandler.instance().addPlayerToLoginList((EntityPlayerMP) event.player);
            NetworkLoader.INSTANCE.sendTo(new MessageRequestLogin(), (EntityPlayerMP) event.player);
        });
    }

    @SubscribeEvent
    public static synchronized void playerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        final String username = event.player.getName();
        final Position pos = new Position(event.player.posX, event.player.posY, event.player.posZ);
        SLStorage.instance().storageProvider.setLastPosition(username, pos);
        try {
            BlockPos spawnPoint = event.player.world.getSpawnPoint();
            event.player.setPosition(spawnPoint.getX(), spawnPoint.getY(), spawnPoint.getZ());
        } catch (Throwable ex) {
            SimpleLogin.logger.error("Fail to process logout.", ex);
        }
    }

    @SubscribeEvent
    public static void onCommand(CommandEvent event) {
        if (Arrays.asList(SLConfig.server.commandNames).contains(event.getCommand().getName())) {
            return;
        }
        if (PlayerLoginHandler.instance().isPlayerInLoginList(event.getSender().getName())) {
            event.setCanceled(true);
            try {
                CommandBase.getCommandSenderAsPlayer(event.getSender()).sendMessage(new TextComponentString("You should login first."));
            } catch (PlayerNotFoundException ignore) {
            }
            SimpleLogin.logger.warn("Player " + event.getSender().getName() + " tried to use command " + event.getCommand().getName() + " before login.");
        }
    }
}
