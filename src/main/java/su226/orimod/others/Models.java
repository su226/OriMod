package su226.orimod.others;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import su226.orimod.Mod;
import su226.orimod.blocks.Blocks;
import su226.orimod.entities.Arrow;
import su226.orimod.items.Items;
import su226.orimod.items.shards.Shard;
import su226.orimod.particles.ChargeFlameParticle;
import su226.orimod.particles.SpiritArcParticle;

@SideOnly(Side.CLIENT)
public class Models {
  private static class PlaceholderLoader implements ICustomModelLoader {
    @Override
    public boolean accepts(ResourceLocation loc) {
      return loc.getResourceDomain().equals(Mod.MODID) && (loc.getResourcePath().equals("placeholder") || loc.getResourcePath().equals("placeholder_with_transform"));
    }

    @Override
    public IModel loadModel(ResourceLocation loc) throws Exception {
      return (state, format, bakedTextureGetter) -> loc.getResourcePath().equals("placeholder_with_transform") ? PlaceholderModel.WITH_TRANSFORM : PlaceholderModel.WITHOUT_TRANSFORM;
    }

    @Override
    public void onResourceManagerReload(IResourceManager loc) {}
  }

  private static class PlaceholderModel implements IBakedModel {
    @SuppressWarnings("deprecation")
    private static final ItemCameraTransforms TRANSFORMS = new ItemCameraTransforms(
      new ItemTransformVec3f(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f, 0.1875f, 0.0625f), new Vector3f(0.55f, 0.55f, 0.55f)),
      new ItemTransformVec3f(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f, 0.1875f, 0.0625f), new Vector3f(0.55f, 0.55f, 0.55f)),
      new ItemTransformVec3f(new Vector3f(0.0f, -90.0f, 25.0f), new Vector3f(0.070625f, 0.2f, 0.070625f), new Vector3f(0.68f, 0.68f, 0.68f)),
      new ItemTransformVec3f(new Vector3f(0.0f, -90.0f, 25.0f), new Vector3f(0.070625f, 0.2f, 0.070625f), new Vector3f(0.68f, 0.68f, 0.68f)),
      new ItemTransformVec3f(new Vector3f(0.0f, 180.0f, 0.0f), new Vector3f(0.0f, 0.8125f, 0.4375f), new Vector3f(1.0f, 1.0f, 1.0f)),
      new ItemTransformVec3f(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(1.0f, 1.0f, 1.0f)),
      new ItemTransformVec3f(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f, 0.125f, 0.0f), new Vector3f(0.5f, 0.5f, 0.5f)),
      new ItemTransformVec3f(new Vector3f(0.0f, 180.0f, 0.0f), new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(1.0f, 1.0f, 1.0f))
    );
    public static final PlaceholderModel WITHOUT_TRANSFORM = new PlaceholderModel(false);
    public static final PlaceholderModel WITH_TRANSFORM = new PlaceholderModel(true);
    private final boolean withTransform;
    
    private PlaceholderModel(boolean withTransform) {
      this.withTransform = withTransform;
    }

    @Override
    public ItemOverrideList getOverrides() {
      return ItemOverrideList.NONE;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
      return null;
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
      return null;
    }

    @Override
    public boolean isAmbientOcclusion() {
      return false;
    }

    @Override
    public boolean isBuiltInRenderer() {
      return true;
    }

    @Override
    public boolean isGui3d() {
      return false;
    }
    
    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
      return this.withTransform ? TRANSFORMS : ItemCameraTransforms.DEFAULT;
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType type) {
      transform = type;
      return ForgeHooksClient.handlePerspective(this, type);
    }
  }

  public static TransformType transform = null;

  public static List<BakedQuad> loadItemModel(String name) {
    try {
      return ModelLoaderRegistry.getModel(new ResourceLocation(Mod.MODID, name))
        .bake(TRSRTransformation.identity(), DefaultVertexFormats.ITEM, ModelLoader.defaultTextureGetter())
        .getQuads(null, null, 0);
    } catch (Exception e) {
      Mod.LOG.error("Failed to load model: {}", name, e);
      return Collections.emptyList();
    }
  }

  public static void setItemModel(Item item, String name) {
    ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(new ResourceLocation(Mod.MODID, name), "inventory"));
  }

  public static void renderItemModel(List<BakedQuad> quads) {
    renderItemModel(quads, 0xffffffff);
  }

  public static void renderItemModel(List<BakedQuad> quads, int color) {
    Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
    Tessellator tess = Tessellator.getInstance();
    BufferBuilder buffer = tess.getBuffer();
    buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
    for (BakedQuad quad : quads) {
      LightUtil.renderQuadColor(buffer, quad, color);
    }
    tess.draw();
  }

  private static void drawQuad(Vec3d[] vertexs, int a, int b, int c, int d) {
    GL11.glVertex3d(vertexs[a].x, vertexs[a].y, vertexs[a].z);
    GL11.glVertex3d(vertexs[b].x, vertexs[b].y, vertexs[b].z);
    GL11.glVertex3d(vertexs[c].x, vertexs[c].y, vertexs[c].z);
    GL11.glVertex3d(vertexs[d].x, vertexs[d].y, vertexs[d].z);
  }

  public static void color(int color) {
    byte r = (byte)(color >>> 16 & 0xff);
    byte g = (byte)(color >>> 8 & 0xff);
    byte b = (byte)(color & 0xff);
    byte a = (byte)(color >>> 24);
    GL11.glColor4ub(r, g, b, a);
  }

  public static void drawCube(Vec3d... vertexs) {
    GL11.glBegin(GL11.GL_QUADS);
    drawQuad(vertexs, 0, 1, 2, 3);
    drawQuad(vertexs, 0, 1, 5, 4);
    drawQuad(vertexs, 1, 2, 6, 5);
    drawQuad(vertexs, 2, 3, 7, 6);
    drawQuad(vertexs, 3, 0, 4, 7);
    drawQuad(vertexs, 4, 5, 6, 7);
    GL11.glEnd();
  }

  public static void drawCubeReverseNormal(Vec3d... vertexs) {
    GL11.glBegin(GL11.GL_QUADS);
    drawQuad(vertexs, 0, 3, 2, 1);
    drawQuad(vertexs, 0, 4, 5, 1);
    drawQuad(vertexs, 1, 5, 6, 2);
    drawQuad(vertexs, 2, 6, 7, 3);
    drawQuad(vertexs, 3, 7, 4, 0);
    drawQuad(vertexs, 4, 7, 6, 5);
    GL11.glEnd();
  }

  public static void drawGlowingLine(double startX, double startY, double startZ, double endX, double endY, double endZ, int color) {
    Vec3d start = new Vec3d(startX, startY, startZ);
    Vec3d end = new Vec3d(endX, endY, endZ);
    Vec3d delta = end.subtract(start);
    Vec3d extend0 = Util.perpendicular(delta).normalize();
    Vec3d extend1 = delta.crossProduct(extend0).normalize();
    GlStateManager.disableTexture2D();
    color(0xffffffff);
    drawCube(
      start.add(extend0.scale(0.01)),
      start.add(extend1.scale(0.01)),
      start.add(extend0.scale(-0.01)),
      start.add(extend1.scale(-0.01)),
      end.add(extend0.scale(0.01)),
      end.add(extend1.scale(0.01)),
      end.add(extend0.scale(-0.01)),
      end.add(extend1.scale(-0.01))
    );
    color(color);
    drawCubeReverseNormal(
      start.add(extend0.scale(0.02)),
      start.add(extend1.scale(0.02)),
      start.add(extend0.scale(-0.02)),
      start.add(extend1.scale(-0.02)),
      end.add(extend0.scale(0.02)),
      end.add(extend1.scale(0.02)),
      end.add(extend0.scale(-0.02)),
      end.add(extend1.scale(-0.02))
    );
    GlStateManager.enableTexture2D();
  }

  public static void drawSquare(Vec3d point1, Vec3d point2, Vec3d point3, ResourceLocation texture) {
    Vec3d point4 = point1.subtract(point2).add(point3);
    Tessellator tess = Tessellator.getInstance();
    BufferBuilder buf = tess.getBuffer();
    Minecraft.getMinecraft().renderEngine.bindTexture(texture);
    buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
    buf.pos(point1.x, point1.y, point1.z).tex(0, 0).endVertex();
    buf.pos(point2.x, point2.y, point2.z).tex(0, 1).endVertex();
    buf.pos(point3.x, point3.y, point3.z).tex(1, 1).endVertex();
    buf.pos(point4.x, point4.y, point4.z).tex(1, 0).endVertex();
    tess.draw();
  }

  public static void drawGlowingRays(int color, double scale, int rays, float rotate) {
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferbuilder = tessellator.getBuffer();

    GlStateManager.disableLighting();
    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
    GlStateManager.disableCull();
    GlStateManager.disableTexture2D();
    GlStateManager.depthMask(false);
    GlStateManager.shadeModel(GL11.GL_SMOOTH);

    Random random = new Random(226);

    int r = color >> 16 & 0xff;
    int g = color >> 8 & 0xff;
    int b = color & 0xff;
    int a = color >> 24;

    for (int i = 0; i < rays; i++) {
      GlStateManager.rotate(random.nextFloat() * 360, 1, 0, 0);
      GlStateManager.rotate(random.nextFloat() * 360, 0, 1, 0);
      GlStateManager.rotate(random.nextFloat() * 360 + rotate, 0, 0, 1);
      double v = (random.nextFloat() * 0.1 + 0.9) * scale;
      bufferbuilder.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);
      bufferbuilder.pos(0, 0, 0).color(255, 255, 255, a).endVertex();
      bufferbuilder.pos(-0.05, 0.5 * v, 0).color(r, g, b, 0).endVertex();
      bufferbuilder.pos(0.05, 0.5 * v, 0).color(r, g, b, 0).endVertex();
      tessellator.draw();
    }

    GlStateManager.shadeModel(GL11.GL_FLAT);
    GlStateManager.depthMask(true);
    GlStateManager.enableTexture2D();
    GlStateManager.enableCull();
    GlStateManager.enableLighting();
  }

  public static void initModels() {
    OBJLoader.INSTANCE.addDomain(Mod.MODID);
    ModelLoaderRegistry.registerLoader(new PlaceholderLoader());
  }

  public static void setModels() {
    Blocks.SPIRIT_SMITHING_TABLE.setModel();
    Items.BASH.setModel();
    Items.ICON.setModel();
    Items.KUROS_FEATHER.setModel();
    Items.LIGHT_BURST.setModel();
    Items.SPIRIT_ARC.setModel();
    Items.SPIRIT_EDGE.setModel();
    Items.SPIRIT_FLAME.setModel();
    Items.DOUBLE_JUMP.setModel();
    Items.TRIPLE_JUMP.setModel();
    Items.STOMP.setModel();
    Items.WALL_JUMP.setModel();
    Items.CLIMB.setModel();
  }

  public static void loadModels() {
    Items.BASH.loadModel();
    Items.KUROS_FEATHER.loadModel();
    Items.LIGHT_BURST.loadModel();
    Items.SPIRIT_ARC.loadModel();
    Items.SPIRIT_EDGE.loadModel();
    Items.SPIRIT_FLAME.loadModel();
    Shard.loadModel();
    Arrow.Render.loadModel();
  }

  public static void setTexture(TextureMap map) {
    Shard.setTexture(map);
    Items.KUROS_FEATHER.setTexture(map);
    ChargeFlameParticle.setTexture(map);
    SpiritArcParticle.setTexture(map);
  }
}
