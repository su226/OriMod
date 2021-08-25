package su226.orimod.packets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;
import su226.orimod.others.Sounds;
import su226.orimod.others.Util;

public class LightBurstPacket extends ProjectileHitPacket {
  public LightBurstPacket() {}

  public LightBurstPacket(Entity owner, Entity hit, Vec3d start, Vec3d end) {
    super(owner, hit, start, end);
  }

  @Override
  public void spawnParticle(Vec3d velocity) {
    MinecraftClient.getInstance().world.addParticle(ParticleTypes.LAVA, start.x, start.y, start.z, velocity.x * 2, velocity.y * 2, velocity.z * 2);
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
  public String getName() {
    return "light_burst";
  }

  @Override
  public void callback() {
    for (int i = 0; i < 20; i++) {
      Vec3d velocity = new Vec3d(0.5, 0, 0).rotateY(Util.randAngle(2f)).rotateX(Util.randAngle(0.5f)).multiply(0.5);
      MinecraftClient.getInstance().world.addParticle(ParticleTypes.FLAME, this.start.x, this.start.y, this.start.z, velocity.x, velocity.y, velocity.z);
    }
  }
}
