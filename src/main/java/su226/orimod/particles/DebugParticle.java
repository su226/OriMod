package su226.orimod.particles;

import com.mojang.blaze3d.platform.GlStateManager;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.math.Vec3d;

public class DebugParticle extends Particle {
  private static final int MAX_AGE = 2000;
  private final Vec3d from;
  private final Vec3d delta;
  private final byte r;
  private final byte g;
  private final byte b;
  
  public DebugParticle(ClientWorld world, Vec3d from, Vec3d to, int color) {
    super(world, from.x, from.y, from.z);
    this.from = from;
    this.delta = to.subtract(from);
    this.maxAge = MAX_AGE;
    this.r = (byte)(color >> 16);
    this.g = (byte)(color >> 8 & 0xff);
    this.b = (byte)(color & 0xff);
  }

  @Override
  public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
    Vec3d cameraPos = camera.getPos();
    GL11.glPushMatrix();
    GL11.glColor3ub(this.r, this.g, this.b);
    GL11.glTranslated(
      from.x - cameraPos.x,
      from.y - cameraPos.y,
      from.z - cameraPos.z
    );
    GlStateManager.disableTexture();
    GL11.glBegin(GL11.GL_LINES);
    GL11.glVertex3d(0, 0, 0);
    GL11.glVertex3d(delta.x, delta.y, delta.z);
    GL11.glEnd();
    GlStateManager.enableTexture();
    GL11.glPopMatrix();
  }

  @Override
  public ParticleTextureSheet getType() {
    return ParticleTextureSheet.CUSTOM;
  }
}
