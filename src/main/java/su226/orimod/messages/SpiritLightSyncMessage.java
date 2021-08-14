package su226.orimod.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import su226.orimod.Mod;
import su226.orimod.capabilities.Capabilities;

public class SpiritLightSyncMessage implements IMessage {
  public static class Handler implements IMessageHandler<SpiritLightSyncMessage, IMessage> {
    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(SpiritLightSyncMessage message, MessageContext ctx) {
      Minecraft mc = Minecraft.getMinecraft();
      mc.addScheduledTask(() -> {
        mc.player.getCapability(Capabilities.SPIRIT_LIGHT, null).set(message.value);
      });
      return null;
    }
  }

  private int value;

  public SpiritLightSyncMessage() {}
  
  public SpiritLightSyncMessage(int value) {
    this.value = value;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void fromBytes(ByteBuf buf) {
    this.value = buf.readInt();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(this.value);
  }

  public static void register() {
    Mod.NETWORK.registerMessage(Handler.class, SpiritLightSyncMessage.class, Messages.nextId(), Side.CLIENT);
  }
}
