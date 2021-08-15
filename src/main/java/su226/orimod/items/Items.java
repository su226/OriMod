package su226.orimod.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.IForgeRegistry;
import su226.orimod.items.shards.MultiJump;
import su226.orimod.items.shards.WallJumpBase.Climb;
import su226.orimod.items.shards.WallJumpBase.WallJump;

public class Items {
  public static final CreativeTabs CREATIVE_TAB = new CreativeTabs("orimod") {
    @Override
    public ItemStack getTabIconItem() {
      return new ItemStack(ICON);
    }
  };

  public static ItemBlock SPIRIT_SMITHING_TABLE;

  public static final Bash BASH = new Bash();
  public static final Icon ICON = new Icon();
  public static final KurosFeather KUROS_FEATHER = new KurosFeather();
  public static final LightBurst LIGHT_BURST = new LightBurst();
  public static final SpiritArc SPIRIT_ARC = new SpiritArc();
  public static final SpiritEdge SPIRIT_EDGE = new SpiritEdge();
  public static final SpiritFlame SPIRIT_FLAME = new SpiritFlame();

  public static final MultiJump.Double DOUBLE_JUMP = new MultiJump.Double();
  public static final MultiJump.Triple TRIPLE_JUMP = new MultiJump.Triple();
  public static final WallJump WALL_JUMP = new WallJump();
  public static final Climb CLIMB = new Climb();

  public static void register(IForgeRegistry<Item> registry) {
    registry.registerAll(
      SPIRIT_SMITHING_TABLE,
      BASH,
      ICON,
      KUROS_FEATHER,
      LIGHT_BURST,
      SPIRIT_ARC,
      SPIRIT_EDGE,
      SPIRIT_FLAME,
      DOUBLE_JUMP,
      TRIPLE_JUMP,
      WALL_JUMP,
      CLIMB
    );
  }
}
