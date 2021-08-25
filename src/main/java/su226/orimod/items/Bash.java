package su226.orimod.items;

import net.minecraft.client.render.*;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import su226.orimod.Mod;
import su226.orimod.blocks.SpiritSmithingTable;
import su226.orimod.components.Components;
import su226.orimod.others.ICustomRender;
import su226.orimod.others.Render;
import su226.orimod.others.Sounds;
import su226.orimod.others.Util;

public class Bash extends Item implements ICustomRender {
  private static final String MODEL = "item/3d/bash";
  private static final String MODEL_OVERLAY = "item/3d/bash_overlay";

  public Bash() {
    super(new Settings().group(Items.GROUP).maxCount(1));
    SpiritSmithingTable.registerRecipe(new SpiritSmithingTable.Recipe(
      new ItemStack(net.minecraft.block.Blocks.PISTON),
      new ItemStack(this),
      300
    ));
  }

  @Override
  public void render(ItemStack stack, ModelTransformation.Mode mode, boolean left, MatrixStack mat, VertexConsumerProvider consumers, int light, int overlay, BakedModel model) {
    if (!left) {
      mat.multiply(new Quaternion(0, 180, 0, true));
      mat.translate(-1, 0, -1);
    }
    VertexConsumer consumer = consumers.getBuffer(RenderLayer.getTranslucent());
    Render.model(mat, consumer, MODEL, 0xffffffff, 0, 15728880);
    Render.model(mat, consumer, MODEL_OVERLAY, Mod.CONFIG.glow_color, 0, 15728880);
  }

  @Override
  public int getMaxUseTime(ItemStack stack) {
    return Mod.CONFIG.bash.timeout;
  }

  @Override
  public UseAction getUseAction(ItemStack stack) {
    return UseAction.BOW;
  }

  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity owner, Hand hand) {
    ItemStack stack = owner.getStackInHand(hand);
    if (!world.isClient) {
      Entity ent = Util.rayTraceEntity(owner, Mod.CONFIG.bash.range, null);
      if (ent != null) {
        owner.setNoGravity(true);
        ent.setNoGravity(true);
        Util.setVelocity(owner, 0, 0, 0,true);
        Util.setVelocity(ent, 0, 0, 0, true);
        Components.BASH.get(stack).setEntity(ent);
        if (ent instanceof MobEntity) {
          ((MobEntity)ent).setAiDisabled(true);
        }
        owner.setCurrentHand(hand);
        world.playSound(null, owner.getX(), owner.getY(), owner.getZ(), Sounds.BASH_START, SoundCategory.PLAYERS, 1, 1);
        return TypedActionResult.success(stack);
      } else {
        world.playSound(null, owner.getX(), owner.getY(), owner.getZ(), Sounds.BASH_NO_TARGET, SoundCategory.PLAYERS, 1, 1);
      }
    }
    return TypedActionResult.fail(stack);
  }

  @Override
  public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
    if (!world.isClient) {
      Entity ent = Components.BASH.get(stack).getEntity();
      Util.setVelocity(user, 0, 0, 0, true);
      Util.setVelocity(ent, 0, 0, 0, true);
      if (remainingUseTicks == 25) {
        world.playSound(null, user.getX(), user.getY(), user.getZ(), Sounds.BASH_TIMEOUT, SoundCategory.PLAYERS, 1, 1);
      }
    }
  }

  @Override
  public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
    this.finishBash(stack, user);
    return stack;
  }

  @Override
  public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
    this.finishBash(stack, user);
  }

  private void finishBash(ItemStack stack, Entity owner) {
    if (!owner.world.isClient) {
      Entity ent = Components.BASH.get(stack).getEntity();
      if (ent instanceof MobEntity) {
        ((MobEntity)ent).setAiDisabled(false);
      }
      Vec3d vec = owner.getRotationVec(1).multiply(Mod.CONFIG.bash.multiplier);
      owner.setNoGravity(false);
      ent.setNoGravity(false);
      Util.setVelocity(owner, vec.x, vec.y, vec.z, true);
      Util.setVelocity(ent, -vec.x, -vec.y, -vec.z, true);
      owner.world.playSound(null, owner.getX(), owner.getY(), owner.getZ(), Sounds.BASH_END, SoundCategory.PLAYERS, 1, 1);
    }
  }
}
