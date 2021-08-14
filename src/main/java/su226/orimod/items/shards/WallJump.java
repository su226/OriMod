package su226.orimod.items.shards;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import su226.orimod.Config;
import su226.orimod.Mod;
import su226.orimod.blocks.SpiritSmithingTable;
import su226.orimod.messages.WallJumpMessage;
import su226.orimod.others.Util;

public class WallJump extends Shard {
  public WallJump() {
    super("wall_jump", "wall_jump", 1, 2);
    SpiritSmithingTable.registerRecipe(new SpiritSmithingTable.Recipe(
      new ItemStack(net.minecraft.init.Blocks.LADDER),
      new ItemStack(this),
      300
    ));
  }

  @Override
  public void onEquipableUpdate(EntityPlayer owner, boolean isMaxPriority) {
    if (!isMaxPriority) {
      return;
    }
    boolean cling = owner.world.collidesWithAnyBlock(owner.getEntityBoundingBox().grow(0.01, 0, 0.01));
    if (owner.world.isRemote && cling && Util.canAirJump((EntityPlayerSP)owner)) {
      Mod.NETWORK.sendToServer(new WallJumpMessage());
    }
    if (cling && owner.motionY < 0.0) {
      owner.motionY *= Config.WALL_JUMP.VELOCITY_MULTIPLIER;
      owner.fallDistance *= Config.WALL_JUMP.FALL_MULTIPLIER;
    }
  }
}
