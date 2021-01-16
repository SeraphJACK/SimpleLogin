package top.seraphjack.simplelogin.server;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.GameType;
import net.minecraft.world.storage.IWorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import top.seraphjack.simplelogin.SLConfig;
import top.seraphjack.simplelogin.SLConstants;
import top.seraphjack.simplelogin.SimpleLogin;
import top.seraphjack.simplelogin.network.MessageRequestLogin;
import top.seraphjack.simplelogin.network.NetworkLoader;
import top.seraphjack.simplelogin.server.capability.CapabilityLoader;
import top.seraphjack.simplelogin.server.storage.Position;
import top.seraphjack.simplelogin.server.storage.SLStorage;

import javax.annotation.Nullable;
import java.util.concurrent.ConcurrentLinkedQueue;

@SuppressWarnings({"BusyWait", "InstantiationOfUtilityClass"})
@OnlyIn(Dist.DEDICATED_SERVER)
public final class PlayerLoginHandler {
    private static Thread PLAYER_HANDLER_THREAD;
    private static PlayerLoginHandler INSTANCE;

    private boolean alive;
    private final ConcurrentLinkedQueue<Login> loginList = new ConcurrentLinkedQueue<>();
    private long lastEntriesSaved;

    private PlayerLoginHandler() {
        PLAYER_HANDLER_THREAD = new Thread(() -> {
            lastEntriesSaved = System.currentTimeMillis();
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            while (alive) {
                try {
                    for (Login login : loginList) {
                        ServerPlayerEntity player = server.getPlayerList().getPlayerByUsername(login.name);
                        if (player == null) {
                            SimpleLogin.logger.debug("Can't find player " + login.name + ", ignoring...");
                            loginList.remove(login);
                            continue;
                        }

                        // Resend request
                        if (System.currentTimeMillis() - login.lastRequested >= 1000) {
                            SimpleLogin.logger.debug("Resending login request to {}...", login.name);
                            NetworkLoader.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new MessageRequestLogin());
                            login.lastRequested = System.currentTimeMillis();
                        }

                        // Block players movement before authentication
                        server.deferTask(() ->
                                player.connection.setPlayerLocation(login.posX, login.posY, login.posZ, login.yaw, login.pitch)
                        );

                        // Kick timed out players
                        if (System.currentTimeMillis() - login.time >= SLConfig.SERVER.secs.get() * 1000) {
                            player.connection.disconnect(new StringTextComponent("Login timed out."));
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
                            SLStorage.instance().storageProvider.save();
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
        return loginList.stream().filter(l -> l.name.equals(name)).findAny().orElse(null);
    }

    public void login(String id, String pwd) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        Login login = getLoginByName(id);
        loginList.remove(login);
        ServerPlayerEntity player = server.getPlayerList().getPlayerByUsername(id);

        if (login == null || player == null) {
            return;
        }

        if (pwd.length() >= 100) {
            player.connection.disconnect(new StringTextComponent("Password too long."));
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
            player.connection.disconnect(new StringTextComponent("Wrong Password."));
        }
    }

    public void playerJoin(ServerPlayerEntity player) {
        loginList.add(new Login(player));
        player.setGameType(GameType.SPECTATOR);
    }

    public void playerLeave(ServerPlayerEntity player) {
        final String username = player.getGameProfile().getName();
        final Position pos = new Position(player.getPosX(), player.getPosY(), player.getPosZ());

        // Save player position in storage
        if (!isPlayerInLoginList(username)) {
            player.getCapability(CapabilityLoader.CAPABILITY_LAST_POS).ifPresent(c -> c.setLastPos(pos));
        }

        // Teleport player to spawn point
        if (SLConfig.SERVER.protectPlayerCoord.get()) {
            try {
                IWorldInfo info = player.getServerWorld().getWorldInfo();
                player.setPosition(info.getSpawnX(), info.getSpawnY(), info.getSpawnZ());
            } catch (Exception ex) {
                SimpleLogin.logger.error("Fail to set player position to spawn point when logging out.", ex);
            }
        }
    }

    public boolean isPlayerInLoginList(String id) {
        return loginList.stream().anyMatch(e -> e.name.equals(id));
    }

    private void afterPlayerLogin(Login login, ServerPlayerEntity player) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        server.deferTask(() -> {
            player.setGameType(SLStorage.instance().storageProvider.gameType(login.name));
            if (SLConfig.SERVER.protectPlayerCoord.get()) {
                Position lastPos = player.getCapability(CapabilityLoader.CAPABILITY_LAST_POS).orElseThrow(RuntimeException::new).getLastPos();

                if (lastPos.equals(SLConstants.defaultPosition)) {
                    player.setPosition(login.posX, login.posY, login.posZ);
                } else {
                    player.setPositionAndUpdate(lastPos.getX(), lastPos.getY(), lastPos.getZ());
                }
            }
        });
    }

    private static class Login {
        String name;
        long time;
        double posX, posY, posZ;
        float yaw, pitch;
        long lastRequested;

        Login(ServerPlayerEntity player) {
            this.name = player.getGameProfile().getName();
            this.time = System.currentTimeMillis();
            this.posX = player.getPosX();
            this.posY = player.getPosY();
            this.posZ = player.getPosZ();
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
