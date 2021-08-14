package su226.orimod.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.registries.IForgeRegistry;
import su226.orimod.items.Items;

public class Blocks {
  public static final SpiritSmithingTable SPIRIT_SMITHING_TABLE = new SpiritSmithingTable();
  
  static {
    Items.SPIRIT_SMITHING_TABLE = createItemBlock(Blocks.SPIRIT_SMITHING_TABLE);
  }

  private static ItemBlock createItemBlock(Block block) {
    ItemBlock ret = new ItemBlock(block);
    ret.setRegistryName(block.getRegistryName());
    return ret;
  }

  public static void register(IForgeRegistry<Block> registry) {
    registry.registerAll(
      SPIRIT_SMITHING_TABLE
    );
  }
}
