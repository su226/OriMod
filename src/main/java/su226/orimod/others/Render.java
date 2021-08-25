package su226.orimod.others;

import dev.monarkhes.myron.api.Myron;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.system.MemoryStack;
import su226.orimod.ModClient;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Random;

public class Render {
  public static final int FULL_BRIGHT = 15728880;
  public static final float EMPTY_MIN_U;
  public static final float EMPTY_MAX_U;
  public static final float EMPTY_MIN_V;
  public static final float EMPTY_MAX_V;
  static {
    Sprite emptySprite = MinecraftClient.getInstance().getSpriteAtlas(ModClient.BLOCK_ATLAS_TEXTURE).apply(Util.getIdentifier("item/empty"));
    EMPTY_MIN_U = emptySprite.getMinU();
    EMPTY_MAX_U = emptySprite.getMaxU();
    EMPTY_MIN_V = emptySprite.getMinV();
    EMPTY_MAX_V = emptySprite.getMaxV();
  }

  public static void model(MatrixStack mat, VertexConsumer consumer, String name, int color, int overlay, int light) {
    rawModel(mat, consumer, "models/" + name + ".obj", color, overlay, light);
  }

  public static void rawModel(MatrixStack mat, VertexConsumer consumer, String name, int color, int overlay, int light) {
    BakedModel model = Myron.getModel(Util.getIdentifier(name));
    float a = (color >>> 24) / 255f;
    float r = (color >> 16 & 0xff) / 255f;
    float g = (color >> 8 & 0xff) / 255f;
    float b = (color & 0xff) / 255f;
    for (BakedQuad quad : model.getQuads(null, null, null)) {
      bakedQuad(mat, consumer, quad, r, g, b, a, overlay, light);
    }
  }

  public static void bakedQuad(MatrixStack mat, VertexConsumer consumer, BakedQuad quad, float red, float green, float blue, float alpha, int overlay, int light) {
    MatrixStack.Entry entry = mat.peek();
    Matrix4f modelMat = entry.getModel();
    Vec3i faceVec = quad.getFace().getVector();
    Vec3f normal = new Vec3f(faceVec.getX(), faceVec.getY(), faceVec.getZ());
    normal.transform(entry.getNormal());
    int[] data = quad.getVertexData();
    int count = data.length / 8;
    MemoryStack memoryStack = MemoryStack.stackPush();
    Throwable exc = null;
    try {
      ByteBuffer byteBuffer = memoryStack.malloc(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL.getVertexSize());
      IntBuffer intBuffer = byteBuffer.asIntBuffer();
      for(int k = 0; k < count; ++k) {
        intBuffer.clear();
        intBuffer.put(data, k * 8, 8);
        float x = byteBuffer.getFloat(0);
        float y = byteBuffer.getFloat(4);
        float z = byteBuffer.getFloat(8);
        // Make sure to & 0xff to make it unsigned
        float r = red * (byteBuffer.get(12) & 0xff) / 255f;
        float g = green * (byteBuffer.get(13) & 0xff) / 255f;
        float b = blue * (byteBuffer.get(14) & 0xff) / 255f;
        float u = byteBuffer.getFloat(16);
        float v = byteBuffer.getFloat(20);
        Vector4f vector4f = new Vector4f(x, y, z, 1);
        vector4f.transform(modelMat);
        consumer.vertex(vector4f.getX(), vector4f.getY(), vector4f.getZ(), r, g, b, alpha, u, v, overlay, light, normal.getX(), normal.getY(), normal.getZ());
      }
    } catch (Throwable e) {
      exc = e;
      throw e;
    } finally {
      if (memoryStack != null) {
        try {
          memoryStack.close();
        } catch (Throwable e) {
          if (exc != null) {
            exc.addSuppressed(e);
          }
        }
      }
    }
  }

