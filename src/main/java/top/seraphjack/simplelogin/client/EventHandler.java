package top.seraphjack.simplelogin.client;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.seraphjack.simplelogin.SLConstants;

@Mod.EventBusSubscriber(modid = SLConstants.MODID, value = Dist.CLIENT)
public final class EventHandler {

    @SubscribeEvent
    public static void onGuiOpen(ScreenEvent.Opening event) {
        if (!(event.getScreen() instanceof SetPasswordScreen) && !PasswordHolder.instance().initialized() && event.getScreen() instanceof JoinMultiplayerScreen) {
            Screen prev = event.getScreen();
            event.setNewScreen(new SetPasswordScreen(prev));
        }
    }
}
