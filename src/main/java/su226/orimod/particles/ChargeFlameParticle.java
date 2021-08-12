package su226.orimod.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import su226.orimod.others.Util;

public class ChargeFlameParticle extends Particle {
  private static final ResourceLocation TEXTURE = Util.getLocation("particle/charge_flame");
  private static TextureAtlasSprite ATLAS;

  public ChargeFlameParticle(World world, Vec3d pos, Vec3d velocity) {
    super(world, pos.x, pos.y, pos.z, velocity.x, velocity.y, velocity.z);
    this.setParticleTexture(ATLAS);
  }

  @Override
  public int getFXLayer() {
    return 1;
  }

  public static void setTexture(TextureMap map) {
    ATLAS = map.registerSprite(TEXTURE);
  }
}
