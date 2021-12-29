package top.seraphjack.simplelogin.client;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenOpenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.seraphjack.simplelogin.SLConstants;

@Mod.EventBusSubscriber(modid = SLConstants.MODID, value = Dist.CLIENT)
public final class EventHandler {

    @SubscribeEvent
    public static void onGuiOpen(ScreenOpenEvent event) {
        if (event.getScreen() instanceof JoinMultiplayerScreen && !PasswordHolder.instance().initialized()) {
            Screen prev = event.getScreen();
            event.setScreen(new SetPasswordScreen(prev));
        }
    }
}
