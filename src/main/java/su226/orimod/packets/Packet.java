package su226.orimod.packets;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import su226.orimod.others.Util;

public abstract class Packet {
  public abstract static class ClientHandler<P extends Packet> {
    public abstract void handle(MinecraftClient client, ClientPlayNetworkHandler handler, P packet, PacketSender responseSender);
  }
  public abstract static class ServerHandler<P extends Packet> {
    public abstract void handle(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, P packet, PacketSender responseSender);
  }
  public abstract String getName();
  public abstract void serialize(PacketByteBuf buf);
  public abstract void deserialize(PacketByteBuf buf);

  public void sendToServer() {
    PacketByteBuf buf = PacketByteBufs.create();
    this.serialize(buf);
    ClientPlayNetworking.send(Util.getIdentifier(this.getName()), buf);
  }
  public void sendTo(ServerPlayerEntity player) {
    PacketByteBuf buf = PacketByteBufs.create();
    this.serialize(buf);
    ServerPlayNetworking.send(player, Util.getIdentifier(this.getName()), buf);
  }
  public void sendToAround(Entity ent, double range) {
    PacketByteBuf buf = PacketByteBufs.create();
    this.serialize(buf);
    Vec3d pos = ent.getPos();
    for (ServerPlayerEntity other : ent.world.getEntitiesByClass(ServerPlayerEntity.class, new Box(pos.x - range, pos.y - range, pos.z - range, pos.x + range, pos.y + range, pos.z + range), null)) {
      ServerPlayNetworking.send(other, Util.getIdentifier(this.getName()), buf);
    }
  }
}
