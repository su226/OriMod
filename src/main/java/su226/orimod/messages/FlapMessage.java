package su226.orimod.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import su226.orimod.Config;
import su226.orimod.Mod;
import su226.orimod.others.Util;

public class FlapMessage implements IMessage {
  public static class Handler implements IMessageHandler<FlapMessage, IMessage> {
    private static final int EXTEND_FINENESS = 10;
    private static final int ROTATE_FINENESS = 10;
    private static final int RADIAL_FINENESS = 2;

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(FlapMessage message, MessageContext ctx) {
      Minecraft mc = Minecraft.getMinecraft();
      mc.addScheduledTask(() -> {
        Vec3d delta = message.end.subtract(message.start).normalize();
        Vec3d base = Util.perpendicular(delta);
        for (int i = 0; i < EXTEND_FINENESS; i++) {
          Vec3d velocity = delta.scale(Config.KUROS_FEATHER.FORCE * i / EXTEND_FINENESS);
          for (int j = 0; j < ROTATE_FINENESS; j++) {
            Vec3d rotate = Util.rotate(delta, base, Math.PI * 2 * j / ROTATE_FINENESS).normalize();
            for (int k = 1; k <= RADIAL_FINENESS; k++) {
              Vec3d point = rotate.scale(Config.KUROS_FEATHER.RANGE * k / RADIAL_FINENESS).add(message.start);
              // mc.effectRenderer.addEffct(new DebugParticle(mc.world, point, point.add(velocity), 0xff0000));;
              mc.world.spawnParticle(EnumParticleTypes.CLOUD, point.x, point.y, point.z, velocity.x, velocity.y, velocity.z);
            }
          }
        }
      });
      return null;
    }
  }

  private Entity owner;
  private Vec3d start;
  private Vec3d end;

  public FlapMessage() {}
  
  public FlapMessage(Entity owner, Vec3d start, Vec3d end) {
    this.owner = owner;
    this.start = start;
    this.end = end;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void fromBytes(ByteBuf buf) {
    this.owner = Minecraft.getMinecraft().world.getEntityByID(buf.readInt());
    this.start = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
    this.end = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(this.owner.getEntityId());
    buf.writeDouble(this.start.x);
    buf.writeDouble(this.start.y);
    buf.writeDouble(this.start.z);
    buf.writeDouble(this.end.x);
    buf.writeDouble(this.end.y);
    buf.writeDouble(this.end.z);
  }

  public static void register() {
    Mod.NETWORK.registerMessage(Handler.class, FlapMessage.class, Messages.nextId(), Side.CLIENT);
  }
}