  private static void glowingLineQuad(VertexConsumer consumer, Vec3f[] vertexes, float r, float g, float b, float a, int i, int j, int k, int l) {
    consumer.vertex(vertexes[i].getX(), vertexes[i].getY(), vertexes[i].getZ(), r, g, b, a, EMPTY_MIN_U, EMPTY_MIN_V, 0, FULL_BRIGHT, 0, 1, 0);
    consumer.vertex(vertexes[j].getX(), vertexes[j].getY(), vertexes[j].getZ(), r, g, b, a, EMPTY_MAX_U, EMPTY_MIN_V, 0, FULL_BRIGHT, 0, 1, 0);
    consumer.vertex(vertexes[k].getX(), vertexes[k].getY(), vertexes[k].getZ(), r, g, b, a, EMPTY_MAX_U, EMPTY_MAX_V, 0, FULL_BRIGHT, 0, 1, 0);
    consumer.vertex(vertexes[l].getX(), vertexes[l].getY(), vertexes[l].getZ(), r, g, b, a, EMPTY_MIN_U, EMPTY_MAX_V, 0, FULL_BRIGHT, 0, 1, 0);
  }

  public static void glowingLineBase(VertexConsumer consumer, Vec3f... vertexes) {
    glowingLineQuad(consumer, vertexes, 1, 1, 1, 1, 0, 1, 2, 3);
    glowingLineQuad(consumer, vertexes, 1, 1, 1, 1, 0, 1, 5, 4);
    glowingLineQuad(consumer, vertexes, 1, 1, 1, 1, 1, 2, 6, 5);
    glowingLineQuad(consumer, vertexes, 1, 1, 1, 1, 2, 3, 7, 6);
    glowingLineQuad(consumer, vertexes, 1, 1, 1, 1, 3, 0, 4, 7);
    glowingLineQuad(consumer, vertexes, 1, 1, 1, 1, 4, 5, 6, 7);
  }

  public static void glowingLineGlow(VertexConsumer consumer, float r, float g, float b, float a, Vec3f... vertexes) {
    glowingLineQuad(consumer, vertexes, r, g, b, a, 0, 3, 2, 1);
    glowingLineQuad(consumer, vertexes, r, g, b, a, 0, 4, 5, 1);
    glowingLineQuad(consumer, vertexes, r, g, b, a, 1, 5, 6, 2);
    glowingLineQuad(consumer, vertexes, r, g, b, a, 2, 6, 7, 3);
    glowingLineQuad(consumer, vertexes, r, g, b, a, 3, 7, 4, 0);
    glowingLineQuad(consumer, vertexes, r, g, b, a, 4, 7, 6, 5);
  }

