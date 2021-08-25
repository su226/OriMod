package su226.orimod.items.shards;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import su226.orimod.Mod;
import su226.orimod.components.Components;
import su226.orimod.components.ICooldown;
import su226.orimod.others.PureExplosion;
import su226.orimod.others.Sounds;
import su226.orimod.packets.StompEffectPacket;
import su226.orimod.packets.StompPacket;

public class Stomp extends Shard {
  public Stomp() {
    super("stomp", "stomp", 1, 1);
  }

  private boolean prevSneak;
  private int lastClick;
  private int stompTick;

  @Override
  public void tick(PlayerEntity player, ItemStack stack) {
    if (player.world.isClient) {
      ClientPlayerEntity client = (ClientPlayerEntity)player;
      if (!player.isOnGround() && client.input.sneaking && !this.prevSneak) {
        if (player.age - this.lastClick > Mod.CONFIG.double_click_max_interval) {
          this.lastClick = player.age;
        } else {
          this.lastClick = 0;
          this.stompTick = player.age;
          new StompPacket().sendToServer();
        }
      }
      this.prevSneak = client.input.sneaking;
      if (this.stompTick != 0 && player.isOnGround()) {
        this.stompTick = 0;
      }
    }
    ICooldown cap = Components.COOLDOWN.get(player);
    Vec3d vec = player.getVelocity();
    if (cap.isInCooldown("stomp")) {
      int tick = player.age - cap.getLastAction("stomp");
      if (player.isOnGround()) {
        cap.setCooldown("stomp", 1, -1);
        player.setNoGravity(false);
        if (!player.world.isClient) {
          new PureExplosion(player, player, Mod.CONFIG.stomp.force).explode();
          new StompEffectPacket(player).sendToAround(player, 32);
        }
      } else if (tick >= Mod.CONFIG.stomp.charge_time) {
        if (tick == Mod.CONFIG.stomp.charge_time) {
          player.world.playSound(null, player.getX(), player.getY(), player.getZ(), Sounds.STOMP_FALL, SoundCategory.PLAYERS, 1, 1);
        }
        if (vec.y > -Mod.CONFIG.stomp.velocity) {
          player.setVelocity(vec.x, -Mod.CONFIG.stomp.velocity, vec.z);
          player.velocityModified = true;
        }
        player.fallDistance = 0;
      } else if (vec.y != 0.1) {
        player.setVelocity(vec.x, 0.1, vec.z);
        player.velocityModified = true;
      }
    }
  }

  public void renderPlayerPre(PlayerEntity ent, float tickDelta, MatrixStack mat) {
    if (this.stompTick == 0) {
      return;
    }
    float tick = ent.age + tickDelta - this.stompTick;
    if (tick > Mod.CONFIG.stomp.charge_time) {
      return;
    }
    mat.push();
    mat.translate(0, 0.9, 0);
    mat.multiply(new Quaternion(tick / Mod.CONFIG.stomp.charge_time * 720, 0, 0, true));
    mat.translate(0, -0.9, 0);
  }

  public void renderPlayerPost(PlayerEntity ent, float tickDelta, MatrixStack mat) {
    if (this.stompTick == 0) {
      return;
    }
    float tick = ent.age + tickDelta - this.stompTick;
    if (tick > Mod.CONFIG.stomp.charge_time) {
      return;
    }
    mat.pop();
  }
}
