package top.seraphjack.simplelogin.server.capability;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.seraphjack.simplelogin.SLConstants;

@OnlyIn(Dist.DEDICATED_SERVER)
@Mod.EventBusSubscriber(value = Dist.DEDICATED_SERVER, modid = SLConstants.MODID)
public class CapabilityLoader {
    @CapabilityInject(ILastPos.class)
    public static Capability<ILastPos> CAPABILITY_LAST_POS;

    private CapabilityLoader() {
        throw new UnsupportedOperationException("No instance.");
    }

    public static void registerCapabilities() {
        CapabilityManager.INSTANCE.register(ILastPos.class, new CapabilityLastPos.Storage(), CapabilityLastPos.Implementation::new);
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesEntity(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity) {
            event.addCapability(new ResourceLocation(SLConstants.MODID, "sl_last_pos"),
                    new CapabilityLastPos.Provider());
        }
    }


    @SubscribeEvent
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void onPlayerClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) throws Throwable {
        Capability[] capabilities = new Capability[]{CAPABILITY_LAST_POS};
        for (Capability capability : capabilities) {
            Capability.IStorage storage = capability.getStorage();

            if (event.getOriginal().getCapability(capability, null).isPresent() && event.getPlayer().getCapability(capability, null).isPresent()) {
                INBT nbt = storage.writeNBT(capability, event.getOriginal().getCapability(capability, null).orElseThrow(RuntimeException::new), null);
                storage.readNBT(capability, event.getPlayer().getCapability(capability, null).orElseThrow(RuntimeException::new), null, nbt);
            }
        }
    }
}
