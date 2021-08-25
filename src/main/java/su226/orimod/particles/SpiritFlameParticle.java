package su226.orimod.particles;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import su226.orimod.others.Render;
import su226.orimod.others.Util;

public class SpiritFlameParticle extends Particle {
  private static final int MAX_AGE = 10;
  private static final int FINENESS = 10;
  private static final double OFFSET_SCALE = 0.2;
  private static final double EXPAND_SCALE = 0.1;
  private static final double POWER = 2;
  private double point;
  private final Entity from;
  private final boolean isVec;
  private Entity to;
  private Vec3d offset;
  private double moveAngle;

  private double getRatio(double cur, double point) {
    double ratio = cur > point ? 1 - (cur - point) / (1 - point) : cur / point;
    return Math.pow(ratio, 1 / SpiritFlameParticle.POWER);
  }
  
  public SpiritFlameParticle(ClientWorld world, Entity from, Vec3d offset) {
    super(world, from.getX(), from.getY(), from.getZ());
    this.from = from;
    this.isVec = true;
    this.offset = offset;
    this.init();
  }
  
  public SpiritFlameParticle(ClientWorld world, Entity from, Entity to) {
    super(world, from.getX(), from.getY(), from.getZ());
    this.from = from;
    this.isVec = false;
    this.to = to;
    this.init();
  }

  private void init() {
    this.maxAge = MAX_AGE;
    this.point = Util.rand(0.25, 0.75);
    this.moveAngle = Util.randAngle(0.5);
    this.base = new Vec3d[FINENESS];
    this.ratios = new double[FINENESS];
    this.expand0 = new Vec3d[FINENESS - 2];
    this.expand1 = new Vec3d[FINENESS - 2];
  }

  @Override
  public ParticleTextureSheet getType() {
    return ParticleTextureSheet.CUSTOM;
  }
  
  private Vec3d[] base;
  private double[] ratios;
  private Vec3d[] expand0;
  private Vec3d[] expand1;

  private void drawSide(Vec3d translate, int mul0, int mul1) {
    boolean flag = mul0 * mul1 < 0;
    Vec3d[] first = flag ? expand1 : expand0;
    Vec3d[] second = flag ? expand0 : expand1;
    GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
    GL11.glVertex3d(base[0].x + translate.x, base[0].y + translate.y, base[0].z + translate.z);
    for (int i = 1; i < FINENESS - 1; i++) {
      double ratio = this.getRatio(i / (FINENESS - 1.0), this.point);
      Vec3d vec = base[i].add(first[i - 1].multiply(mul0 * ratio).add(translate));
      GL11.glVertex3d(vec.x, vec.y, vec.z);
      vec = base[i].add(second[i - 1].multiply(mul1 * ratio).add(translate));
      GL11.glVertex3d(vec.x, vec.y, vec.z);
    }
    GL11.glVertex3d(base[FINENESS - 1].x + translate.x, base[FINENESS - 1].y + translate.y, base[FINENESS - 1].z + translate.z);
    GL11.glEnd();
  }

  @Override
  public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
    double progress = (this.age + tickDelta) / this.maxAge;
    double start = Math.pow(progress, POWER);
    double end = Math.pow(progress, 1 / POWER);
    Vec3d fromVec = this.from.getCameraPosVec(tickDelta);
    Vec3d delta = this.isVec ? this.offset : Util.pos(this.to, tickDelta).add(new Vec3d(0, this.to.getHeight() / 2, 0)).subtract(fromVec);
    Vec3d move = Util.rotate(delta, Util.perpendicular(delta), this.moveAngle).normalize().multiply(OFFSET_SCALE);
    for (int i = 0; i < FINENESS; i++) {
      double cur = start + (end - start) * i / (FINENESS - 1);
      this.ratios[i] = this.getRatio(cur, point);
      base[i] = delta.multiply(cur).add(move.multiply(this.ratios[i]));
    }
    for (int i = 0; i < FINENESS - 2; i++) {
      expand0[i] = base[i + 1].subtract(base[i]).add(base[i + 1].subtract(base[i + 2])).normalize().multiply(this.ratios[i + 1] * EXPAND_SCALE);
      expand1[i] = expand0[i].crossProduct(delta).normalize().multiply(this.ratios[i + 1] * EXPAND_SCALE);
    }
    Vec3d translate = fromVec.subtract(camera.getPos());
    GlStateManager.disableTexture();
    Render.Legacy.light(240, 240);
    drawSide(translate, 1, 1);
    drawSide(translate, 1, -1);
    drawSide(translate, -1, 1);
    drawSide(translate, -1, -1);
    GlStateManager.enableTexture();
  }
}
