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
import su226.orimod.Mod;
import su226.orimod.others.Sounds;
import su226.orimod.others.Util;

public class MultiJumpEffectMessage implements IMessage {
  public static class Handler implements IMessageHandler<MultiJumpEffectMessage, IMessage> {
    private static final int PARTICLE_COUNT = 10;

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(MultiJumpEffectMessage message, MessageContext ctx) {
      Minecraft mc = Minecraft.getMinecraft();
      mc.addScheduledTask(() -> {
        Util.playSound(message.entity, Sounds.MULTI_JUMP);
        Vec3d delta = new Vec3d(0, -1, 0);
        Vec3d move = new Vec3d(1, 0, 0);
        for (int i = 0; i < PARTICLE_COUNT; i++) {
          Vec3d velocity = delta.add(move.rotateYaw(Util.randAngle(2)).scale(Math.random())).normalize().scale(Math.random());
          mc.world.spawnParticle(EnumParticleTypes.CLOUD, message.entity.posX, message.entity.posY, message.entity.posZ, velocity.x, velocity.y, velocity.z);
        }
      });
      return null;
    }
  }

  private Entity entity;

  public MultiJumpEffectMessage() {}
  
  public MultiJumpEffectMessage(Entity entity) {
    this.entity = entity;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void fromBytes(ByteBuf buf) {
    this.entity = Minecraft.getMinecraft().world.getEntityByID(buf.readInt());
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(this.entity.getEntityId());
  }

  public static void register() {
    Mod.NETWORK.registerMessage(Handler.class, MultiJumpEffectMessage.class, Messages.nextId(), Side.CLIENT);
  }
}
