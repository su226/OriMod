package su226.orimod.packets;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import su226.orimod.components.Components;

public class SpiritLightSyncPacket extends Packet {
  public static class Handler extends Packet.ClientHandler<SpiritLightSyncPacket> {
    @Override
    public void handle(MinecraftClient client, ClientPlayNetworkHandler handler, SpiritLightSyncPacket packet, PacketSender responseSender) {
      client.execute(() -> Components.SPIRIT_LIGHT.get(client.player).set(packet.value));
    }
  }

  private int value;

  public SpiritLightSyncPacket() {}
  
  public SpiritLightSyncPacket(int value) {
    this.value = value;
  }

  @Override
  public String getName() {
    return "spirit_light";
  }

  @Override
  public void deserialize(PacketByteBuf buf) {
    this.value = buf.readInt();
  }

  @Override
  public void serialize(PacketByteBuf buf) {
    buf.writeInt(this.value);
  }
}
