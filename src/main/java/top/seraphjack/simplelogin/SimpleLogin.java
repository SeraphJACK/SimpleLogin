package top.seraphjack.simplelogin;

import net.minecraft.command.arguments.ArgumentSerializer;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.seraphjack.simplelogin.client.ClientLoader;
import top.seraphjack.simplelogin.command.ArgumentTypeEntryName;
import top.seraphjack.simplelogin.network.NetworkLoader;
import top.seraphjack.simplelogin.server.ServerLoader;

import javax.annotation.ParametersAreNonnullByDefault;

@Mod(SLConstants.MODID)
@ParametersAreNonnullByDefault
public class SimpleLogin {
    public static Logger logger = LogManager.getLogger(SLConstants.MODID);

    public SimpleLogin() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientLoader::clientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ServerLoader::serverSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener((FMLCommonSetupEvent e) -> NetworkLoader.registerPackets());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SLConfig.SERVER_SPEC);
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        ArgumentTypes.register("simplelogin:entry", ArgumentTypeEntryName.class,
                new ArgumentSerializer<>(ArgumentTypeEntryName::entryName));
    }
}