  public static void glowingLine(MatrixStack mat, VertexConsumer buf, float startX, float startY, float startZ, float endX, float endY, float endZ, int color) {
    Matrix4f matrix = mat.peek().getModel();
    Vector4f start4f = new Vector4f(startX, startY, startZ, 1);
    start4f.transform(matrix);
    Vector4f end4f = new Vector4f(endX, endY, endZ, 1);
    end4f.transform(matrix);
    Vec3f start = new Vec3f(start4f.getX(), start4f.getY(), start4f.getZ());
    Vec3f end = new Vec3f(end4f.getX(), end4f.getY(), end4f.getZ());
    Vec3f extend1 = end.copy();
    extend1.subtract(start);
    Vec3f extend0 = extend1.copy();
    Util.perpendicular(extend0);
    extend0.normalize();
    extend1.cross(extend0);
    extend1.normalize();
    Vec3f extend2 = extend0.copy();
    extend2.scale(-1);
    Vec3f extend3 = extend1.copy();
    extend3.scale(-1);
    Vec3f extendGlow0 = extend0.copy();
    Vec3f extendGlow1 = extend1.copy();
    Vec3f extendGlow2 = extend2.copy();
    Vec3f extendGlow3 = extend3.copy();
    extendGlow0.scale(0.02f);
    extendGlow1.scale(0.02f);
    extendGlow2.scale(0.02f);
    extendGlow3.scale(0.02f);
    extend0.scale(0.01f);
    extend1.scale(0.01f);
    extend2.scale(0.01f);
    extend3.scale(0.01f);
    Vec3f start0 = start.copy();
    start0.add(extend0);
    Vec3f start1 = start.copy();
    start1.add(extend1);
    Vec3f start2 = start.copy();
    start2.add(extend2);
    Vec3f start3 = start.copy();
    start3.add(extend3);
    Vec3f end0 = end.copy();
    end0.add(extend0);
    Vec3f end1 = end.copy();
    end1.add(extend1);
    Vec3f end2 = end.copy();
    end2.add(extend2);
    Vec3f end3 = end.copy();
    end3.add(extend3);
    Vec3f startGlow0 = start.copy();
    startGlow0.add(extendGlow0);
    Vec3f startGlow1 = start.copy();
    startGlow1.add(extendGlow1);
    Vec3f startGlow2 = start.copy();
    startGlow2.add(extendGlow2);
    Vec3f startGlow3 = start.copy();
    startGlow3.add(extendGlow3);
    Vec3f endGlow0 = end.copy();
    endGlow0.add(extendGlow0);
    Vec3f endGlow1 = end.copy();
    endGlow1.add(extendGlow1);
    Vec3f endGlow2 = end.copy();
    endGlow2.add(extendGlow2);
    Vec3f endGlow3 = end.copy();
    endGlow3.add(extendGlow3);
    glowingLineBase(buf,
      start0,
      start1,
      start2,
      start3,
      end0,
      end1,
      end2,
      end3
    );
    float a = (color >>> 24) / 255f;
    float r = (color >> 16 & 0xff) / 255f;
    float g = (color >> 8 & 0xff) / 255f;
    float b = (color & 0xff) / 255f;
    glowingLineGlow(buf, r, g, b, a,
      startGlow0,
      startGlow1,
      startGlow2,
      startGlow3,
      endGlow0,
      endGlow1,
      endGlow2,
      endGlow3
    );
  }

  public static void square(MatrixStack mat, VertexConsumer consumer, Vec3d point1, Vec3d point2, Vec3d point3, Identifier texture, int color, int light) {
    Vec3d point4 = point1.subtract(point2).add(point3);
    Matrix4f m = mat.peek().getModel();
    int a = color >>> 24;
    int r = color >> 16 & 0xff;
    int g = color >> 8 & 0xff;
    int b = color & 0xff;
    Sprite sprite = MinecraftClient.getInstance().getSpriteAtlas(ModClient.BLOCK_ATLAS_TEXTURE).apply(texture);
    consumer.vertex(m, (float)point1.x, (float)point1.y, (float)point1.z).color(r, g, b, a).texture(sprite.getMinU(), sprite.getMinV()).overlay(0).light(light).normal(0, 1, 0).next();
    consumer.vertex(m, (float)point2.x, (float)point2.y, (float)point2.z).color(r, g, b, a).texture(sprite.getMinU(), sprite.getMaxV()).overlay(0).light(light).normal(0, 1, 0).next();
    consumer.vertex(m, (float)point3.x, (float)point3.y, (float)point3.z).color(r, g, b, a).texture(sprite.getMaxU(), sprite.getMaxV()).overlay(0).light(light).normal(0, 1, 0).next();
    consumer.vertex(m, (float)point4.x, (float)point4.y, (float)point4.z).color(r, g, b, a).texture(sprite.getMaxU(), sprite.getMinV()).overlay(0).light(light).normal(0, 1, 0).next();
  }

