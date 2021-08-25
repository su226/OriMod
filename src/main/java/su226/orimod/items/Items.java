package su226.orimod.items;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item.Settings;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;
import su226.orimod.blocks.Blocks;
import su226.orimod.items.shards.MultiJump;
import su226.orimod.items.shards.Stomp;
import su226.orimod.items.shards.WallJumpBase;
import su226.orimod.others.Util;

public class Items {
  public static final Icon ICON = new Icon();
  public static final ItemGroup GROUP = FabricItemGroupBuilder.build(Util.getIdentifier("orimod"), () -> new ItemStack(ICON));

  public static final Bash BASH = new Bash();
  public static final KurosFeather KUROS_FEATHER = new KurosFeather();
  public static final LightBurst LIGHT_BURST = new LightBurst();
  public static final SpiritArc SPIRIT_ARC = new SpiritArc();
  public static final SpiritEdge SPIRIT_EDGE = new SpiritEdge();
  public static final SpiritFlame SPIRIT_FLAME = new SpiritFlame();

  public static final MultiJump.Double DOUBLE_JUMP = new MultiJump.Double();
  public static final MultiJump.Triple TRIPLE_JUMP = new MultiJump.Triple();
  public static final Stomp STOMP = new Stomp();
  public static final WallJumpBase.WallJump WALL_JUMP = new WallJumpBase.WallJump();
  public static final WallJumpBase.Climb CLIMB = new WallJumpBase.Climb();

  public static final BlockItem SPIRIT_SMITHING_TABLE = new BlockItem(Blocks.SPIRIT_SMITHING_TABLE, new Settings().group(GROUP));

  public static void register() {
    Registry.register(Registry.ITEM, Util.getIdentifier("icon"), ICON);
    Registry.register(Registry.ITEM, Util.getIdentifier("bash"), BASH);
    Registry.register(Registry.ITEM, Util.getIdentifier("kuros_feather"), KUROS_FEATHER);
    Registry.register(Registry.ITEM, Util.getIdentifier("light_burst"), LIGHT_BURST);
    Registry.register(Registry.ITEM, Util.getIdentifier("spirit_arc"), SPIRIT_ARC);
    Registry.register(Registry.ITEM, Util.getIdentifier("spirit_edge"), SPIRIT_EDGE);
    Registry.register(Registry.ITEM, Util.getIdentifier("spirit_flame"), SPIRIT_FLAME);
    Registry.register(Registry.ITEM, Util.getIdentifier("shard_double_jump"), DOUBLE_JUMP);
    Registry.register(Registry.ITEM, Util.getIdentifier("shard_triple_jump"), TRIPLE_JUMP);
    Registry.register(Registry.ITEM, Util.getIdentifier("shard_stomp"), STOMP);
    Registry.register(Registry.ITEM, Util.getIdentifier("shard_wall_jump"), WALL_JUMP);
    Registry.register(Registry.ITEM, Util.getIdentifier("shard_climb"), CLIMB);
    Registry.register(Registry.ITEM, Util.getIdentifier("spirit_smithing_table"), SPIRIT_SMITHING_TABLE);
  }

  public static void registerModelPredicate() {
    SPIRIT_ARC.registerModelPredicate();
  }
}
