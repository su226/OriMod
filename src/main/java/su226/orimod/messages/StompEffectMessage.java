package su226.orimod.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import su226.orimod.Mod;
import su226.orimod.others.Sounds;
import su226.orimod.others.Util;

public class StompEffectMessage implements IMessage {
  public static class Handler implements IMessageHandler<StompEffectMessage, IMessage> {

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(StompEffectMessage message, MessageContext ctx) {
      Minecraft mc = Minecraft.getMinecraft();
      mc.addScheduledTask(() -> {
        Entity ent = mc.world.getEntityByID(message.entity);
        Vec3d origin = ent.getPositionVector();
        BlockPos pos = ent.getPosition().add(0, -1, 0);
        for (int x = -3; x < 3; x++) {
          for (int z = -3; z < 3; z++) {
            for (int y = 1; y > -1; y--) {
              BlockPos pos1 = pos.add(x, y, z);
              IBlockState state = mc.world.getBlockState(pos1);
              if (state.getBlock() != Blocks.AIR) {
                for (double ox = 0.125; ox <= 0.875; ox += 0.5) {
                  for (double oz = 0.125; oz <= 0.875; oz += 0.5) {
                    Vec3d vec = new Vec3d(pos1.getX() + ox, pos1.getY() + 1, pos1.getZ() + oz);
                    Vec3d delta = vec.subtract(origin).scale(0.05);
                    mc.world.spawnParticle(EnumParticleTypes.BLOCK_DUST, vec.x, vec.y, vec.z, delta.x, 0.1, delta.z, Block.getStateId(state));
                  }
                }
                break;
              }
            }
          }
        }
        Util.playSound(ent, Sounds.STOMP_HIT);
      });
      return null;
    }
  }

  private int entity;

  public StompEffectMessage() {}
  
  public StompEffectMessage(Entity entity) {
    this.entity = entity.getEntityId();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void fromBytes(ByteBuf buf) {
    this.entity = buf.readInt();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(this.entity);
  }

  public static void register() {
    Mod.NETWORK.registerMessage(Handler.class, StompEffectMessage.class, Messages.nextId(), Side.CLIENT);
  }
}
