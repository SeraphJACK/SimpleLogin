package top.seraphjack.simplelogin.server;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import top.seraphjack.simplelogin.SLConfig;
import top.seraphjack.simplelogin.SLConstants;
import top.seraphjack.simplelogin.SimpleLogin;
import top.seraphjack.simplelogin.network.MessageRequestCommandLogin;
import top.seraphjack.simplelogin.network.MessageRequestLogin;
import top.seraphjack.simplelogin.network.NetworkLoader;
import top.seraphjack.simplelogin.server.capability.CapabilityLoader;
import top.seraphjack.simplelogin.server.storage.Position;
import top.seraphjack.simplelogin.server.storage.SLStorage;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

@SideOnly(Side.SERVER)
public class PlayerLoginHandler {
    private static Thread PLAYER_HANDLER_THREAD;
    private static PlayerLoginHandler INSTANCE;

    private boolean alive;
    private final ConcurrentLinkedQueue<Login> loginList = new ConcurrentLinkedQueue<>();
    private long lastEntriesSaved;

    @SuppressWarnings("BusyWait")
    private PlayerLoginHandler() {
        PLAYER_HANDLER_THREAD = new Thread(() -> {
            lastEntriesSaved = System.currentTimeMillis();
            while (alive) {
                try {
                    for (Login login : loginList) {
                        EntityPlayerMP player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(login.name);
                        if (player == null) {
                            // SimpleLogin.logger.warn("Can't find player " + login.name + ", ignoring...");
                            loginList.remove(login);
                            continue;
                        }

                        // Resend request
                        if (System.currentTimeMillis() - login.lastRequested >= 1000) {
                            if (SLConfig.server.enableCommandLoginMode) {
                                NetworkLoader.INSTANCE.sendTo(new MessageRequestCommandLogin(SLStorage.instance().storageProvider.registered(player.getName())), player);
                            } else {
                                NetworkLoader.INSTANCE.sendTo(new MessageRequestLogin(), player);
                            }
                            login.lastRequested = System.currentTimeMillis();
                        }

                        // Block players movement before authentication
                        FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() ->
                                player.connection.setPlayerLocation(login.posX, login.posY, login.posZ, login.yaw, login.pitch)
                        );

                        // Kick timed out players
                        if (System.currentTimeMillis() - login.time >= SLConfig.server.secs * 1000) {
                            player.connection.disconnect(new TextComponentString("Login timed out."));
                            loginList.removeIf(i -> i.name.equals(player.getGameProfile().getName()));
                            SimpleLogin.logger.warn("Player " + login.name + " haven't login after a long time.");
                            loginList.remove(login);
                        }
                    }

                    // Auto save entries every 5 minutes
                    if (System.currentTimeMillis() - lastEntriesSaved >= 5 * 60 * 1000) {
                        lastEntriesSaved = System.currentTimeMillis();
                        if (SLStorage.instance().storageProvider.dirty()) {
                            SimpleLogin.logger.info("Auto saving entries");
                            long start = System.currentTimeMillis();
                            try {
                                SLStorage.instance().storageProvider.save();
                                SimpleLogin.logger.info("Done! Took " + (System.currentTimeMillis() - start) + "ms.");
                            } catch (IOException ex) {
                                SimpleLogin.logger.error("Failed to auto save entries", ex);
                            }
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
        return loginList.stream().filter(l -> l.name.equals(name)).findAny().orElse(null);
    }

    public void login(String id, String pwd, boolean allowMistakenPassword) {
        Login login = getLoginByName(id);
        EntityPlayerMP player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(id);

        if (login == null || player == null) {
            return;
        }

        if (System.currentTimeMillis() - login.lastLoginTrial < 1000) {
            player.sendMessage(new TextComponentString("Login trial too frequent"));
        } else if (pwd.length() >= 100) {
            loginList.remove(login);
            player.connection.disconnect(new TextComponentString("Password too long."));
            SimpleLogin.logger.warn("Player " + id + " tried to login with a invalid password(too long).");
        } else if (!SLStorage.instance().storageProvider.registered(id)) {
            SLStorage.instance().storageProvider.register(id, pwd);
            loginList.remove(login);
            afterPlayerLogin(login, player);
            SimpleLogin.logger.info("Player " + id + " has successfully registered.");
        } else if (SLStorage.instance().storageProvider.checkPassword(id, pwd)) {
            loginList.remove(login);
            afterPlayerLogin(login, player);
            SimpleLogin.logger.info("Player " + id + " has successfully logged in.");
        } else {
            SimpleLogin.logger.warn("Player " + id + " tried to login with a wrong password.");
            if (allowMistakenPassword) {
                login.lastLoginTrial = System.currentTimeMillis();
                player.sendMessage(new TextComponentString("Password not correct, please wait for at least 1 second before trying again"));
            } else {
                loginList.remove(login);
                player.connection.disconnect(new TextComponentString("Wrong Password."));
            }
        }
    }

    public void playerJoin(EntityPlayerMP player) {
        loginList.add(new Login(player));
        player.setGameType(GameType.SPECTATOR);
    }

    public void playerLeave(EntityPlayerMP player) {
        final String username = player.getName();
        final Position pos = new Position(player.posX, player.posY, player.posZ);

        // Save player position in storage
        if (!isPlayerInLoginList(username)) {
            Objects.requireNonNull(player.getCapability(CapabilityLoader.CAPABILITY_LAST_POS, null))
                    .setLastPos(pos);
        }

        // Teleport player to spawn point
        try {
            if (player.getRidingEntity() == null && SLConfig.server.protectPlayerCoordinate) {
                BlockPos spawnPoint = player.world.getSpawnPoint();
                player.setPosition(spawnPoint.getX(), spawnPoint.getY(), spawnPoint.getZ());
            }
        } catch (Exception ex) {
            SimpleLogin.logger.error("Fail to set player position to spawn point when logging out.", ex);
        }
    }

    public boolean isPlayerInLoginList(String id) {
        return loginList.stream().anyMatch(e -> e.name.equals(id));
    }

    private void afterPlayerLogin(Login login, EntityPlayerMP player) {
        FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
            player.setGameType(SLStorage.instance().storageProvider.gameType(login.name));
            Position lastPos = Objects.requireNonNull(player.getCapability(CapabilityLoader.CAPABILITY_LAST_POS, null))
                    .getLastPos();

            if (lastPos.equals(SLConstants.defaultPosition)) {
                player.setPosition(login.posX, login.posY, login.posZ);
            } else if (SLConfig.server.protectPlayerCoordinate) {
                player.setPositionAndUpdate(lastPos.getX(), lastPos.getY(), lastPos.getZ());
            }
        });
    }

    private static class Login {
        String name;
        long time;
        double posX, posY, posZ;
        float yaw, pitch;
        long lastRequested, lastLoginTrial;

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
