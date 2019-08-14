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
import top.seraphjack.simplelogin.server.storage.Position;
import top.seraphjack.simplelogin.server.storage.SLStorage;

import javax.annotation.Nullable;
import java.util.concurrent.ConcurrentLinkedQueue;

@SideOnly(Side.SERVER)
public class PlayerLoginHandler {
    private static Thread PLAYER_HANDLER_THREAD;
    private static PlayerLoginHandler INSTANCE;

    private boolean alive;
    private ConcurrentLinkedQueue<Login> loginList = new ConcurrentLinkedQueue<>();
    private long lastEntriesSaved;

    private PlayerLoginHandler() {
        PLAYER_HANDLER_THREAD = new Thread(() -> {
            lastEntriesSaved = System.currentTimeMillis();
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
                            loginList.removeIf(i -> i.name.equals(player.getGameProfile().getName()));
                            SimpleLogin.logger.warn("Player " + login.name + " haven't login after a long time.");
                            loginList.remove(login);
                        }
                    }

                    if (System.currentTimeMillis() - lastEntriesSaved >= 5 * 60 * 1000) {
                        lastEntriesSaved = System.currentTimeMillis();
                        if (SLStorage.instance().storageProvider.dirty()) {
                            SimpleLogin.logger.info("Auto saving entries");
                            long start = System.currentTimeMillis();
                            synchronized (SLStorage.instance().storageProvider) {
                                SLStorage.instance().storageProvider.save();
                            }
                            SimpleLogin.logger.info("Done! Took " + (System.currentTimeMillis() - start) + "ms.");
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
        loginList.remove(login);
        EntityPlayerMP player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(id);

        if (login == null || player == null) {
            return;
        }

        if (pwd.length() >= 100) {
            player.connection.disconnect(new TextComponentString("Password too long."));
            SimpleLogin.logger.warn("Player " + id + " tried to login with a invalid password(too long).");
        } else if (!SLStorage.instance().storageProvider.registered(id)) {
            SLStorage.instance().storageProvider.register(id, pwd);
            afterPlayerLogin(login, player);
            SimpleLogin.logger.info("Player " + id + " has successfully registered.");
        } else if (SLStorage.instance().storageProvider.checkPassword(id, pwd)) {
            afterPlayerLogin(login, player);
            SimpleLogin.logger.info("Player " + id + " has successfully logged in.");
        } else {
            SimpleLogin.logger.warn("Player " + id + " tried to login with a wrong password.");
            player.connection.disconnect(new TextComponentString("Wrong Password."));
        }
    }

    private void afterPlayerLogin(Login login, EntityPlayerMP player) {
        player.setGameType(SLStorage.instance().storageProvider.gameType(login.name));
        Position lastPos = SLStorage.instance().storageProvider.getLastPosition(login.name);
        player.setPosition(lastPos.getX(), lastPos.getY(), lastPos.getZ());
    }

    public void addPlayerToLoginList(EntityPlayerMP player) {
        loginList.add(new Login(player));
        player.setGameType(GameType.SPECTATOR);
    }

    public boolean isPlayerInLoginList(String id) {
        return loginList.stream().anyMatch(e -> e.name.equals(id));
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
        SimpleLogin.logger.info("Shutting down player login handler");
        alive = false;
        try {
            PLAYER_HANDLER_THREAD.join();
        } catch (InterruptedException e) {
            SimpleLogin.logger.error("Fail to shutdown login handler", e);
        }
    }
}
