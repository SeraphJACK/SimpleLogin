package top.seraphjack.simplelogin.server;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import top.seraphjack.simplelogin.SLConfig;
import top.seraphjack.simplelogin.server.capability.CapabilityLoader;
import top.seraphjack.simplelogin.server.capability.IPassword;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

@SideOnly(Side.SERVER)
public class PlayerLoginHandler {
    private static Thread PLAYER_HANDLER_THREAD;
    private static PlayerLoginHandler INSTANCE;

    private boolean alive;
    private ConcurrentLinkedQueue<Runnable> tasks = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Login> loginList = new ConcurrentLinkedQueue<>();
    private Set<String> resetPasswordUsers = new HashSet<>();

    private PlayerLoginHandler() {
        PLAYER_HANDLER_THREAD = new Thread(() -> {
            while (alive) {
                while (!tasks.isEmpty()) {
                    tasks.poll().run();
                }

                for (Login login : loginList) {
                    EntityPlayerMP player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(login.name);
                    if (player == null) {
                        loginList.remove(login);
                        return;
                    }

                    if (System.currentTimeMillis() - login.time >= SLConfig.server.secs * 1000) {
                        player.connection.disconnect(new TextComponentTranslation("Login timed out."));
                    }
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignore) {

                }
            }
        });
        alive = true;
        PLAYER_HANDLER_THREAD.start();
    }

    public static PlayerLoginHandler instance() {
        if (INSTANCE == null) INSTANCE = new PlayerLoginHandler();
        return INSTANCE;
    }

    public void login(String id, String pwd) {
        loginList.removeIf((l) -> l.name.equals(id));
        EntityPlayerMP player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(id);
        if (player == null) {
            System.out.println("Player " + id + " is null!");
            return;
        }

        IPassword capability = player.getCapability(CapabilityLoader.CAPABILITY_PASSWORD, null);
        if (capability == null) {
            System.out.println("Player " + id + "'s capability is null!");
            return;
        }

        if (pwd.length() >= 100) {
            player.connection.disconnect(new TextComponentTranslation("Password too long."));
        } else if (capability.isFirst() || resetPasswordUsers.contains(id)) {
            capability.setFirst(false);
            capability.setPassword(pwd);
            setPlayerToSurvivalMode(player);
            resetPasswordUsers.remove(id);
            System.out.println("Player " + id + " has successfully registered.");
        } else if (capability.getPassword().equals(pwd)) {
            setPlayerToSurvivalMode(player);
            System.out.println("Player " + id + " has successfully logged in.");
        } else {
            player.connection.disconnect(new TextComponentTranslation("Wrong Password."));
        }
    }

    private void setPlayerToSurvivalMode(EntityPlayerMP player) {
        FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> player.setGameType(GameType.SURVIVAL));
    }

    void addPlayerToLoginList(EntityPlayerMP player) {
        loginList.add(new Login(player.getGameProfile().getName()));
        player.setGameType(GameType.SPECTATOR);
    }

    void resetPassword(String id) {
        resetPasswordUsers.add(id);
    }

    String getResetPasswordUsers() {
        StringBuilder ret = new StringBuilder();
        resetPasswordUsers.stream().map(i -> i + "\n").forEach(ret::append);
        return ret.toString();
    }

    private static class Login {
        String name;
        long time;

        Login(String name) {
            this.name = name;
            this.time = System.currentTimeMillis();
        }
    }

    void stop() {
        alive = false;
        try {
            PLAYER_HANDLER_THREAD.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
