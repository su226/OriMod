package su226.orimod.packets;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import su226.orimod.others.Sounds;
import su226.orimod.others.Util;

public class MultiJumpEffectPacket extends Packet {
  public static class Handler extends Packet.ClientHandler<MultiJumpEffectPacket> {
    private static final int PARTICLE_COUNT = 10;

    @Override
    public void handle(MinecraftClient client, ClientPlayNetworkHandler handler, MultiJumpEffectPacket packet, PacketSender responseSender) {
      client.execute(() -> {
        Entity ent = client.world.getEntityById(packet.entity);
        Util.playSound(ent, Sounds.MULTI_JUMP);
        Vec3d delta = new Vec3d(0, -1, 0);
        Vec3d move = new Vec3d(1, 0, 0);
        for (int i = 0; i < PARTICLE_COUNT; i++) {
          Vec3d velocity = delta.add(move.rotateY(Util.randAngle(2)).multiply(Math.random())).normalize().multiply(Math.random());
          client.world.addParticle(ParticleTypes.CLOUD, ent.getX(), ent.getY(), ent.getZ(), velocity.x, velocity.y, velocity.z);
        }
      });
    }
  }

  private int entity;

  public MultiJumpEffectPacket() {}
  
  public MultiJumpEffectPacket(Entity entity) {
    this.entity = entity.getEntityId();
  }

  @Override
  public String getName() {
    return "multi_jump_effect";
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
