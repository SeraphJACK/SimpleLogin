package top.seraphjack.simplelogin.server;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import top.seraphjack.simplelogin.SLConfig;
import top.seraphjack.simplelogin.SimpleLogin;
import top.seraphjack.simplelogin.network.MessageRequestLogin;
import top.seraphjack.simplelogin.network.NetworkLoader;
import top.seraphjack.simplelogin.server.capability.CapabilityLoader;
import top.seraphjack.simplelogin.server.capability.IPassword;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

@SideOnly(Side.SERVER)
public class PlayerLoginHandler {
    private static Thread PLAYER_HANDLER_THREAD;
    private static PlayerLoginHandler INSTANCE;

    private boolean alive;
    private ConcurrentLinkedQueue<Login> loginList = new ConcurrentLinkedQueue<>();
    private Set<String> resetPasswordUsers = new HashSet<>();

    private PlayerLoginHandler() {
        PLAYER_HANDLER_THREAD = new Thread(() -> {
            while (alive) {
                try {
                    for (Login login : loginList) {
                        EntityPlayerMP player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(login.name);
                        if (player == null) {
                            SimpleLogin.logger.warn("Can't find player " + login.name + ", ignoring...");
                            loginList.remove(login);
                            continue;
                        }

                        if (System.currentTimeMillis() - login.lastRequested >= 1000) {
                            NetworkLoader.INSTANCE.sendTo(new MessageRequestLogin(), player);
                            login.lastRequested = System.currentTimeMillis();
                        }

                        player.connection.setPlayerLocation(login.posX, login.posY, login.posZ, login.yaw, login.pitch);

                        if (System.currentTimeMillis() - login.time >= SLConfig.server.secs * 1000) {
                            player.connection.disconnect(new TextComponentString("Login timed out."));
                            SimpleLogin.logger.warn("Player " + login.name + " haven't login after a long time.");
                        }
                    }

                    if (loginList.isEmpty()) {
                        Thread.sleep(1000);
                    } else {
                        Thread.sleep(400);
                    }
                } catch (Throwable e) {
                    SimpleLogin.logger.error("Exception caught in PlayerLoginHandler thread", e);
                }
            }
            SimpleLogin.logger.warn("Closing Player Login Handler...");
        }, "Simple-Login-Handler-Thread");
        alive = true;
        PLAYER_HANDLER_THREAD.start();
    }

    public static PlayerLoginHandler instance() {
        if (INSTANCE == null) INSTANCE = new PlayerLoginHandler();
        return INSTANCE;
    }

    @Nullable
    private Login getLoginByName(String name) {
        for (Login l : loginList) {
            if (l.name.equals(name)) {
                return l;
            }
        }
        return null;
    }

    public void login(String id, String pwd) {
        Login login = getLoginByName(id);
        loginList.removeIf((l) -> l.name.equals(id));
        EntityPlayerMP player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(id);
        if (login == null || player == null) {
            SimpleLogin.logger.warn("Invalid login packet from player " + id + ".");
            return;
        }

        IPassword capability = player.getCapability(CapabilityLoader.CAPABILITY_PASSWORD, null);
        if (capability == null) {
            SimpleLogin.logger.warn("Fail to load capability for player " + id + ". Ignoring...");
            return;
        }

        if (pwd.length() >= 100) {
            player.connection.disconnect(new TextComponentString("Password too long."));
            SimpleLogin.logger.warn("Player " + id + " tried to login with a invalid password(too long).");
        } else if (capability.isFirst() || resetPasswordUsers.contains(id)) {
            capability.setFirst(false);
            capability.setPassword(pwd);
            processLogin(login, player);
            resetPasswordUsers.remove(id);
            SimpleLogin.logger.info("Player " + id + " has successfully registered.");
        } else if (capability.getPassword().equals(pwd)) {
            processLogin(login, player);
            SimpleLogin.logger.info("Player " + id + " has successfully logged in.");
        } else {
            SimpleLogin.logger.warn("Player " + id + " tried to login with a wrong password.");
            player.connection.disconnect(new TextComponentString("Wrong Password."));
        }
    }

    private void processLogin(Login login, EntityPlayerMP player) {
        FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
            player.setGameType(GameType.SURVIVAL);
            player.setPosition(login.posX, login.posY, login.posZ);
        });
    }

    void addPlayerToLoginList(EntityPlayerMP player) {
        loginList.add(new Login(player));
        player.setGameType(GameType.SPECTATOR);
    }

    boolean isPlayerInLoginList(String id) {
        return loginList.stream().anyMatch(e -> e.name.equals(id));
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
        double posX, posY, posZ;
        float yaw, pitch;
        long lastRequested;

        Login(EntityPlayerMP player) {
            this.name = player.getGameProfile().getName();
            this.time = System.currentTimeMillis();
            this.posX = player.posX;
            this.posY = player.posY;
            this.posZ = player.posZ;
            this.yaw = player.rotationYaw;
            this.pitch = player.rotationPitch;
            this.lastRequested = System.currentTimeMillis();
        }
    }

    void stop() {
        SimpleLogin.logger.info("Shutting down player login handler...");
        alive = false;
        try {
            PLAYER_HANDLER_THREAD.join();
        } catch (InterruptedException e) {
            SimpleLogin.logger.error("Fail to shutdown login handler. ", e);
        }
    }
}
