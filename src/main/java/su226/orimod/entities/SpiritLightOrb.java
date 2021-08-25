package su226.orimod.entities;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import su226.orimod.components.Components;
import su226.orimod.others.IEntitySync;
import su226.orimod.others.Util;

public class SpiritLightOrb extends Entity implements IEntitySync {
  public static class Render extends EntityRenderer<SpiritLightOrb> {
    public static final Identifier TEXTURE = Util.getIdentifier("textures/entity/spirit_light.png");

    public Render(EntityRenderDispatcher dispatcher, EntityRendererRegistry.Context context) {
      super(dispatcher);
      this.shadowRadius = 0.15f;
      this.shadowOpacity = 0.75f;
    }

    @Override
    protected int getBlockLight(SpiritLightOrb entity, BlockPos pos) {
      return 15;
    }

    @Override
    public void render(SpiritLightOrb entity, float yaw, float tickDelta, MatrixStack mat, VertexConsumerProvider consumers, int light) {
      mat.push();
      mat.translate(0, 0.25, 0);
      mat.multiply(this.dispatcher.getRotation());
      Vector4f up = new Vector4f(0, 0, 1, 0);
      up.transform(mat.peek().getModel());
      mat.multiply(new Vec3f(up.getX(), up.getY(), up.getZ()).getDegreesQuaternion((entity.orbAge + tickDelta) * 3));
      mat.multiply(new Quaternion(0, 180, 0, true));
      float scale = entity.amount * 0.025f + 0.2f;
      mat.scale(scale, scale, scale);
      GlStateManager.enableDepthTest();
      GlStateManager.enableBlend();
      // New method cannot full-bright, I want to know why.
      su226.orimod.others.Render.Legacy.square(mat, new Vec3d(-1, -1, 0), new Vec3d(1, -1, 0), new Vec3d(1, 1, 0), TEXTURE);
      GlStateManager.disableBlend();
      GlStateManager.disableDepthTest();
      mat.pop();
    }

    @Override
    public Identifier getTexture(SpiritLightOrb entity) {
      return TEXTURE;
    }
  }
  private static final int MAX_AGE = 6000;
  private static final double SEARCH_DISTANCE = 8;
  private int amount;
  private int lastCheck;
  private int orbAge;
  private int health = 5;
  private PlayerEntity target;

  public SpiritLightOrb(Entity from, int amount) {
    super(Entities.SPIRIT_LIGHT_ORB, from.world);
    this.setPosition(from.getX(), from.getY(), from.getZ());
    this.yaw = Util.rand(360);
    this.setVelocity(Util.rand(-0.2, 0.2), Util.rand(0.4), Util.rand(-0.2, 0.2));
    this.amount = amount;
  }

  public SpiritLightOrb(EntityType<? extends SpiritLightOrb> type, World world) {
    super(type, world);
  }

  @Override
  protected boolean canClimb() {
    return false;
  }

  @Override
  protected void initDataTracker() {}

  @Override
  public void tick() {
    super.tick();

    this.prevX = this.getX();
    this.prevY = this.getY();
    this.prevZ = this.getZ();
    if (this.isSubmergedIn(FluidTags.WATER)) {
      this.applyWaterMovement();
    } else if (!this.hasNoGravity()) {
      this.setVelocity(this.getVelocity().add(0.0, -0.03, 0.0));
    }

    if (this.world.getFluidState(this.getBlockPos()).isIn(FluidTags.LAVA)) {
      this.setVelocity((this.random.nextDouble() - this.random.nextDouble()) * 0.2, 0.2, (this.random.nextDouble() - this.random.nextDouble()) * 0.2);
      this.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4f, 2 + this.random.nextFloat() * 0.4f);
    }

    if (!this.world.isSpaceEmpty(this.getBoundingBox())) {
      this.pushOutOfBlocks(this.getX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2, this.getZ());
    }

    if (this.lastCheck < this.orbAge - 20 + this.getEntityId() % 100) {
      if (this.target == null || this.target.squaredDistanceTo(this) > SEARCH_DISTANCE * SEARCH_DISTANCE) {
        this.target = this.world.getClosestPlayer(this, 8);
      }
      this.lastCheck = this.orbAge;
    }

    if (this.target != null && this.target.isSpectator()) {
      this.target = null;
    }

    if (this.target != null) {
      Vec3d vec3d = new Vec3d(this.target.getX() - this.getX(), this.target.getY() + this.target.getStandingEyeHeight() / 2 - this.getY(), this.target.getZ() - this.getZ());
      double squared = vec3d.lengthSquared();
      if (squared < SEARCH_DISTANCE * SEARCH_DISTANCE) {
        double f = 1 - Math.sqrt(squared) / 8;
        this.setVelocity(this.getVelocity().add(vec3d.normalize().multiply(f * f * 0.1)));
      }
    }

    this.move(MovementType.SELF, this.getVelocity());
    float fraction = 0.98f;
    if (this.onGround) {
      fraction = this.world.getBlockState(new BlockPos(this.getX(), this.getY() - 1.0D, this.getZ())).getBlock().getSlipperiness() * 0.98f;
    }

    this.setVelocity(this.getVelocity().multiply(fraction, 0.98, fraction));
    if (this.onGround) {
      this.setVelocity(this.getVelocity().multiply(1, -0.9, 1));
    }

    if (++this.orbAge >= MAX_AGE) {
      this.remove();
    }
  }

  private void applyWaterMovement() {
    Vec3d vec3d = this.getVelocity();
    this.setVelocity(vec3d.x * 0.99, Math.min(vec3d.y + 0.0005, 0.05), vec3d.z * 0.99);
  }

  @Override
  protected void onSwimmingStart() {}

  @Override
  public boolean damage(DamageSource source, float amount) {
    if (!this.isInvulnerableTo(source)) {
      this.scheduleVelocityUpdate();
      this.health = (int)(this.health - amount);
      if (this.health <= 0) {
        this.remove();
      }
    }
    return false;
  }

  @Override
  public void writeCustomDataToNbt(NbtCompound nbt) {
    nbt.putShort("Health", (short)this.health);
    nbt.putShort("Age", (short)this.orbAge);
    nbt.putShort("Value", (short)this.amount);
  }

  @Override
  public void readCustomDataFromNbt(NbtCompound nbt) {
    this.health = nbt.getShort("Health");
    this.orbAge = nbt.getShort("Age");
    this.amount = nbt.getShort("Value");
  }

  @Override
  public void onPlayerCollision(PlayerEntity player) {
    if (!this.world.isClient && player.experiencePickUpDelay == 0) {
      player.experiencePickUpDelay = 2;
      Components.SPIRIT_LIGHT.get(player).collect(this.amount);
      this.remove();
    }
  }

  @Override
  public boolean isAttackable() {
    return false;
  }

  @Override
  public Packet<?> createSpawnPacket() {
    return new EntitySpawnS2CPacket(this, this.amount);
  }

  @Override
  public void applySyncData(int data) {
    this.amount = data;
  }
}