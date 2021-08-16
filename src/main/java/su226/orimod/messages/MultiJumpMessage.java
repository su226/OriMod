package su226.orimod.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import su226.orimod.Config;
import su226.orimod.Mod;
import su226.orimod.capabilities.Capabilities;
import su226.orimod.others.Util;

public class MultiJumpMessage implements IMessage {
  public static class Handler implements IMessageHandler<MultiJumpMessage, IMessage> {
    @Override
    public IMessage onMessage(MultiJumpMessage message, MessageContext ctx) {
      EntityPlayerMP player = ctx.getServerHandler().player;
      player.getServerWorld().addScheduledTask(() -> {
        if (player.getCapability(Capabilities.COOLDOWN, null).doAction("multi_jump")) {
          player.jump();
          player.motionY *= Config.JUMP_AND_CLIMB.MULTI_JUMP_MULTIPLIER;
          player.fallDistance = 0;
          player.velocityChanged = true;
          Mod.NETWORK.sendToAllAround(new MultiJumpEffectMessage(player), Util.getTargetPoint(player, 32));
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
