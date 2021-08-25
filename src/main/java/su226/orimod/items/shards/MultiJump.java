package su226.orimod.items.shards;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import su226.orimod.blocks.SpiritSmithingTable;
import su226.orimod.components.Components;
import su226.orimod.components.ICooldown;
import su226.orimod.items.Items;
import su226.orimod.others.Util;
import su226.orimod.packets.MultiJumpPacket;

public abstract class MultiJump extends Shard {
  public static class Double extends MultiJump {
    public Double() {
      super("double_jump", 1, 2);
      SpiritSmithingTable.registerRecipe(new SpiritSmithingTable.Recipe(
        new ItemStack(net.minecraft.item.Items.RABBIT_FOOT),
        new ItemStack(this),
        300
      ));
    }

    @Override
    public int getJumpCount() {
      return 1;
    }
  }

  public static class Triple extends MultiJump {
    public Triple() {
      super("triple_jump", 2, 2);
      SpiritSmithingTable.registerRecipe(new SpiritSmithingTable.Recipe(
        new ItemStack(Items.DOUBLE_JUMP),
        new ItemStack(this),
        300
      ));
    }

    @Override
    public int getJumpCount() {
      return 2;
    }
  }

  public MultiJump(String texture, int level, int maxLevel) {
    super("multi_jump", texture, level, maxLevel);
  }

  public abstract int getJumpCount();

  @Override
  public void tick(PlayerEntity player, ItemStack stack) {
    ICooldown cap = Components.COOLDOWN.get(player);
    if (player.world.isClient) {
      if (Util.canAirJump((ClientPlayerEntity)player) && cap.doAction("multi_jump")) {
        new MultiJumpPacket().sendToServer();
      }
    }
    if (player.isOnGround()) {
      cap.setCooldown("multi_jump", this.getJumpCount(), -1);
    }
  }
}
