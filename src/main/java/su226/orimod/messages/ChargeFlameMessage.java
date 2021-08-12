package su226.orimod.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import su226.orimod.Mod;
import su226.orimod.others.Sounds;
import su226.orimod.others.Util;
import su226.orimod.particles.ChargeFlameParticle;

public class ChargeFlameMessage implements IMessage {
  public static class Handler implements IMessageHandler<ChargeFlameMessage, IMessage> {
    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(ChargeFlameMessage message, MessageContext ctx) {
      Minecraft mc = Minecraft.getMinecraft();
      mc.addScheduledTask(() -> {
        for (int i = 0; i < 100; i++) {
          Vec3d velocity = new Vec3d(1, 0, 0).rotateYaw(Util.randAngle(2f)).rotatePitch(Util.randAngle(0.5f));
          mc.effectRenderer.addEffect(new ChargeFlameParticle(mc.world, message.pos, velocity));
        }
        Util.playSound(message.owner, message.pos, Sounds.CHARGE_FLAME_END);
      });
      return null;
    }
  }

  private Entity owner;
  private Vec3d pos;

  public ChargeFlameMessage() {}
  
  public ChargeFlameMessage(Entity owner, Vec3d pos) {
    this.owner = owner;
    this.pos = pos;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void fromBytes(ByteBuf buf) {
    this.owner = Minecraft.getMinecraft().world.getEntityByID(buf.readInt());
    this.pos = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(this.owner.getEntityId());
    buf.writeDouble(this.pos.x);
    buf.writeDouble(this.pos.y);
    buf.writeDouble(this.pos.z);
  }

  public static void register() {
    Mod.NETWORK.registerMessage(Handler.class, ChargeFlameMessage.class, Messages.nextId(), Side.CLIENT);
  }
}
