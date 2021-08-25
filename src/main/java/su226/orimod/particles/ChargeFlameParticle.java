package su226.orimod.particles;

import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import su226.orimod.ModClient;
import su226.orimod.others.Util;

public class ChargeFlameParticle extends SpriteBillboardParticle {
  public static final Identifier TEXTURE = Util.getIdentifier("particle/charge_flame");

  public ChargeFlameParticle(ClientWorld world, Vec3d pos, Vec3d velocity) {
    super(world, pos.x, pos.y, pos.z, velocity.x, velocity.y, velocity.z);
    this.setSprite(ModClient.PARTICLE_ATLAS.getSprite(TEXTURE));
    this.velocityX = velocity.x * 0.1;
    this.velocityY = velocity.y * 0.1;
    this.velocityZ = velocity.z * 0.1;
  }

  @Override
  public ParticleTextureSheet getType() {
    return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
  }
}
