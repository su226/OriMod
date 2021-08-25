package su226.orimod.blocks;

import net.minecraft.util.registry.Registry;
import su226.orimod.others.Util;

public class Blocks {
  public static final SpiritSmithingTable SPIRIT_SMITHING_TABLE = new SpiritSmithingTable();
  
  public static void register() {
    Registry.register(Registry.BLOCK, Util.getIdentifier("spirit_smithing_table"), SPIRIT_SMITHING_TABLE);
  }
}
