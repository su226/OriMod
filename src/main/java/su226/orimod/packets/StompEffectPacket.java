package su226.orimod.packets;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import su226.orimod.others.Sounds;
import su226.orimod.others.Util;

public class StompEffectPacket extends Packet {
  public static class Handler extends Packet.ClientHandler<StompEffectPacket> {
    @Override
    public void handle(MinecraftClient client, ClientPlayNetworkHandler handler, StompEffectPacket packet, PacketSender responseSender) {
      client.execute(() -> {
        Entity ent = client.world.getEntityById(packet.entity);
        Vec3d origin = ent.getPos();
        BlockPos pos = ent.getBlockPos().add(0, -1, 0);
        for (int x = -3; x < 3; x++) {
          for (int z = -3; z < 3; z++) {
            for (int y = 1; y > -1; y--) {
              BlockPos pos1 = pos.add(x, y, z);
              BlockState state = client.world.getBlockState(pos1);
              if (state.getBlock() != Blocks.AIR) {
                for (double ox = 0.125; ox <= 0.875; ox += 0.5) {
                  for (double oz = 0.125; oz <= 0.875; oz += 0.5) {
                    Vec3d vec = new Vec3d(pos1.getX() + ox, pos1.getY() + 1, pos1.getZ() + oz);
                    Vec3d delta = vec.subtract(origin).multiply(0.05);
                    client.world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, state), vec.x, vec.y, vec.z, delta.x, 0.1, delta.z);
                  }
                }
                break;
              }
            }
          }
        }
        Util.playSound(ent, Sounds.STOMP_HIT);
      });
    }
  }

  private int entity;

  public StompEffectPacket() {}
  
  public StompEffectPacket(Entity entity) {
    this.entity = entity.getEntityId();
  }

  @Override
  public String getName() {
    return "stomp_effect";
  }

  @Override
  public void deserialize(PacketByteBuf buf) {
    this.entity = buf.readInt();
  }

  @Override
  public void serialize(PacketByteBuf buf) {
    buf.writeInt(this.entity);
  }
}
