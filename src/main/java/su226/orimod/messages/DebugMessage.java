package su226.orimod.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import su226.orimod.Mod;
import su226.orimod.particles.DebugParticle;

public class DebugMessage implements IMessage {
  public static class Handler implements IMessageHandler<DebugMessage, IMessage> {
    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(DebugMessage message, MessageContext ctx) {
      Minecraft mc = Minecraft.getMinecraft();
      mc.addScheduledTask(() -> {
        mc.effectRenderer.addEffect(new DebugParticle(mc.world, message.from, message.to, message.color));
      });
      return null;
    }
  }

  private Vec3d from;
  private Vec3d to;
  private int color;

  public DebugMessage() {}
  
  public DebugMessage(Vec3d from, Vec3d to, int color) {
    this.from = from;
    this.to = to;
    this.color = color;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void fromBytes(ByteBuf buf) {
    this.from = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
    this.to = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
    this.color = buf.readInt();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeDouble(this.from.x);
    buf.writeDouble(this.from.y);
    buf.writeDouble(this.from.z);
    buf.writeDouble(this.to.x);
    buf.writeDouble(this.to.y);
    buf.writeDouble(this.to.z);
    buf.writeInt(this.color);
  }

  public static void register() {
    Mod.NETWORK.registerMessage(Handler.class, DebugMessage.class, Messages.nextId(), Side.CLIENT);
  }
}
