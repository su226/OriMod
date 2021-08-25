package su226.orimod.items.shards;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import su226.orimod.Mod;
import su226.orimod.blocks.SpiritSmithingTable;
import su226.orimod.items.Items;
import su226.orimod.others.Util;
import su226.orimod.packets.WallJumpPacket;

public abstract class WallJumpBase extends Shard {
  public static class WallJump extends WallJumpBase {
    public WallJump() {
      super("wall_jump", 1, 2);
      SpiritSmithingTable.registerRecipe(new SpiritSmithingTable.Recipe(
        new ItemStack(net.minecraft.block.Blocks.LADDER),
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
    public void tick(PlayerEntity player, ItemStack stack) {
      super.tick(player, stack);
      if (!player.isSneaking()) {
        return;
      }
      Box bb = player.getBoundingBox();
      boolean clingXp = !player.world.isSpaceEmpty(bb.stretch(Mod.CONFIG.jump_and_climb.wall_threshold, 0, 0));
      boolean clingXn = !player.world.isSpaceEmpty(bb.stretch(-Mod.CONFIG.jump_and_climb.wall_threshold, 0, 0));
      boolean clingZp = !player.world.isSpaceEmpty(bb.stretch(0, 0, Mod.CONFIG.jump_and_climb.wall_threshold));
      boolean clingZn = !player.world.isSpaceEmpty(bb.stretch(0, 0, -Mod.CONFIG.jump_and_climb.wall_threshold));
      double rad = Math.toRadians(player.yaw);
      double sin = Math.sin(rad);
      double cos = Math.cos(rad);
      double moveX = -(player.forwardSpeed * sin + player.sidewaysSpeed * cos) * Mod.CONFIG.jump_and_climb.climb_multiplier;
      double moveZ = (player.forwardSpeed * cos + player.sidewaysSpeed * sin) * Mod.CONFIG.jump_and_climb.climb_multiplier;
      if (clingXp || clingXn || clingZp || clingZn) {
        Vec3d vec = player.getVelocity();
        double y = Math.max(vec.y / 0.98, 0);
        player.fallDistance = 0;
        if (clingXp && moveX > 0 && y < moveX) {
          y = moveX;
        }
        if (clingXn && moveX < 0 && y < -moveX) {
          y = -moveX;
        }
        if (clingZp && moveZ > 0 && y < moveZ) {
          y = moveZ;
        }
        if (clingZn && moveZ < 0 && y < -moveZ) {
          y = -moveZ;
        }
        player.setVelocity(vec.x, y, vec.z);
      }
    }
  }

  public WallJumpBase(String name, int level, int maxLevel) {
    super("wall_jump_base", name, level, maxLevel);
  }

  @Override
  public void tick(PlayerEntity player, ItemStack stack) {
    boolean cling = !player.world.isSpaceEmpty(player.getBoundingBox().expand(Mod.CONFIG.jump_and_climb.wall_threshold, 0, Mod.CONFIG.jump_and_climb.wall_threshold));
    if (player.world.isClient && cling && Util.canAirJump((ClientPlayerEntity) player)) {
      new WallJumpPacket().sendToServer();
    }
    Vec3d vec = player.getVelocity();
    if (cling && vec.y < 0.0) {
      player.setVelocity(vec.x, vec.y * Mod.CONFIG.jump_and_climb.wall_velocity_fraction, vec.z);
      player.fallDistance *= Mod.CONFIG.jump_and_climb.wall_fall_fraction;
    }
  }
}
