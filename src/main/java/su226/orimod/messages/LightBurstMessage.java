package su226.orimod.messages;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import su226.orimod.Mod;
import su226.orimod.others.Sounds;
import su226.orimod.others.Util;
import su226.orimod.particles.LightBurstParticle;

public class LightBurstMessage extends ProjectileHitMessage {
  public static class Handler extends ProjectileHitMessage.Handler<LightBurstMessage> {}
  
  public LightBurstMessage() {}

  public LightBurstMessage(Entity owner, Entity hit, Vec3d start, Vec3d end) {
    super(owner, hit, start, end);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void spawnParticle(Vec3d velocity) {
    Minecraft mc = Minecraft.getMinecraft();
    mc.effectRenderer.addEffect(new LightBurstParticle(mc.world, this.start, velocity.scale(2)));
  }

  @Override
  public SoundEvent getHitEntitySound() {
    return Sounds.LIGHT_BURST_HIT_ENTITY;
  }

  @Override
  public SoundEvent getHitGroundSound() {
    return Sounds.LIGHT_BURST_HIT_GROUND;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void callback() {
    for (int i = 0; i < 20; i++) {
      Vec3d velocity = new Vec3d(0.5, 0, 0).rotateYaw(Util.randAngle(2f)).rotatePitch(Util.randAngle(0.5f)).scale(0.5);
      Minecraft.getMinecraft().world.spawnParticle(EnumParticleTypes.FLAME, this.start.x, this.start.y, this.start.z, velocity.x, velocity.y, velocity.z);
    }
  }

  public static void register() {
    Mod.NETWORK.registerMessage(Handler.class, LightBurstMessage.class, Messages.nextId(), Side.CLIENT);
  }
}
