package su226.orimod.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import su226.orimod.others.Util;

public abstract class ProjectileHitMessage implements IMessage {
  public static abstract class Handler<T extends ProjectileHitMessage> implements IMessageHandler<T, IMessage> {
    private static final int PARTICLE_COUNT = 10;

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(T message, MessageContext ctx) {
      Minecraft.getMinecraft().addScheduledTask(() -> {
        if (message.hit != null) {
          Util.playSound(message.hit, message.start, message.getHitEntitySound());
        } else {
          Util.playSound(message.owner, message.start, message.getHitGroundSound());
        }
        Vec3d delta = message.end.subtract(message.start).normalize();
        Vec3d move = Util.perpendicular(delta).normalize();
        for (int i = 0; i < PARTICLE_COUNT; i++) {
          Vec3d velocity = delta.add(Util.rotate(delta, move, Util.randAngle(2)).scale(Math.random())).normalize().scale(Math.random());
          message.spawnParticle(velocity);
        }
        message.callback();
      });
      return null;
    }
  }

  protected Entity owner;
  protected Entity hit;
  protected Vec3d start;
  protected Vec3d end;

  public ProjectileHitMessage() {}
  
  public ProjectileHitMessage(Entity owner, Entity hit, Vec3d start, Vec3d end) {
    this.owner = owner;
    this.hit = hit;
    this.start = start;
    this.end = end;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void fromBytes(ByteBuf buf) {
    World world = Minecraft.getMinecraft().world;
    this.owner = world.getEntityByID(buf.readInt());
    int hitId = buf.readInt();
    this.hit = hitId == -1 ? null : world.getEntityByID(hitId);
    this.start = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
    this.end = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(this.owner.getEntityId());
    buf.writeInt(this.hit == null ? -1 : this.hit.getEntityId());
    buf.writeDouble(this.start.x);
    buf.writeDouble(this.start.y);
    buf.writeDouble(this.start.z);
    buf.writeDouble(this.end.x);
    buf.writeDouble(this.end.y);
    buf.writeDouble(this.end.z);
  }

  public abstract void spawnParticle(Vec3d velocity);
  public abstract SoundEvent getHitEntitySound();
  public abstract SoundEvent getHitGroundSound();
  public void callback() {}
}