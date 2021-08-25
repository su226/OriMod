package su226.orimod.others;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Stats {
  public static final Identifier LIGHT_COLLECT = Util.getIdentifier("light_collect");
  public static final Identifier LIGHT_COST = Util.getIdentifier("light_cost");

  public static void register() {
    Registry.register(Registry.CUSTOM_STAT, LIGHT_COLLECT, LIGHT_COLLECT);
    Registry.register(Registry.CUSTOM_STAT, LIGHT_COST, LIGHT_COST);
  }
}
