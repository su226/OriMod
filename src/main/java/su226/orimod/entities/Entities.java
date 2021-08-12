package su226.orimod.entities;

import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.registries.IForgeRegistry;

public class Entities {
  private static int id = 0;

  public static int nextId() {
    return id++;
  }

  public static void register(IForgeRegistry<EntityEntry> registry) {
    Arrow.register(registry);
    LightBurstEntity.register(registry);
  }

  public static void registerRender() {
    Arrow.registerRender();
    LightBurstEntity.registerRender();
  }
}
