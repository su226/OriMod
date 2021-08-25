package su226.orimod.items.shards;

import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import su226.orimod.Mod;
import su226.orimod.ModClient;
import su226.orimod.items.Items;
import su226.orimod.others.ICustomRender;
import su226.orimod.others.Render;
import su226.orimod.others.Trinkets;
import su226.orimod.others.Util;

import java.util.ArrayList;
import java.util.List;

public abstract class Shard extends TrinketItem implements ICustomRender {
  public static final List<Identifier> TEXTURES = new ArrayList<>();
  private static final String MODEL = "item/3d/shard";
  private final String category;
  private final Identifier texture;
  private final int level;
  private final int maxLevel;

  public Shard(String category, String texture, int level, int maxLevel) {
    super(new Settings().group(Items.GROUP).maxCount(1));
    this.category = category;
    this.texture = Util.getIdentifier(String.format("item/shard/%s", texture));
    this.level = level;
    this.maxLevel = maxLevel;
    TEXTURES.add(this.texture);
  }

  @Override
  public boolean canWearInSlot(String group, String slot) {
    return Trinkets.SHARD_SLOTS.contains(slot);
  }

  @Override
  public void onEquip(PlayerEntity player, ItemStack stack) {
    Inventory inv = TrinketsApi.getTrinketsInventory(player);
    for (int i = 0; i < inv.size(); i++) {
      ItemStack stack1 = inv.getStack(i);
      Item item = stack1.getItem();
      if (stack1 != stack && item instanceof Shard && ((Shard)item).category.equals(this.category)) {
        if (!player.inventory.insertStack(stack)) {
          player.dropItem(stack, false);
        }
        break;
      }
    }
  }

  @Override
  public boolean applyCustomRender() {
    return true;
  }

  @Override
  public boolean applyDefaultTransform() {
    return true;
  }

  @Override
  public void render(ItemStack stack, ModelTransformation.Mode mode, boolean left, MatrixStack mat, VertexConsumerProvider consumers, int light, int overlay, BakedModel model) {
    if (mode == ModelTransformation.Mode.GUI) {
      this.renderGui(mat, consumers);
    } else {
      this.renderOther(mat, consumers, mode, light, model);
    }
  }

  private void renderOther(MatrixStack mat, VertexConsumerProvider consumers, ModelTransformation.Mode mode, int light, BakedModel model) {
    if (mode == ModelTransformation.Mode.FIXED) {
      mat.multiply(new Quaternion(0, 180, 0, true));
      mat.translate(-1, 0, -1);
    }
    VertexConsumer consumer = consumers.getBuffer(Render.SHARD_LAYER);
    if (Mod.CONFIG.enable_3d) {
      Render.model(mat, consumer, MODEL, 0xcc192433, 0, light);
    } else {
      for (BakedQuad quad : model.getQuads(null, null, null)) {
        Render.bakedQuad(mat, consumer, quad, 1, 1, 1, 1, 0, light);
      }
    }
    double z = Mod.CONFIG.enable_3d ? 0.55 : 0.53126;
    Render.square(mat, consumer, new Vec3d(0.296875, 0.59375, z), new Vec3d(0.296875, 0.1875, z), new Vec3d(0.703125, 0.1875, z), this.texture, 0xffffffff, light);
    float tick = ModClient.tick + MinecraftClient.getInstance().getTickDelta();
    mat.translate(0.5, 0.5, 0.5);
    Render.glowingRays(mat, consumer, 0xffffffff, 1, 20, tick * 0.2f);
  }

  private static final int FINENESS = 16;

  private void renderGui(MatrixStack mat, VertexConsumerProvider consumers) {
    VertexConsumer consumer = consumers.getBuffer(Render.SHARD_LAYER);
    Render.Circle circle = new Render.Circle();
    circle.edges(FINENESS).pos(0.5f, 0.5f, 0).outer(0.5f).color(0xff192433).light(Render.FULL_BRIGHT).solid(mat, consumer);
    circle.inner(0.4f).pos(0.5f, 0.5f, 1).color(0xffffffff);
    if (this.maxLevel == 1) {
      circle.outline(mat, consumer);
    } else {
      float angle = (float)Math.PI * 2 / this.maxLevel;
      float stripAngle = angle - 0.2f;
      int fineness = FINENESS / this.maxLevel;
      circle.edges(fineness);
      for (int i = 0; i < this.maxLevel; i++) {
        float minAngle = angle * i + 0.1f;
        if (i >= this.level) {
          circle.color(0x88ffffff);
        }
        circle.range(minAngle, minAngle + stripAngle).outline(mat, consumer);
      }
    }
    Render.square(mat, consumer, new Vec3d(0.2, 0.8, 2), new Vec3d(0.2, 0.2, 2), new Vec3d(0.8, 0.2, 2), this.texture, 0xffffffff, Render.FULL_BRIGHT);
  }
}
