package su226.orimod.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import su226.orimod.Mod;
import su226.orimod.capabilities.Capabilities;

public class MultiJumpMessage implements IMessage {
  public static class Handler implements IMessageHandler<MultiJumpMessage, IMessage> {
    @Override
    public IMessage onMessage(MultiJumpMessage message, MessageContext ctx) {
      EntityPlayerMP player = ctx.getServerHandler().player;
      player.getServerWorld().addScheduledTask(() -> {
        if (player.getCapability(Capabilities.MULTI_JUMP, null).doJump()) {
          player.jump();
          player.velocityChanged = true;
          Mod.NETWORK.sendToDimension(new MultiJumpEffectMessage(player), player.dimension);
        }
      });
      return null;
    }
  }

  public MultiJumpMessage() {}

  @Override
  public void fromBytes(ByteBuf buf) {}

  @Override
  public void toBytes(ByteBuf buf) {}

  public static void register() {
    Mod.NETWORK.registerMessage(Handler.class, MultiJumpMessage.class, Messages.nextId(), Side.SERVER);
  }
}
