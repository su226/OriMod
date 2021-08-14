package su226.orimod.entities;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketCollectItem;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.registries.IForgeRegistry;
import su226.orimod.capabilities.Capabilities;
import su226.orimod.capabilities.ISpiritLight;
import su226.orimod.others.Util;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

public class SpiritLightOrb extends Entity {
  public static class Render extends net.minecraft.client.renderer.entity.Render<SpiritLightOrb> {
    public static final Factory FACTORY = new Factory();
    public static final ResourceLocation TEXTURE = Util.getLocation("textures/entity/spirit_light.png");

    private Render(RenderManager manager) {
      super(manager);
      this.shadowSize = 0.15F;
      this.shadowOpaque = 0.75F;
    }

    @Override
    public void doRender(@Nonnull SpiritLightOrb entity, double x, double y, double z, float yaw, float partialTicks) {
      if (!this.renderOutlines) {
        GlStateManager.disableLighting();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        bindEntityTexture(entity);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0f);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);

        GlStateManager.translate(0, 0.1, 0);
        GlStateManager.rotate(180 - renderManager.playerViewY, 0, 1, 0);
        GlStateManager.rotate((renderManager.options.thirdPersonView == 2 ? -1 : 1) * -renderManager.playerViewX, 1, 0, 0);
        GlStateManager.rotate((partialTicks + entity.ticksExisted) * 3, 0, 0, 1);
        double scale = entity.value * 0.025 + 0.2;
        GlStateManager.scale(scale, scale, scale);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(-1, -1, 0.0D).tex(0, 0).endVertex();
        bufferbuilder.pos(1, -1, 0.0D).tex(1, 0).endVertex();
        bufferbuilder.pos(1, 1, 0.0D).tex(1, 1).endVertex();
        bufferbuilder.pos(-1, 1, 0.0D).tex(0, 1).endVertex();
        tessellator.draw();

        GlStateManager.disableBlend();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.popMatrix();
        GlStateManager.enableLighting();
      }
      super.doRender(entity, x, y, z, yaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(@Nonnull SpiritLightOrb entity) {
      return TEXTURE;
    }

    public static class Factory implements IRenderFactory<SpiritLightOrb> {
      @Override
      public net.minecraft.client.renderer.entity.Render<? super SpiritLightOrb> createRenderFor(RenderManager manager) {
        return new Render(manager);
      }
    }
  }

  private static final int MAX_AGE = 6000;
  private static final double SEARCH_DISTANCE = 8;
  public int value;
  private int lastCheck;
  private int age;
  private int health = 5;
  private EntityPlayer closest;

  public SpiritLightOrb(World world, double x, double y, double z, int value) {
    super(world);
    this.setSize(0.25F, 0.25F);
    this.setPosition(x, y, z);
    this.rotationYaw = Util.rand(360);
    this.motionX = Util.rand(-0.2f, 0.2f);
    this.motionY = Util.rand(0.4f);
    this.motionZ = Util.rand(-0.2f, 0.2f);
    this.value = value;
  }

  public SpiritLightOrb(World world) {
    super(world);
    this.setSize(0.25F, 0.25F);
  }

  @Override
  protected boolean canTriggerWalking() {
    return false;
  }

  @Override
  protected void entityInit() {}

