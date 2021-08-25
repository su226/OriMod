package su226.orimod.packets;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;
import su226.orimod.others.Sounds;
import su226.orimod.particles.SpiritArcParticle;

public class SpiritArcPacket extends ProjectileHitPacket {
  public SpiritArcPacket() {}

  public SpiritArcPacket(Entity owner, Entity hit, Vec3d start, Vec3d end) {
    super(owner, hit, start, end);
  }

  @Override
  public String getName() {
    return "spirit_arc";
  }

  @Override
  @Environment(EnvType.CLIENT)
  public void spawnParticle(Vec3d velocity) {
    MinecraftClient mc = MinecraftClient.getInstance();
    mc.particleManager.addParticle(new SpiritArcParticle(mc.world, this.start, velocity));
  }

  @Override
  public SoundEvent getHitEntitySound() {
    return Sounds.ARROW_HIT_ENTITY;
  }

  @Override
  public SoundEvent getHitGroundSound() {
    return Sounds.ARROW_HIT_GROUND;
  }
}
