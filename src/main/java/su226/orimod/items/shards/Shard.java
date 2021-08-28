package su226.orimod.items.shards;

import java.util.List;
import java.util.function.BiPredicate;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import su226.orimod.ClientProxy;
import su226.orimod.Config;
import su226.orimod.capabilities.IEquipper.IEquipable;
import su226.orimod.items.Items;
import su226.orimod.others.Models;
import su226.orimod.others.Util;

public abstract class Shard extends Item implements IEquipable {
  public static class Render extends TileEntityItemStackRenderer {
    private Shard shard;

    public Render(Shard shard) {
      super();
      this.shard = shard;
    }

    @Override
    public void renderByItem(ItemStack stack, float unused) {
      if (Models.transform == TransformType.GUI) {
        this.drawShardGUI();
      } else {
        this.drawShard();
      }
    }

    private void drawShard() {
      GlStateManager.pushMatrix();
      Models.renderItemModel(MODEL, 0xcc192433);
      GlStateManager.disableLighting();
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
      double z = Config.ENABLE_3D ? 0.55 : 0.53126;
      Models.drawSquare(new Vec3d(0.296875, 0.59375, z), new Vec3d(0.296875, 0.1875, z), new Vec3d(0.703125, 0.1875, z), this.shard.texture);
      GlStateManager.enableLighting();
      GlStateManager.translate(0.5, 0.5, 0.5);
      float tick = ClientProxy.tick + Minecraft.getMinecraft().getRenderPartialTicks();
      Models.drawGlowingRays(0xffffffff, 1, 20, tick * 0.2f);
      GlStateManager.popMatrix();
    }

    private static final int FINENESS = 16;

    private void drawShardGUI() {
      GlStateManager.disableTexture2D();
      Models.color(0xff192433);
      GL11.glBegin(GL11.GL_POLYGON);
      for (int i = FINENESS; i > 0; i--) {
        GL11.glVertex2d(0.5 + 0.5 * Math.sin(Math.PI * 2 * i / FINENESS), 0.5 + 0.5 * Math.cos(Math.PI * 2 * i / FINENESS));
      }
      GL11.glEnd();
      if (this.shard.maxLevel == 1) {
        GL11.glColor3d(1, 1, 1);
        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
        for (int i = 0; i <= FINENESS; i++) {
          GL11.glVertex2d(0.5 + 0.5 * Math.sin(Math.PI * 2 * i / FINENESS), 0.5 + 0.5 * Math.cos(Math.PI * 2 * i / FINENESS));
          GL11.glVertex2d(0.5 + 0.4 * Math.sin(Math.PI * 2 * i / FINENESS), 0.5 + 0.4 * Math.cos(Math.PI * 2 * i / FINENESS));
        }
        GL11.glEnd();
      } else {
        double angle = Math.PI * 2 / this.shard.maxLevel;
        double stripAngle = angle - 0.2;
        int fineness = FINENESS / this.shard.maxLevel;
        for (int i = 0; i < this.shard.maxLevel; i++) {
          double minAngle = angle * i + 0.1;
          if (i >= this.shard.level) {
            GL11.glColor4d(1, 1, 1, 0.5);
          } else {
            GL11.glColor3d(1, 1, 1);
          }
          GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
          for (int j = 0; j <= fineness; j++) {
            GL11.glVertex2d(0.5 + 0.5 * Math.sin(minAngle + stripAngle * j / fineness), 0.5 + 0.5 * Math.cos(minAngle + stripAngle * j / fineness));
            GL11.glVertex2d(0.5 + 0.4 * Math.sin(minAngle + stripAngle * j / fineness), 0.5 + 0.4 * Math.cos(minAngle + stripAngle * j / fineness));
          }
          GL11.glEnd();
        }
      }
      GlStateManager.enableTexture2D();
      GL11.glColor3d(1, 1, 1);
      Models.drawSquare(new Vec3d(0.2, 0.8, 0), new Vec3d(0.2, 0.2, 0), new Vec3d(0.8, 0.2, 0), this.shard.texture);
    }
  }

  private static List<BakedQuad> MODEL;
  private String regName;
  private ResourceLocation texture;
  private int level;
  private int maxLevel;

  public Shard(String name, String texture, int level, int maxLevel) {
    super();
    this.regName = "shard_" + name;
    this.texture = Util.getLocation(String.format("textures/items/shards/%s.png", texture));
    this.level = level;
    this.maxLevel = maxLevel;
    this.setRegistryName(Util.getLocation(regName));
    this.setUnlocalizedName(Util.getI18nKey(regName));
    this.setCreativeTab(Items.CREATIVE_TAB);
    this.setMaxStackSize(1);
  }

  @Override
  public BiPredicate<ItemStack, EntityPlayer> getEquipableSlots() {
    return IEquipable.HOTBAR_AND_OFF_HAND;
  }

  @Override
  public void onUpdate(ItemStack stack, World world, Entity owner, int slot, boolean held) {
    this.updateEquipable(stack, owner);
  }

  @SideOnly(Side.CLIENT)
  public void setModel() {
    Models.setItemModel(this, "placeholder_with_transform");
    this.setTileEntityItemStackRenderer(new Render(this));
  }

  @SideOnly(Side.CLIENT)
  public static void loadModel() {
    if (Config.ENABLE_3D) {
      MODEL = Models.loadItemModel("item/3d/shard.obj");
    } else {
      MODEL = Models.loadItemModel("item/shard");
    }
  }

  public static void setTexture(TextureMap map) {
    map.registerSprite(Util.getLocation("items/shard"));
  }
}
