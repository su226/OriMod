package su226.orimod.particles;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class LightBurstParticle extends Base2DParticle {
  public LightBurstParticle(World world, Vec3d pos, Vec3d velocity) {
    super(world, pos.x, pos.y, pos.z, velocity.x, velocity.y, velocity.z);
  }

  @Override
  protected double getMinU() {
    return 0.0625;
  }

  @Override
  protected double getMinV() {
    return 0.1875;
  }

  @Override
  protected double getMaxU() {
    return 0.125;
  }

  @Override
  protected double getMaxV() {
    return 0.25;
  }

  @Override
  protected ResourceLocation getTexture() {
    return null;
  }
}
