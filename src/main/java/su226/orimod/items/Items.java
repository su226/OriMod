package su226.orimod.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.IForgeRegistry;

public class Items {
  public static final CreativeTabs CREATIVE_TAB = new CreativeTabs("orimod") {
    @Override
    public ItemStack getTabIconItem() {
      return new ItemStack(ICON);
    }
  };

  public static final Bash BASH = new Bash();
  public static final MultiJump.Double DOUBLE_JUMP = new MultiJump.Double();
  public static final MultiJump.Triple TRIPLE_JUMP = new MultiJump.Triple();
  public static final Icon ICON = new Icon();
  public static final KurosFeather KUROS_FEATHER = new KurosFeather();
  public static final LightBurst LIGHT_BURST = new LightBurst();
  public static final SpiritArc SPIRIT_ARC = new SpiritArc();
  public static final SpiritEdge SPIRIT_EDGE = new SpiritEdge();
  public static final SpiritFlame SPIRIT_FLAME = new SpiritFlame();

  public static void register(IForgeRegistry<Item> registry) {
    registry.registerAll(
      BASH,
      ICON,
      DOUBLE_JUMP,
      TRIPLE_JUMP,
      KUROS_FEATHER,
      LIGHT_BURST,
      SPIRIT_ARC,
      SPIRIT_EDGE,
      SPIRIT_FLAME
    );
  }
}
