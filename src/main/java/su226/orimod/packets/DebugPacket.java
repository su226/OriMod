package su226.orimod.packets;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import su226.orimod.particles.DebugParticle;

public class DebugPacket extends Packet {
  public static class Handler extends Packet.ClientHandler<DebugPacket> {
    @Override
    public void handle(MinecraftClient client, ClientPlayNetworkHandler handler, DebugPacket packet, PacketSender responseSender) {
      client.execute(() -> client.particleManager.addParticle(new DebugParticle(client.world, packet.from, packet.to, packet.color)));
    }
  }

  private Vec3d from;
  private Vec3d to;
  private int color;

  public DebugPacket() {}
  
  public DebugPacket(Vec3d from, Vec3d to, int color) {
    this.from = from;
    this.to = to;
    this.color = color;
  }

  @Override
  public String getName() {
    return "debug";
  }

  @Override
  public void deserialize(PacketByteBuf buf) {
    this.from = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
    this.to = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
    this.color = buf.readInt();
  }

  @Override
  public void serialize(PacketByteBuf buf) {
    buf.writeDouble(this.from.x);
    buf.writeDouble(this.from.y);
    buf.writeDouble(this.from.z);
    buf.writeDouble(this.to.x);
    buf.writeDouble(this.to.y);
    buf.writeDouble(this.to.z);
    buf.writeInt(this.color);
  }
}
