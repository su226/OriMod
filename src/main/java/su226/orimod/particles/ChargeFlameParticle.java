package su226.orimod.particles;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import su226.orimod.others.Util;

public class ChargeFlameParticle extends Base2DParticle {
  private static final ResourceLocation TEXTURE = Util.getLocation("textures/particle/charge_flame.png");

  public ChargeFlameParticle(World world, Vec3d pos, Vec3d velocity) {
    super(world, pos.x, pos.y, pos.z, velocity.x, velocity.y, velocity.z);
  }

  @Override
  protected double getMinU() {
    return 0;
  }

  @Override
  protected double getMinV() {
    return 0;
  }

  @Override
  protected double getMaxU() {
    return 1;
  }

  @Override
  protected double getMaxV() {
    return 1;
  }

  @Override
  protected ResourceLocation getTexture() {
    return TEXTURE;
  }
}
