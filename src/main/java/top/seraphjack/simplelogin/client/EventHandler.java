package top.seraphjack.simplelogin.client;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.seraphjack.simplelogin.SLConstants;

@Mod.EventBusSubscriber(modid = SLConstants.MODID, value = Dist.CLIENT)
public final class EventHandler {

    @SubscribeEvent
    public static void onGuiOpen(GuiOpenEvent event) {
        if (event.getGui() instanceof JoinMultiplayerScreen && !PasswordHolder.instance().initialized()) {
            Screen prev = event.getGui();
            event.setGui(new SetPasswordScreen(prev));
        }
    }
}
