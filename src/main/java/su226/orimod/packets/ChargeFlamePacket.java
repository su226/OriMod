package su226.orimod.packets;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import su226.orimod.others.Sounds;
import su226.orimod.others.Util;
import su226.orimod.particles.ChargeFlameParticle;

public class ChargeFlamePacket extends Packet {
  public static class Handler extends Packet.ClientHandler<ChargeFlamePacket> {
    @Override
    @Environment(EnvType.CLIENT)
    public void handle(MinecraftClient client, ClientPlayNetworkHandler handler, ChargeFlamePacket packet, PacketSender responseSender) {
      client.execute(() -> {
        Entity entity = client.world.getEntityById(packet.entity);
        Vec3d pos = entity.getClientCameraPosVec(client.getTickDelta());
        for (int i = 0; i < 100; i++) {
          Vec3d velocity = new Vec3d(1, 0, 0).rotateY(Util.randAngle(2f)).rotateX(Util.randAngle(0.5f));
          client.particleManager.addParticle(new ChargeFlameParticle(client.world, pos.add(velocity), velocity));
        }
        Util.playSound(entity, Sounds.CHARGE_FLAME_END);
      });
    }
  }
  private int entity;

  public ChargeFlamePacket() {}

  public ChargeFlamePacket(Entity entity) {
    this.entity = entity.getEntityId();
  }

  @Override
  public String getName() {
    return "charge_flame";
  }

  @Override
  public void serialize(PacketByteBuf buf) {
    buf.writeInt(this.entity);
  }

  @Override
  public void deserialize(PacketByteBuf buf) {
    this.entity = buf.readInt();
  }
}
