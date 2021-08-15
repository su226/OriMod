package su226.orimod.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import su226.orimod.Config;
import su226.orimod.Mod;

public class WallJumpMessage implements IMessage {
  public static class Handler implements IMessageHandler<WallJumpMessage, IMessage> {
    @Override
    public IMessage onMessage(WallJumpMessage message, MessageContext ctx) {
      EntityPlayerMP player = ctx.getServerHandler().player;
      WorldServer world = player.getServerWorld();
      world.addScheduledTask(() -> {
        AxisAlignedBB cling = player.getEntityBoundingBox().grow(0.01, 0, 0.01);
        if (world.collidesWithAnyBlock(cling)) {
          player.jump();
          player.motionY *= Config.JUMP_AND_CLIMB.WALL_JUMP_MULTIPLIER;
          player.fallDistance = 0;
          player.velocityChanged = true;
        }
      });
      return null;
    }
  }

  @Override
  public void fromBytes(ByteBuf buf) {}

  @Override
  public void toBytes(ByteBuf buf) {}

  public static void register() {
    Mod.NETWORK.registerMessage(Handler.class, WallJumpMessage.class, Messages.nextId(), Side.SERVER);
  }
}
