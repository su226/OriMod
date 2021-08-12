package su226.orimod.capabilities;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class Capabilities {
  @CapabilityInject(IChargeable.class)
  public static Capability<IChargeable> CHARGEABLE;
  @CapabilityInject(IEquipper.class)
  public static Capability<IEquipper> EQUIPPER;
  @CapabilityInject(IHasEntity.class)
  public static Capability<IHasEntity> HAS_ENTITY;
  @CapabilityInject(IKurosFeather.class)
  public static Capability<IKurosFeather> KUROS_FEATHER;
  @CapabilityInject(IMultiJump.class)
  public static Capability<IMultiJump> MULTI_JUMP;

  public static void register() {
    IChargeable.register();
    IEquipper.register();
    IHasEntity.register();
    IKurosFeather.register();
    IMultiJump.register();
  }
}
