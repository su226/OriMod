package su226.orimod.entities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.registries.IForgeRegistry;
import su226.orimod.Config;
import su226.orimod.Mod;
import su226.orimod.items.Items;
import su226.orimod.items.LightBurst;
import su226.orimod.items.SpiritFlame;
import su226.orimod.messages.LightBurstMessage;
import su226.orimod.others.Models;
import su226.orimod.others.PureExplosion;
import su226.orimod.others.Util;

public class LightBurstEntity extends EntityThrowable {
  public static class Render extends net.minecraft.client.renderer.entity.Render<LightBurstEntity> {
    public static Factory FACTORY = new Factory();
    public static ItemStack STACK;

    public Render(RenderManager manager) {
      super(manager);
      if (!Config.ENABLE_3D) {
        STACK = new ItemStack(Items.LIGHT_BURST);
      }
    }

    @Override
    protected ResourceLocation getEntityTexture(LightBurstEntity entity) {
      return null;
    }

    @Override
    public void doRender(LightBurstEntity entity, double x, double y, double z, float yaw, float partialTicks) {
      GlStateManager.pushMatrix();
      GlStateManager.disableLighting();
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
      if (Config.ENABLE_3D) {
        GlStateManager.translate(x - 0.5, y - 0.5, z - 0.5);
        GlStateManager.enableCull();
        Models.renderItemModel(LightBurst.MODEL);
        GlStateManager.translate(-0.05, -0.05, -0.05);
        GlStateManager.scale(1.1, 1.1, 1.1);
        Models.renderItemModel(SpiritFlame.MODEL_OVERLAY, LightBurst.GLOW_COLOR);
      } else {
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(180 - renderManager.playerViewY, 0, 1, 0);
        GlStateManager.rotate((renderManager.options.thirdPersonView == 2 ? -1 : 1) * -renderManager.playerViewX, 1, 0, 0);
        Minecraft.getMinecraft().getRenderItem().renderItem(STACK, TransformType.GROUND);
      }
      GlStateManager.enableLighting();
      GlStateManager.popMatrix();
    }

    public static class Factory implements IRenderFactory<LightBurstEntity> {
      @Override
      public net.minecraft.client.renderer.entity.Render<? super LightBurstEntity> createRenderFor(RenderManager manager) {
        return new LightBurstEntity.Render(manager);
      }
    }
  }

  public LightBurstEntity(World world) {
    super(world);
  }
  
  public LightBurstEntity(World world, EntityLivingBase owner) {
    super(world, owner);
  }
  
  public static void register(IForgeRegistry<EntityEntry> registry) {
    registry.register(EntityEntryBuilder.create()
      .entity(LightBurstEntity.class)
      .id(Util.getLocation("light_burst"), Entities.nextId())
      .name(Util.getI18nKey("light_burst"))
      .tracker(128, 1, true)
      .build());
  }

  public static void registerRender() {
    RenderingRegistry.registerEntityRenderingHandler(LightBurstEntity.class, Render.FACTORY);
  }

  @Override
  public void onUpdate() {
    super.onUpdate();
  }

  @Override
  protected void onImpact(RayTraceResult ray) {
    if (!this.world.isRemote) {
      Vec3d hit;
      Vec3d pos = this.getPositionVector();
      if (ray.entityHit != null) {
        Vec3d end = pos.add(new Vec3d(this.motionX, this.motionY, this.motionZ));
        RayTraceResult ray2 = ray.entityHit.getEntityBoundingBox().calculateIntercept(pos, end);
        if (ray2 == null) {
          Mod.LOG.debug("Intercept is null! This should not happen!");
          hit = end;
        } else {
          hit = ray2.hitVec;
        }
        if (ray.entityHit != this.thrower) {
          ray.entityHit.attackEntityFrom(DamageSource.causeExplosionDamage(this.thrower), (float)Config.LIGHT_BURST.DAMAGE);
        }
      } else {
        hit = ray.hitVec;
      }
      new PureExplosion(this, this.thrower, Config.LIGHT_BURST.EXPLOSION_FORCE).doExplosionA();
      Mod.NETWORK.sendToAllAround(new LightBurstMessage(this.thrower, null, hit, pos), Util.getTargetPoint(this, 32));
      this.setDead();
    }
  }
}
