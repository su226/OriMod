package su226.orimod.packets;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;
import su226.orimod.others.Util;

public abstract class ProjectileHitPacket extends Packet {
  public static class Handler extends Packet.ClientHandler<ProjectileHitPacket> {
    private static final int PARTICLE_COUNT = 10;

    @Override
    public void handle(MinecraftClient client, ClientPlayNetworkHandler handler, ProjectileHitPacket packet, PacketSender responseSender) {
      client.execute(() -> {
        Entity hit = packet.hit == -1 ? null : client.world.getEntityById(packet.hit);
        Entity owner = client.world.getEntityById(packet.owner);
        if (hit != null) {
          Util.playSound(hit, packet.start, packet.getHitEntitySound());
        } else {
          Util.playSound(owner, packet.start, packet.getHitGroundSound());
        }
        Vec3d delta = packet.end.subtract(packet.start).normalize();
        Vec3d move = Util.perpendicular(delta).normalize();
        for (int i = 0; i < PARTICLE_COUNT; i++) {
          Vec3d velocity = delta.add(Util.rotate(delta, move, Util.randAngle(2)).multiply(Math.random())).normalize().multiply(Math.random());
          packet.spawnParticle(velocity);
        }
        packet.callback();
      });
    }
  }

  protected int owner;
  protected int hit;
  protected Vec3d start;
  protected Vec3d end;

  public ProjectileHitPacket() {}
  
  public ProjectileHitPacket(Entity owner, Entity hit, Vec3d start, Vec3d end) {
    this.owner = owner.getEntityId();
    this.hit = hit == null ? -1 : hit.getEntityId();
    this.start = start;
    this.end = end;
  }

  @Override
  public void deserialize(PacketByteBuf buf) {
    this.owner = buf.readInt();
    this.hit = buf.readInt();
    this.start = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
    this.end = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
  }

  @Override
  public void serialize(PacketByteBuf buf) {
    buf.writeInt(this.owner);
    buf.writeInt(this.hit);
    buf.writeDouble(this.start.x);
    buf.writeDouble(this.start.y);
    buf.writeDouble(this.start.z);
    buf.writeDouble(this.end.x);
    buf.writeDouble(this.end.y);
    buf.writeDouble(this.end.z);
  }

  public abstract void spawnParticle(Vec3d velocity);
  public abstract SoundEvent getHitEntitySound();
  public abstract SoundEvent getHitGroundSound();
  public void callback() {}
}