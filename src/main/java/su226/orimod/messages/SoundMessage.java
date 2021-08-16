package su226.orimod.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import su226.orimod.Mod;
import su226.orimod.others.Util;

public class SoundMessage implements IMessage {
  public static class Handler implements IMessageHandler<SoundMessage, IMessage> {
    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(SoundMessage message, MessageContext ctx) {
      Minecraft.getMinecraft().addScheduledTask(() -> {
        Util.playSound(message.entity, message.position, message.sound);
      });
      return null;
    }
  }

  public static void play(Entity ent, SoundEvent sound) {
    Mod.NETWORK.sendToAllAround(new SoundMessage(ent, ent.getPositionVector(), sound), Util.getTargetPoint(ent, 18));
  }

  public static void play(Entity ent, Vec3d position, SoundEvent sound) {
    Mod.NETWORK.sendToAllAround(new SoundMessage(ent, position, sound), Util.getTargetPoint(ent, 18));
  }

  private Entity entity;
  private Vec3d position;
  private SoundEvent sound;

  public SoundMessage() {}
  
  public SoundMessage(Entity ent, Vec3d position, SoundEvent sound) {
    this.entity = ent;
    this.position = position;
    this.sound = sound;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void fromBytes(ByteBuf buf) {
    this.entity = Minecraft.getMinecraft().world.getEntityByID(buf.readInt());
    this.position = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
    this.sound = SoundEvent.REGISTRY.getObjectById(buf.readInt());
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(this.entity.getEntityId());
    buf.writeDouble(this.position.x);
    buf.writeDouble(this.position.y);
    buf.writeDouble(this.position.z);
    buf.writeInt(SoundEvent.REGISTRY.getIDForObject(this.sound));
  }

  public static void register() {
    Mod.NETWORK.registerMessage(Handler.class, SoundMessage.class, Messages.nextId(), Side.CLIENT);
  }
}
