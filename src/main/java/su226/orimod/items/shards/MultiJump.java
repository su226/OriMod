package su226.orimod.items.shards;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import su226.orimod.Mod;
import su226.orimod.blocks.SpiritSmithingTable;
import su226.orimod.capabilities.Capabilities;
import su226.orimod.capabilities.ICooldown;
import su226.orimod.items.Items;
import su226.orimod.messages.MultiJumpMessage;
import su226.orimod.others.Util;

public abstract class MultiJump extends Shard {
  public static class Double extends MultiJump {
    public Double() {
      super("double_jump", 1, 2);
      SpiritSmithingTable.registerRecipe(new SpiritSmithingTable.Recipe(
        new ItemStack(net.minecraft.init.Items.RABBIT_FOOT),
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

  public MultiJump(String name, int level, int maxLevel) {
    super(name, name, level, maxLevel);
  }

  public abstract int getJumpCount();

  @Override
  public void onEquipableUpdate(EntityPlayer owner, boolean isMaxPriority) {
    if (!isMaxPriority) {
      return;
    }
    ICooldown cap = owner.getCapability(Capabilities.COOLDOWN, null);
    if (owner.world.isRemote) {
      if (Util.canAirJump((EntityPlayerSP)owner) && cap.doAction("multi_jump")) {
        Mod.NETWORK.sendToServer(new MultiJumpMessage());
      }
    }
    if (owner.onGround) {
      cap.setCooldown("multi_jump", this.getJumpCount(), -1);
    }
  }

  @Override
  public String getEquipableType() {
    return "multi_jump";
  }

  @Override
  public int getEquipablePriority() {
    return this.getJumpCount();
  }
}
