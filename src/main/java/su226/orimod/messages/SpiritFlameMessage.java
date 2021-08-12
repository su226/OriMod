package su226.orimod.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import su226.orimod.Mod;
import su226.orimod.others.Sounds;
import su226.orimod.others.Util;
import su226.orimod.particles.SpiritFlameParticle;

public class SpiritFlameMessage implements IMessage {
  public static class Handler implements IMessageHandler<SpiritFlameMessage, IMessage> {
    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(SpiritFlameMessage message, MessageContext ctx) {
      Minecraft mc = Minecraft.getMinecraft();
      mc.addScheduledTask(() -> {
        if (message.isVec) {
          mc.effectRenderer.addEffect(new SpiritFlameParticle(mc.world, message.from, message.offset));
        } else {
          mc.effectRenderer.addEffect(new SpiritFlameParticle(mc.world, message.from, message.to));
          Util.playSound(message.to, Sounds.SPIRIT_FLAME_HIT);
        }
        Util.playSound(message.from, Sounds.SPIRIT_FLAME_THROW);
      });
      return null;
    }
  }

  private Entity from;
  private boolean isVec;
  private Entity to;
  private Vec3d offset;

  public SpiritFlameMessage() {}
  
  public SpiritFlameMessage(Entity from, Entity to) {
    this.isVec = false;
    this.from = from;
    this.to = to;
  }
  
  public SpiritFlameMessage(Entity from, Vec3d offset) {
    this.isVec = true;
    this.from = from;
    this.offset = offset;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void fromBytes(ByteBuf buf) {
    World world = Minecraft.getMinecraft().world;
    this.from = world.getEntityByID(buf.readInt());
    this.isVec = buf.readBoolean();
    if (this.isVec) {
      this.offset = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
    } else {
      this.to = world.getEntityByID(buf.readInt());
    }
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(this.from.getEntityId());
    buf.writeBoolean(this.isVec);
    if (this.isVec) {
      buf.writeDouble(this.offset.x);
      buf.writeDouble(this.offset.y);
      buf.writeDouble(this.offset.z);
    } else {
      buf.writeInt(this.to.getEntityId());
    }
  }

  public static void register() {
    Mod.NETWORK.registerMessage(Handler.class, SpiritFlameMessage.class, Messages.nextId(), Side.CLIENT);
  }
}
