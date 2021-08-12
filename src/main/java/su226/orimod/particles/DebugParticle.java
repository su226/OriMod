package su226.orimod.particles;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DebugParticle extends Particle {
  private static final int MAX_AGE = 2000;
  private Vec3d from;
  private Vec3d delta;
  private byte r;
  private byte g;
  private byte b;
  
  public DebugParticle(World world, Vec3d from, Vec3d to, int color) {
    super(world, from.x, from.y, from.z);
    this.from = from;
    this.delta = to.subtract(from);
    this.particleMaxAge = MAX_AGE;
    this.r = (byte)(color >> 16);
    this.g = (byte)(color >> 8 & 0xff);
    this.b = (byte)(color & 0xff);
  }

  @Override
  public void renderParticle(BufferBuilder buffer, Entity entity, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
    GlStateManager.disableLighting();
    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
    GlStateManager.pushMatrix();
    GL11.glColor3ub(this.r, this.g, this.b);
    GL11.glTranslated(
      from.x - Particle.interpPosX,
      from.y - Particle.interpPosY,
      from.z - Particle.interpPosZ
    );
    GL11.glBegin(GL11.GL_LINES);
    GL11.glVertex3d(0, 0, 0);
    GL11.glVertex3d(delta.x, delta.y, delta.z);
    GL11.glEnd();
    GlStateManager.popMatrix();
    GlStateManager.enableLighting();
  }
}
