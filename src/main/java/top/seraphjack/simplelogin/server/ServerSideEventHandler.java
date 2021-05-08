package top.seraphjack.simplelogin.server;

import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.network.PacketDistributor;
import top.seraphjack.simplelogin.SLConfig;
import top.seraphjack.simplelogin.SLConstants;
import top.seraphjack.simplelogin.SimpleLogin;
import top.seraphjack.simplelogin.network.MessageRequestLogin;
import top.seraphjack.simplelogin.network.NetworkLoader;
import top.seraphjack.simplelogin.server.handler.PlayerLoginHandler;

import java.lang.reflect.Field;

@OnlyIn(Dist.DEDICATED_SERVER)
@Mod.EventBusSubscriber(value = Dist.DEDICATED_SERVER, modid = SLConstants.MODID)
public class ServerSideEventHandler {
    @SubscribeEvent
    public static void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerLoginHandler.instance().playerJoin((ServerPlayerEntity) event.getPlayer());
        // noinspection InstantiationOfUtilityClass
        NetworkLoader.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getPlayer()), new MessageRequestLogin());
    }

    @SubscribeEvent
    public static void playerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        PlayerLoginHandler.instance().playerLeave((ServerPlayerEntity) event.getEntity());
    }

    // Block command usage from unauthenticated players
    @SubscribeEvent
    public static void onCommand(CommandEvent event) {
        String command = event.getParseResults().getReader().getString();
        if (command.startsWith("/")) command = command.substring(1);
        SimpleLogin.logger.debug("Checking command '{}'", command);
        ICommandSource realSource = getRealSource(event.getParseResults().getContext().getSource());
        if (realSource == null) {
            return;
        }
        if (!(realSource instanceof ServerPlayerEntity)) {
            return;
        }
        ServerPlayerEntity cast = (ServerPlayerEntity) realSource;
        if (PlayerLoginHandler.instance().hasPlayerLoggedIn(cast.getScoreboardName())) {
            return;
        }
        if (SLConfig.SERVER.whitelistCommands.get().contains(command)) {
            return;
        }
        SimpleLogin.logger.debug("Denied {} to execute command '{}' before login", event.getParseResults().getContext().getSource().getName(), command);
        event.setCanceled(true);
    }

    private static ICommandSource getRealSource(CommandSource source) {
        try {
            Field sourceField = ObfuscationReflectionHelper.findField(CommandSource.class, "field_197041_c");
            sourceField.setAccessible(true);
            return (ICommandSource) sourceField.get(source);
        } catch (Exception e) {
            SimpleLogin.logger.error("Failed to get real source", e);
            return ICommandSource.DUMMY;
        }
    }
}
