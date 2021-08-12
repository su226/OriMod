package su226.orimod.particles;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import su226.orimod.others.Util;

public class SpiritFlameParticle extends Particle {
  private static final int MAX_AGE = 10;
  private static final int FINENESS = 10;
  private static final double OFFSET_SCALE = 0.2;
  private static final double EXPAND_SCALE = 0.1;
  private static final double POWER = 2;
  private double point;
  private Entity from;
  private boolean isVec;
  private Entity to;
  private Vec3d offset;
  private double moveAngle;

  private double getRatio(double cur, double point, double power) {
    double ratio = cur > point ? 1 - (cur - point) / (1 - point) : cur / point;
    return Math.pow(ratio, 1 / power);
  }
  
  public SpiritFlameParticle(World world, Entity from, Vec3d offset) {
    super(world, from.posX, from.posY, from.posZ);
    this.from = from;
    this.isVec = true;
    this.offset = offset;
    this.init();
  }
  
  public SpiritFlameParticle(World world, Entity from, Entity to) {
    super(world, from.posX, from.posY, from.posZ);
    this.from = from;
    this.isVec = false;
    this.to = to;
    this.init();
  }

  private void init() {
    this.particleMaxAge = MAX_AGE;
    this.point = Util.rand(0.25, 0.75);
    this.moveAngle = Util.randAngle(0.5);
    this.base = new Vec3d[FINENESS];
    this.ratios = new double[FINENESS];
    this.expand0 = new Vec3d[FINENESS - 2];
    this.expand1 = new Vec3d[FINENESS - 2];
  }
  
  private Vec3d[] base;
  private double[] ratios;
  private Vec3d[] expand0;
  private Vec3d[] expand1;

  private void drawSide(double mul0, double mul1) {
    GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
    GL11.glVertex3d(base[0].x, base[0].y, base[0].z);
    for (int i = 1; i < FINENESS - 1; i++) {
      double ratio = this.getRatio(i / (FINENESS - 1.0), this.point, POWER);
      Vec3d vec = base[i].add(expand0[i - 1].scale(mul0 * ratio));
      GL11.glVertex3d(vec.x, vec.y, vec.z);
      vec = base[i].add(expand1[i - 1].scale(mul1 * ratio));
      GL11.glVertex3d(vec.x, vec.y, vec.z);
    }
    GL11.glVertex3d(base[FINENESS - 1].x, base[FINENESS - 1].y, base[FINENESS - 1].z);
    GL11.glEnd();
  }

  @Override
  public void renderParticle(BufferBuilder buffer, Entity entity, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
    // GlStateManager.disableLighting();
    GlStateManager.disableTexture2D();
    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
    GlStateManager.pushMatrix();
    double progress = (this.particleAge + partialTicks) / this.particleMaxAge;
    double start = Math.pow(progress, POWER);
    double end = Math.pow(progress, 1 / POWER);
    Vec3d fromVec = this.from.getPositionEyes(partialTicks);
    Vec3d delta = this.isVec ? this.offset : Util.pos(this.to, partialTicks).add(new Vec3d(0, this.to.height / 2, 0)).subtract(fromVec);
    Vec3d move = Util.rotate(delta, Util.perpendicular(delta), this.moveAngle).normalize().scale(OFFSET_SCALE);
    for (int i = 0; i < FINENESS; i++) {
      double cur = start + (end - start) * i / (FINENESS - 1);
      this.ratios[i] = this.getRatio(cur, point, POWER);
      base[i] = delta.scale(cur).add(move.scale(this.ratios[i]));
    }
    for (int i = 0; i < FINENESS - 2; i++) {
      expand0[i] = base[i + 1].subtract(base[i]).add(base[i + 1].subtract(base[i + 2])).normalize().scale(this.ratios[i + 1] * EXPAND_SCALE);
      expand1[i] = expand0[i].crossProduct(delta).normalize().scale(this.ratios[i + 1] * EXPAND_SCALE);
    }
    GL11.glTranslated(
      fromVec.x - Particle.interpPosX,
      fromVec.y - Particle.interpPosY,
      fromVec.z - Particle.interpPosZ
    );
    drawSide(1, 1);
    drawSide(1, -1);
    drawSide(-1, 1);
    drawSide(-1, -1);
    GlStateManager.popMatrix();
    GlStateManager.enableTexture2D();
    // GlStateManager.enableLighting();
  }
}
