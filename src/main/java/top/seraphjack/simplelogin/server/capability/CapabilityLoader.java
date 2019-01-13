package top.seraphjack.simplelogin.server.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.SERVER)
public class CapabilityLoader {
    @CapabilityInject(IPassword.class)
    public static Capability<IPassword> CAPABILITY_PASSWORD;

    public CapabilityLoader() {
        CapabilityManager.INSTANCE.register(IPassword.class, new CapabilityPassword.Storage(), CapabilityPassword.Implementation::new);
    }
}
