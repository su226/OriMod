package su226.orimod.packets;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import su226.orimod.Mod;
import su226.orimod.others.Sounds;
import su226.orimod.others.Util;

public class FlapPacket extends Packet {
  public static class Handler extends Packet.ClientHandler<FlapPacket> {
    private static final int EXTEND_FINENESS = 10;
    private static final int ROTATE_FINENESS = 10;
    private static final int RADIAL_FINENESS = 2;

    @Override
    public void handle(MinecraftClient client, ClientPlayNetworkHandler handler, FlapPacket packet, PacketSender responseSender) {
      client.execute(() -> {
        Entity owner = client.world.getEntityById(packet.owner);
        Vec3d start = owner.getCameraPosVec(1);
        Vec3d delta = packet.end.subtract(start).normalize();
        Vec3d base = Util.perpendicular(delta);
        for (int i = 0; i < EXTEND_FINENESS; i++) {
          Vec3d velocity = delta.multiply(Mod.CONFIG.kuros_feather.force * i / EXTEND_FINENESS);
          for (int j = 0; j < ROTATE_FINENESS; j++) {
            Vec3d rotate = Util.rotate(delta, base, Math.PI * 2 * j / ROTATE_FINENESS).normalize();
            for (int k = 1; k <= RADIAL_FINENESS; k++) {
              Vec3d point = rotate.multiply(Mod.CONFIG.kuros_feather.range * k / RADIAL_FINENESS).add(start);
              client.world.addParticle(ParticleTypes.CLOUD, point.x, point.y, point.z, velocity.x, velocity.y, velocity.z);
            }
          }
        }
        Util.playSound(owner, Sounds.FLAP_START);
      });
    }
  }

  private int owner;
  private Vec3d end;

  public FlapPacket() {}
  
  public FlapPacket(Entity owner, Vec3d end) {
    this.owner = owner.getEntityId();
    this.end = end;
  }

  @Override
  public String getName() {
    return "flap";
  }

  @Override
  public void deserialize(PacketByteBuf buf) {
    this.owner = buf.readInt();
    this.end = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
  }

  @Override
  public void serialize(PacketByteBuf buf) {
    buf.writeInt(this.owner);
    buf.writeDouble(this.end.x);
    buf.writeDouble(this.end.y);
    buf.writeDouble(this.end.z);
  }
}
