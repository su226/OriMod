package su226.orimod.items;

import java.util.List;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import su226.orimod.Config;
import su226.orimod.blocks.SpiritSmithingTable;
import su226.orimod.others.Models;
import su226.orimod.others.Util;

public class SpiritEdge extends ItemSword {
  private static class Render extends TileEntityItemStackRenderer {
    @Override
    public void renderByItem(ItemStack stack, float unused) {
      GlStateManager.disableLighting();
      GlStateManager.enableCull();
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
      Models.renderItemModel(MODEL);
      Models.renderItemModel(MODEL_OVERLAY, Config.GLOW_COLOR);
      GlStateManager.enableLighting();
    }
  }

  public static final ToolMaterial MATERIAL = EnumHelper.addToolMaterial("spirit", Config.TOOLS.HARVEST_LEVEL, 0, (float)Config.TOOLS.EFFICIENCY, (float)Config.TOOLS.DAMAGE, Config.TOOLS.ENCHANTABILITY);
  private static List<BakedQuad> MODEL;
  private static List<BakedQuad> MODEL_OVERLAY;

  public SpiritEdge() {
    super(MATERIAL);
    this.setRegistryName(Util.getLocation("spirit_edge"));
    this.setUnlocalizedName(Util.getI18nKey("spirit_edge"));
    this.setCreativeTab(Items.CREATIVE_TAB);
    SpiritSmithingTable.registerRecipe(new SpiritSmithingTable.Recipe(
      new ItemStack(net.minecraft.init.Items.DIAMOND_SWORD),
      new ItemStack(this),
      300
    ));
  }

  @SideOnly(Side.CLIENT)
  public void setModel() {
    if (Config.ENABLE_3D) {
      Models.setItemModel(this, "placeholder");
      this.setTileEntityItemStackRenderer(new Render());
    } else {
      Models.setItemModel(this, "spirit_edge");
    }
  }

  @SideOnly(Side.CLIENT)
  public void loadModel() {
    if (Config.ENABLE_3D) {
      MODEL = Models.loadItemModel("item/3d/spirit_edge.obj");
      MODEL_OVERLAY = Models.loadItemModel("item/3d/spirit_edge_overlay.obj");
    }
  }
}
