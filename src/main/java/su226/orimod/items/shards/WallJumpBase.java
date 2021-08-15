package su226.orimod.items.shards;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import su226.orimod.Config;
import su226.orimod.Mod;
import su226.orimod.blocks.SpiritSmithingTable;
import su226.orimod.items.Items;
import su226.orimod.messages.WallJumpMessage;
import su226.orimod.others.Util;

public class WallJumpBase extends Shard {
  public static class WallJump extends WallJumpBase {
    public WallJump() {
      super("wall_jump", 1, 2);
      SpiritSmithingTable.registerRecipe(new SpiritSmithingTable.Recipe(
        new ItemStack(net.minecraft.init.Blocks.LADDER),
        new ItemStack(this),
        300
      ));
    }
  }

  public static class Climb extends WallJumpBase {
    public Climb() {
      super("climb", 2, 2);
      SpiritSmithingTable.registerRecipe(new SpiritSmithingTable.Recipe(
        new ItemStack(Items.WALL_JUMP),
        new ItemStack(this),
        300
      ));
    }
    
    @Override
    public void onEquipableUpdate(EntityPlayer owner, boolean isMaxPriority) {
      if (!isMaxPriority) {
        return;
      }
      super.onEquipableUpdate(owner, true);
      if (!owner.isSneaking()) {
        return;
      }
      AxisAlignedBB bb = owner.getEntityBoundingBox();
      boolean clingXp = owner.world.collidesWithAnyBlock(bb.expand(Config.JUMP_AND_CLIMB.WALL_THRESOLD, 0, 0));
      boolean clingXn = owner.world.collidesWithAnyBlock(bb.expand(-Config.JUMP_AND_CLIMB.WALL_THRESOLD, 0, 0));
      boolean clingZp = owner.world.collidesWithAnyBlock(bb.expand(0, 0, Config.JUMP_AND_CLIMB.WALL_THRESOLD));
      boolean clingZn = owner.world.collidesWithAnyBlock(bb.expand(0, 0, -Config.JUMP_AND_CLIMB.WALL_THRESOLD));
      double rad = Math.toRadians(owner.rotationYaw);
      double sin = Math.sin(rad);
      double cos = Math.cos(rad);
      double moveX = (-owner.moveForward * sin + owner.moveStrafing * cos) * Config.JUMP_AND_CLIMB.CLIMB_MULTIPLIER;
      double moveZ = (owner.moveForward * cos + owner.moveStrafing * sin) * Config.JUMP_AND_CLIMB.CLIMB_MULTIPLIER;
      if (clingXp || clingXn || clingZp || clingZn) {
        owner.motionY = 0;
        owner.fallDistance = 0;
      }
      if (clingXp && moveX > 0 && owner.motionY < moveX) {
        owner.motionY = moveX;
      }
      if (clingXn && moveX < 0 && owner.motionY < -moveX) {
        owner.motionY = -moveX;
      }
      if (clingZp && moveZ > 0 && owner.motionY < moveZ) {
        owner.motionY = moveZ;
      }
      if (clingZn && moveZ < 0 && owner.motionY < -moveZ) {
        owner.motionY = -moveZ;
      }
    }
  }

  private int level;

  public WallJumpBase(String name, int level, int maxLevel) {
    super(name, name, level, maxLevel);
    this.level = level;
  }

  @Override
  public String getEquipableType() {
    return "wall_jump_base";
  }

  @Override
  public int getEquipablePriority() {
    return this.level;
  }

  @Override
  public void onEquipableUpdate(EntityPlayer owner, boolean isMaxPriority) {
    if (!isMaxPriority) {
      return;
    }
    boolean cling = owner.world.collidesWithAnyBlock(owner.getEntityBoundingBox().grow(Config.JUMP_AND_CLIMB.WALL_THRESOLD, 0, Config.JUMP_AND_CLIMB.WALL_THRESOLD));
    if (owner.world.isRemote && cling && Util.canAirJump((EntityPlayerSP)owner)) {
      Mod.NETWORK.sendToServer(new WallJumpMessage());
    }
    if (cling && owner.motionY < 0.0) {
      owner.motionY *= Config.JUMP_AND_CLIMB.WALL_VELOCITY_FRACTION;
      owner.fallDistance *= Config.JUMP_AND_CLIMB.WALL_FALL_FRACTION;
    }
  }
}
