package su226.orimod.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class LightBurstParticle extends Particle {
  public LightBurstParticle(World world, Vec3d pos, Vec3d velocity) {
    super(world, pos.x, pos.y, pos.z, velocity.x, velocity.y, velocity.z);
    this.particleTextureIndexX = 1;
    this.particleTextureIndexY = 3;
  }
}
