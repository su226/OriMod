package su226.orimod.packets;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import su226.orimod.others.Sounds;
import su226.orimod.others.Util;
import su226.orimod.particles.SpiritFlameParticle;

public class SpiritFlamePacket extends Packet {
  public static class Handler extends ClientHandler<SpiritFlamePacket> {
    @Override
    public void handle(MinecraftClient client, ClientPlayNetworkHandler handler, SpiritFlamePacket packet, PacketSender responseSender) {
      client.execute(() -> {
        Entity from = client.world.getEntityById(packet.from);
        if (packet.isVec) {
          client.particleManager.addParticle(new SpiritFlameParticle(client.world, from, packet.offset));
        } else {
          Entity to = client.world.getEntityById(packet.to);
          client.particleManager.addParticle(new SpiritFlameParticle(client.world, from, to));
          Util.playSound(to, Sounds.SPIRIT_FLAME_HIT);
        }
        Util.playSound(from, Sounds.SPIRIT_FLAME_THROW);
      });
    }
  }

  private int from;
  private boolean isVec;
  private int to;
  private Vec3d offset;

  public SpiritFlamePacket() {}
  
  public SpiritFlamePacket(Entity from, Entity to) {
    this.isVec = false;
    this.from = from.getEntityId();
    this.to = to.getEntityId();
  }
  
  public SpiritFlamePacket(Entity from, Vec3d offset) {
    this.isVec = true;
    this.from = from.getEntityId();
    this.offset = offset;
  }

  @Override
  public String getName() {
    return "spirit_flame";
  }

  @Override
  public void deserialize(PacketByteBuf buf) {
    this.from = buf.readInt();
    this.isVec = buf.readBoolean();
    if (this.isVec) {
      this.offset = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
    } else {
      this.to = buf.readInt();
    }
  }

  @Override
  public void serialize(PacketByteBuf buf) {
    buf.writeInt(this.from);
    buf.writeBoolean(this.isVec);
    if (this.isVec) {
      buf.writeDouble(this.offset.x);
      buf.writeDouble(this.offset.y);
      buf.writeDouble(this.offset.z);
    } else {
      buf.writeInt(this.to);
    }
  }
}
