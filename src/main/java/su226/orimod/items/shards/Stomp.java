package su226.orimod.items.shards;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderPlayerEvent;
import su226.orimod.Config;
import su226.orimod.Mod;
import su226.orimod.capabilities.Capabilities;
import su226.orimod.capabilities.ICooldown;
import su226.orimod.messages.SoundMessage;
import su226.orimod.messages.StompEffectMessage;
import su226.orimod.messages.StompMessage;
import su226.orimod.others.PureExplosion;
import su226.orimod.others.Sounds;
import su226.orimod.others.Util;

public class Stomp extends Shard {
  public Stomp() {
    super("stomp", "stomp", 1, 1);
  }

  private boolean prevSneak;
  private int lastClick;
  private int stompTick;

  @Override
  public void onEquipableUpdate(EntityPlayer owner, boolean isMaxPriority) {
    if (owner.world.isRemote) {
      EntityPlayerSP player = (EntityPlayerSP)owner;
      if (!player.onGround && player.movementInput.sneak && !this.prevSneak) {
        if (owner.ticksExisted - this.lastClick > Config.DOUBLE_CLICK_MAX_INTERVAL) {
          this.lastClick = owner.ticksExisted;
        } else {
          this.lastClick = 0;
          this.stompTick = owner.ticksExisted;
          Mod.NETWORK.sendToServer(new StompMessage());
        }
      }
      this.prevSneak = player.movementInput.sneak;
      if (this.stompTick != 0 && owner.onGround) {
        this.stompTick = 0;
      }
    }
    ICooldown cap = owner.getCapability(Capabilities.COOLDOWN, null);
    if (cap.isInCooldown("stomp")) {
      int tick = owner.ticksExisted - cap.getLastAction("stomp");
      if (owner.onGround) {
        cap.setCooldown("stomp", 1, -1);
        owner.setNoGravity(false);
        if (!owner.world.isRemote) {
          new PureExplosion(owner, Config.STOMP.FORCE).doExplosionA();
          Mod.NETWORK.sendToAllAround(new StompEffectMessage(owner), Util.getTargetPoint(owner, 32));
        }
      } else if (tick >= Config.STOMP.CHARGE_TIME) {
        if (tick == Config.STOMP.CHARGE_TIME) {
          SoundMessage.play(owner, Sounds.STOMP_FALL);
        }
        if (owner.motionY > -Config.STOMP.VELOCITY) {
          owner.motionY = -Config.STOMP.VELOCITY;
          owner.velocityChanged = true;
        }
        owner.fallDistance = 0;
      } else if (owner.motionY != 0.1) {
        owner.motionY = 0.1;
        owner.velocityChanged = true;
      }
    }
  }

  public void renderPlayerPre(RenderPlayerEvent.Pre event) {
    if (this.stompTick == 0) {
      return;
    }
    EntityPlayer owner = event.getEntityPlayer();
    float tick = owner.ticksExisted + event.getPartialRenderTick() - this.stompTick;
    if (tick > Config.STOMP.CHARGE_TIME) {
      return;
    }
    GlStateManager.pushMatrix();
    GlStateManager.translate(0, 0.9, 0);
    float yaw = owner.rotationYaw / 180 * (float)Math.PI;
    GlStateManager.rotate(tick / Config.STOMP.CHARGE_TIME * 720, MathHelper.cos(yaw), 0, MathHelper.sin(yaw));
    GlStateManager.translate(0, -0.9, 0);
  }

  public void renderPlayerPost(RenderPlayerEvent.Post event) {
    if (this.stompTick == 0) {
      return;
    }
    float tick = event.getEntityPlayer().ticksExisted + event.getPartialRenderTick() - this.stompTick;
    if (tick > Config.STOMP.CHARGE_TIME) {
      return;
    }
    GlStateManager.popMatrix();
  }
}