  @Override
  public void onUpdate() {
    super.onUpdate();

    this.prevPosX = this.posX;
    this.prevPosY = this.posY;
    this.prevPosZ = this.posZ;

    if (!this.hasNoGravity()) {
      this.motionY -= 0.03;
    }

    if (this.world.getBlockState(new BlockPos(this)).getMaterial() == Material.LAVA) {
      this.motionY = 0.2;
      this.motionX = (this.rand.nextDouble() - this.rand.nextDouble()) * 0.2;
      this.motionZ = (this.rand.nextDouble() - this.rand.nextDouble()) * 0.2;
      this.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
    }

    AxisAlignedBB bb = this.getEntityBoundingBox();
    this.pushOutOfBlocks(this.posX, (bb.minY + bb.maxY) / 2, this.posZ);

    if (this.lastCheck < this.age - 20 + this.getEntityId() % 100) {
      if (this.closest == null || this.closest.getDistanceSq(this) > SEARCH_DISTANCE) {
        this.closest = this.world.getClosestPlayerToEntity(this, SEARCH_DISTANCE);
      }
      this.lastCheck = this.age;
    }

    if (this.closest != null && this.closest.isSpectator()) {
      this.closest = null;
    }

    if (this.closest != null) {
      double distanceX = (this.closest.posX - this.posX) / 8.0;
      double distanceY = (this.closest.posY + this.closest.getEyeHeight() / 2.0 - this.posY) / 8.0;
      double distanceZ = (this.closest.posZ - this.posZ) / 8.0;
      double distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ);
      if (distance < 1) {
        double coefficient = closest.isSneaking() ? 3.0 : 0.1;
        double offset = 1 - distance;
        offset *= offset;
        this.motionX += distanceX / distance * offset * coefficient;
        this.motionY += distanceY / distance * offset * coefficient;
        this.motionZ += distanceZ / distance * offset * coefficient;
      }
    }

    this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);

    float slipperiness = 0.98F;
    if (this.onGround) {
      BlockPos underPos = new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(bb.minY) - 1, MathHelper.floor(this.posZ));
      IBlockState underState = this.world.getBlockState(underPos);
      slipperiness *= underState.getBlock().getSlipperiness(underState, this.world, underPos, this);
    }

    this.motionX *= slipperiness;
    this.motionY *= 0.98;
    this.motionZ *= slipperiness;

    if (this.onGround) {
      this.motionY *= -0.9;
    }

    if (++this.age >= MAX_AGE) {
      this.setDead();
    }
  }

  @Override
  public boolean handleWaterMovement() {
    return this.world.handleMaterialAcceleration(this.getEntityBoundingBox(), Material.WATER, this);
  }

  @Override
  protected void dealFireDamage(int amount) {
    this.attackEntityFrom(DamageSource.IN_FIRE, amount);
  }

  @Override
  public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
    if (!this.world.isRemote && !this.isDead && !this.isEntityInvulnerable(source)) {
      this.markVelocityChanged();
      this.health = (int) (this.health - amount);
      if (this.health <= 0) {
        this.setDead();
      }
    }
    return false;
  }

  @Override
  public void writeEntityToNBT(@Nonnull NBTTagCompound compound) {
    compound.setInteger("Health", this.health);
    compound.setInteger("Age", this.age);
    compound.setInteger("Value", this.value);
  }

  @Override
  public void readEntityFromNBT(@Nonnull NBTTagCompound compound) {
    this.health = compound.getInteger("Health");
    this.age = compound.getInteger("Age");
    this.value = compound.getInteger("Value");
  }

  @Override
  public boolean canBeAttackedWithItem() {
    return false;
  }

  @Override
  public void onCollideWithPlayer(@Nonnull EntityPlayer player) {
    if (!this.world.isRemote) {
      ISpiritLight light = player.getCapability(Capabilities.SPIRIT_LIGHT, null);
      if (player.xpCooldown == 0) {
        player.xpCooldown = 2;
        if (!this.isDead && !this.world.isRemote) {
          EntityTracker entitytracker = ((WorldServer) this.world).getEntityTracker();
          entitytracker.sendToTracking(this, new SPacketCollectItem(this.getEntityId(), player.getEntityId(), 1));
        }
        if (this.value > 0) {
          light.collect(this.value);
        }
        this.setDead();
      }
    }
  }
  
  public static void register(IForgeRegistry<EntityEntry> registry) {
    registry.register(EntityEntryBuilder.create()
      .entity(SpiritLightOrb.class)
      .id(Util.getLocation("spirit_light"), Entities.nextId())
      .name(Util.getI18nKey("spirit_light"))
      .tracker(128, 1, true)
      .build());
  }

  public static void registerRender() {
    RenderingRegistry.registerEntityRenderingHandler(SpiritLightOrb.class, Render.FACTORY);
  }
}