package su226.orimod.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class Base2DParticle extends Particle {
  protected Base2DParticle(World world, double x, double y, double z) {
    super(world, x, y, z);
  }

  protected Base2DParticle(World world, double x, double y, double z, double vx, double vy, double vz) {
    super(world, x, y, z, vx, vy, vz);
  }

  protected abstract double getMinU();
  protected abstract double getMinV();
  protected abstract double getMaxU();
  protected abstract double getMaxV();
  protected abstract ResourceLocation getTexture();
  
  @Override
  public void renderParticle(BufferBuilder bufferIn, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
    double scale = 0.1F * this.particleScale;
    double offsetX = this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX;
    double offsetY = this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY;
    double offsetZ = this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ;
    int brightness = this.getBrightnessForRender(partialTicks);
    int brightnessLow = brightness >> 16 & 0xffff;
    int brightnessHigh = brightness & 0xffff;
    Vec3d[] vecs = new Vec3d[]{
      new Vec3d((-rotationX * scale - rotationXY * scale), (-rotationZ * scale), (-rotationYZ * scale - rotationXZ * scale)),
      new Vec3d((-rotationX * scale + rotationXY * scale), (rotationZ * scale), (-rotationYZ * scale + rotationXZ * scale)),
      new Vec3d((rotationX * scale + rotationXY * scale), (rotationZ * scale), (rotationYZ * scale + rotationXZ * scale)),
      new Vec3d((rotationX * scale - rotationXY * scale), (-rotationZ * scale), (rotationYZ * scale - rotationXZ * scale))
    };
    if (this.particleAngle != 0.0F) {
      float angle = this.particleAngle + (this.particleAngle - this.prevParticleAngle) * partialTicks;
      float sin = MathHelper.sin(angle * 0.5F);
      float cos = MathHelper.cos(angle * 0.5F);
      Vec3d rot = new Vec3d(sin * cameraViewDir.x, sin * cameraViewDir.y, sin * cameraViewDir.z);
      for(int i = 0; i < 4; i++) {
        vecs[i] = rot.scale(2.0D * vecs[i].dotProduct(rot)).add(vecs[i].scale((cos * cos) - rot.dotProduct(rot))).add(rot.crossProduct(vecs[i]).scale((2.0F * cos)));
      }
    }
    double minu = this.getMinU();
    double minv = this.getMinV();
    double maxu = this.getMaxU();
    double maxv = this.getMaxV();
    ResourceLocation tex = this.getTexture();
    if (tex != null) {
      Minecraft.getMinecraft().getTextureManager().bindTexture(tex);
    }
    bufferIn.pos(offsetX + vecs[0].x, offsetY + vecs[0].y, offsetZ + vecs[0].z).tex(maxu, maxv).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(brightnessLow, brightnessHigh).endVertex();
    bufferIn.pos(offsetX + vecs[1].x, offsetY + vecs[1].y, offsetZ + vecs[1].z).tex(maxu, minv).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(brightnessLow, brightnessHigh).endVertex();
    bufferIn.pos(offsetX + vecs[2].x, offsetY + vecs[2].y, offsetZ + vecs[2].z).tex(minu, minv).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(brightnessLow, brightnessHigh).endVertex();
    bufferIn.pos(offsetX + vecs[3].x, offsetY + vecs[3].y, offsetZ + vecs[3].z).tex(minu, maxv).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(brightnessLow, brightnessHigh).endVertex();
  }
}