  public static void glowingRays(MatrixStack mat, VertexConsumer consumer, int color, float scale, int rays, float rotate) {
    Random random = new Random(226);
    int r = color >> 16 & 0xff;
    int g = color >> 8 & 0xff;
    int b = color & 0xff;
    int a = color >> 24;
    mat.push();
    for (int i = 0; i < rays; i++) {
      mat.multiply(new Quaternion(random.nextFloat() * 360, random.nextFloat() * 360, random.nextFloat() * 360 + rotate, true));
      Matrix4f m = mat.peek().getModel();
      float v = (random.nextFloat() * 0.1f + 0.9f) * scale;
      consumer.vertex(m, 0, 0, 0).color(r, g, b, 0).texture(EMPTY_MIN_U, EMPTY_MIN_V).overlay(0).light(FULL_BRIGHT).normal(0, 1, 0).next();
      consumer.vertex(m, -0.05f, 0.5f * v, 0).color(r, g, b, a).texture(EMPTY_MIN_U, EMPTY_MAX_V).overlay(0).light(FULL_BRIGHT).normal(0, 1, 0).next();
      consumer.vertex(m, 0.05f, 0.5f * v, 0).color(r, g, b, a).texture(EMPTY_MAX_U, EMPTY_MAX_V).overlay(0).light(FULL_BRIGHT).normal(0, 1, 0).next();
      consumer.vertex(m, 0, 0, 0).color(r, g, b, 0).texture(EMPTY_MAX_U, EMPTY_MIN_V).overlay(0).light(FULL_BRIGHT).normal(0, 1, 0).next();
      consumer.vertex(m, 0, 0, 0).color(r, g, b, 0).texture(EMPTY_MIN_U, EMPTY_MIN_V).overlay(0).light(FULL_BRIGHT).normal(0, 1, 0).next();
      consumer.vertex(m, 0, 0, 0).color(r, g, b, 0).texture(EMPTY_MAX_U, EMPTY_MIN_V).overlay(0).light(FULL_BRIGHT).normal(0, 1, 0).next();
      consumer.vertex(m, 0.05f, 0.5f * v, 0).color(r, g, b, a).texture(EMPTY_MAX_U, EMPTY_MAX_V).overlay(0).light(FULL_BRIGHT).normal(0, 1, 0).next();
      consumer.vertex(m, -0.05f, 0.5f * v, 0).color(r, g, b, a).texture(EMPTY_MIN_U, EMPTY_MAX_V).overlay(0).light(FULL_BRIGHT).normal(0, 1, 0).next();
    }
    mat.pop();
  }

  /** Legacy render should only be used in GUI. */
  public static class Legacy {
    public static void rgba(int value) {
      byte r = (byte)(value >> 16 & 0xff);
      byte g = (byte)(value >> 8 & 0xff);
      byte b = (byte)(value & 0xff);
      byte a = (byte)(value >> 24);
      GL11.glColor4ub(r, g, b, a);
    }

    public static void light(float u, float v) {
      GL13.glMultiTexCoord2f(GL13.GL_TEXTURE2, u, v);
    }

    public static void square(MatrixStack mat, Vec3d point1, Vec3d point2, Vec3d point3, Identifier texture) {
      Vec3d point4 = point1.subtract(point2).add(point3);
      Matrix4f m = mat.peek().getModel();
      Tessellator tess = Tessellator.getInstance();
      BufferBuilder buf = tess.getBuffer();
      MinecraftClient.getInstance().getTextureManager().bindTexture(texture);
      buf.begin(GL11.GL_QUADS, VertexFormats.POSITION_TEXTURE);
      buf.vertex(m, (float)point1.x, (float)point1.y, (float)point1.z).texture(0, 0).next();
      buf.vertex(m, (float)point2.x, (float)point2.y, (float)point2.z).texture(0, 1).next();
      buf.vertex(m, (float)point3.x, (float)point3.y, (float)point3.z).texture(1, 1).next();
      buf.vertex(m, (float)point4.x, (float)point4.y, (float)point4.z).texture(1, 0).next();
      tess.draw();
    }
  }

  public static class Circle {
    private float x;
    private float y;
    private float z;
    private float outer = 1;
    private float inner = 0.5f;
    private float minAngle = 0;
    private float maxAngle = (float)Math.PI * 2;
    private int edges = 4;
    private int r = 0xff;
    private int g = 0xff;
    private int b = 0xff;
    private int a = 0xff;
    private float centerU = (EMPTY_MAX_U + EMPTY_MIN_U) / 2;
    private float centerV = (EMPTY_MAX_V + EMPTY_MIN_V) / 2;
    private float radiusU = (EMPTY_MAX_U - EMPTY_MIN_U) / 2;
    private float radiusV = (EMPTY_MAX_V - EMPTY_MIN_V) / 2;
    private int overlay;
    private int light;
    private Matrix4f mat;
    private VertexConsumer consumer;

