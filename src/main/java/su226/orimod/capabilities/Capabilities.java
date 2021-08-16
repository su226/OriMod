package su226.orimod.capabilities;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class Capabilities {
  @CapabilityInject(IChargeable.class)
  public static Capability<IChargeable> CHARGEABLE;
  @CapabilityInject(ICooldown.class)
  public static Capability<ICooldown> COOLDOWN;
  @CapabilityInject(IEquipper.class)
  public static Capability<IEquipper> EQUIPPER;
  @CapabilityInject(IHasEntity.class)
  public static Capability<IHasEntity> HAS_ENTITY;
  @CapabilityInject(IKurosFeather.class)
  public static Capability<IKurosFeather> KUROS_FEATHER;
  @CapabilityInject(ISpiritLight.class)
  public static Capability<ISpiritLight> SPIRIT_LIGHT;

  public static void register() {
    IChargeable.register();
    ICooldown.register();
    IEquipper.register();
    IHasEntity.register();
    IKurosFeather.register();
    ISpiritLight.register();
  }
}
