package top.seraphjack.simplelogin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import top.seraphjack.simplelogin.SimpleLogin;

@SideOnly(Side.CLIENT)
public class Notifier {
    private static Notifier INSTANCE;

    private Thread NOTIFIER_THREAD;
    private boolean alive;
    /**
     * 0: no notification
     * 1: notify register
     * 2: notify login
     */
    private short notifyState;

    private Notifier() {
        NOTIFIER_THREAD = new Thread(() -> {
            while (alive) {
                try {
                    EntityPlayerSP player = Minecraft.getMinecraft().player;
                    if (player != null) {
                        switch (notifyState) {
                            case 1: {
                                player.sendMessage(new TextComponentTranslation("simplelogin_notify_register"));
                                break;
                            }
                            case 2: {
                                player.sendMessage(new TextComponentTranslation("simplelogin_notify_login"));
                                break;
                            }
                        }
                    }
                    Thread.sleep(2000);
                } catch (Exception ignore) {

                }
            }
        }, "simplelogin-notifier-thread");
        alive = true;
        NOTIFIER_THREAD.start();
    }

    public static Notifier instance() {
        if (INSTANCE == null) {
            INSTANCE = new Notifier();
        }
        return INSTANCE;
    }

    public void notifyRegister() {
        notifyState = 1;
    }

    public void notifyLogin() {
        notifyState = 2;
    }

    public void clearNotify() {
        notifyState = 0;
    }

    public void stop() {
        alive = false;
        try {
            NOTIFIER_THREAD.join();
        } catch (InterruptedException e) {
            SimpleLogin.logger.error("Failed to stop notifier", e);
        }
        INSTANCE = null;
    }
}
