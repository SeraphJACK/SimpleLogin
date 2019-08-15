package top.seraphjack.simplelogin.server.capability;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import top.seraphjack.simplelogin.SimpleLogin;

@SideOnly(Side.SERVER)
@Mod.EventBusSubscriber(value = Side.SERVER, modid = SimpleLogin.MODID)
public class CapabilityLoader {
    @CapabilityInject(ISLEntry.class)
    public static Capability<ISLEntry> CAPABILITY_PASSWORD;

    public CapabilityLoader() {
        CapabilityManager.INSTANCE.register(ISLEntry.class, new CapabilityPassword.Storage(), CapabilityPassword.Implementation::new);
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesEntity(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(new ResourceLocation(SimpleLogin.MODID, "sl_password"),
                    new CapabilityPassword.PlayerProvider());
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
        Capability<ISLEntry> capability = CAPABILITY_PASSWORD;
        Capability.IStorage<ISLEntry> storage = capability.getStorage();

        if (event.getOriginal().hasCapability(capability, null) && event.getEntityPlayer().hasCapability(capability, null)) {
            NBTBase nbt = storage.writeNBT(capability, event.getOriginal().getCapability(capability, null), null);
            storage.readNBT(capability, event.getEntityPlayer().getCapability(capability, null), null, nbt);
        }
    }
}
