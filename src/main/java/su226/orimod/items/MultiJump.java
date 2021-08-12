package su226.orimod.items;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import su226.orimod.Mod;
import su226.orimod.capabilities.Capabilities;
import su226.orimod.capabilities.IMultiJump;
import su226.orimod.messages.MultiJumpMessage;
import su226.orimod.others.Util;

public abstract class MultiJump extends Shard {
  public static class Double extends MultiJump {
    public Double() {
      super("double_jump", 1, 2);
    }

    @Override
    public int getJumpCount() {
      return 1;
    }
  }

  public static class Triple extends MultiJump {
    public Triple() {
      super("triple_jump", 2, 2);
    }

    @Override
    public int getJumpCount() {
      return 2;
    }
  }

  private boolean prevPressed;
  private boolean prevFlying;

  public MultiJump(String name, int level, int maxLevel) {
    super(name, Util.getLocation("textures/items/multi_jump.png"), level, maxLevel);
  }

  public abstract int getJumpCount();

  @Override
  public void onEquipableEquip(EntityPlayer owner) {
    owner.getCapability(Capabilities.MULTI_JUMP, null).addCount(this.getJumpCount());
  }

  @Override
  public void onEquipableUnequip(EntityPlayer owner) {
    owner.getCapability(Capabilities.MULTI_JUMP, null).removeCount(this.getJumpCount());
  }

  @Override
  public void onEquipableUpdate(EntityPlayer owner) {
    IMultiJump cap = owner.getCapability(Capabilities.MULTI_JUMP, null);
    if (owner.world.isRemote) {
      EntityPlayerSP player = (EntityPlayerSP)owner;
      if (player.movementInput.jump  && !prevPressed && cap.getMaxCount() == this.getJumpCount() && this.canJump(owner) && cap.doJump()) {
        Mod.NETWORK.sendToServer(new MultiJumpMessage());
      }
      prevPressed = player.movementInput.jump;
      prevFlying = player.capabilities.isFlying;
    }
    if (owner.onGround) {
      cap.resetJump();
    }
  }

  private boolean canJump(EntityPlayer owner) {
    return !owner.onGround && !owner.capabilities.isFlying && !prevFlying && !owner.isElytraFlying() && !owner.isInWater();
  }
}
