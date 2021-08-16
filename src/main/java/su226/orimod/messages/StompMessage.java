package su226.orimod.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import su226.orimod.Mod;
import su226.orimod.capabilities.Capabilities;
import su226.orimod.capabilities.ICooldown;
import su226.orimod.others.Sounds;

public class StompMessage implements IMessage {
  public static class Handler implements IMessageHandler<StompMessage, IMessage> {
    @Override
    public IMessage onMessage(StompMessage message, MessageContext ctx) {
      EntityPlayerMP player = ctx.getServerHandler().player;
      player.getServerWorld().addScheduledTask(() -> {
        ICooldown cap = player.getCapability(Capabilities.COOLDOWN, null);
        cap.setCooldown("stomp", 1, -1);
        cap.doAction("stomp");
        player.setNoGravity(true);
        SoundMessage.play(player, Sounds.STOMP_START);
      });
      return null;
    }
  }

  public StompMessage() {}

  @Override
  public void fromBytes(ByteBuf buf) {}

  @Override
  public void toBytes(ByteBuf buf) {}

  public static void register() {
    Mod.NETWORK.registerMessage(Handler.class, StompMessage.class, Messages.nextId(), Side.SERVER);
  }
}