    public Circle pos(float x, float y, float z) {
      this.x = x;
      this.y = y;
      this.z = z;
      return this;
    }

    public Circle outer(float outer) {
      this.outer = outer;
      return this;
    }

    public Circle inner(float inner) {
      this.inner = inner;
      return this;
    }

    public Circle range(float minAngle, float maxAngle) {
      this.minAngle = minAngle;
      this.maxAngle = maxAngle;
      return this;
    }

    public Circle edges(int edges) {
      this.edges = edges;
      return this;
    }

    public Circle color(int color) {
      this.r = color >> 16 & 0xff;
      this.g = color >> 8 & 0xff;
      this.b = color & 0xff;
      this.a = color >>> 24;
      return this;
    }

    public Circle texture(float minU, float minV, float maxU, float maxV) {
      this.centerU = (maxU + minU) / 2;
      this.centerV = (maxV + minV) / 2;
      this.radiusU = (maxU - minU) / 2;
      this.radiusV = (maxV - minV) / 2;
      return this;
    }

    public Circle overlay(int value) {
      this.overlay = value;
      return this;
    }

    public Circle light(int value) {
      this.light = value;
      return this;
    }

    public void solid(MatrixStack mat, VertexConsumer consumer) {
      this.mat = mat.peek().getModel();
      this.consumer = consumer;
      for (int i = 0; i < this.edges - 1; i += 2) {
        this.vertexCenter();
        this.vertexOuter(i + 2);
        this.vertexOuter(i + 1);
        this.vertexOuter(i);
      }
      if ((edges & 1) == 1) {
        this.vertexCenter();
        this.vertexCenter();
        this.vertexOuter(0);
        this.vertexOuter(edges - 1);
      }
    }

    public void outline(MatrixStack mat, VertexConsumer consumer) {
      this.mat = mat.peek().getModel();
      this.consumer = consumer;
      for (int i = 0; i < this.edges; i++) {
        this.vertexOuter(i);
        this.vertexInner(i);
        this.vertexInner(i + 1);
        this.vertexOuter(i + 1);
      }
    }

    private void vertexCenter() {
      consumer.vertex(mat, x , y, 0).color(r, g, b, a).texture(centerU, centerV).overlay(overlay).light(light).normal(0, 1, 0).next();
    }

    private void vertexOuter(int i) {
      float angle = (maxAngle - minAngle) * i / edges + minAngle;
      float sin = MathHelper.sin(angle);
      float cos = MathHelper.cos(angle);
      consumer.vertex(mat, x + outer * sin, y + outer * cos, z).color(r, g, b, a).texture(centerU + radiusU * sin, centerV + radiusV * cos).overlay(overlay).light(light).normal(0, 0, 1).next();
    }

    private void vertexInner(int i) {
      float angle = (maxAngle - minAngle) * i / edges + minAngle;
      float ratio = inner / outer;
      float sin = MathHelper.sin(angle);
      float cos = MathHelper.cos(angle);
      consumer.vertex(mat, x + inner * sin, y + inner * cos, z).color(r, g, b, a).texture(centerU + radiusU * ratio * sin, centerV + radiusV * ratio * cos).overlay(overlay).light(light).normal(0, 0, 1).next();
    }
  }

  public static RenderLayer SHARD_LAYER;

  public static class Layers extends RenderPhase {
    public Layers(String name, Runnable begin, Runnable end) {
      super(name, begin, end);
    }

    public static void init() {
      SHARD_LAYER = RenderLayer.of("spirit_shard_overlay", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT, GL11.GL_QUADS, 256, true, false,
        RenderLayer.MultiPhaseParameters.builder()
        .shadeModel(SMOOTH_SHADE_MODEL)
        .texture(BLOCK_ATLAS_TEXTURE)
        .transparency(TRANSLUCENT_TRANSPARENCY)
        .lightmap(ENABLE_LIGHTMAP)
        .alpha(RenderPhase.ONE_TENTH_ALPHA)
        .build(false));
    }
  }

  static {
    Layers.init();
  }
}
