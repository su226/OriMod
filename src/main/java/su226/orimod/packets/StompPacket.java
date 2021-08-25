package su226.orimod.packets;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import su226.orimod.components.Components;
import su226.orimod.components.ICooldown;
import su226.orimod.others.Sounds;

public class StompPacket extends Packet {
  public static class Handler extends ServerHandler<StompPacket> {
    @Override
    public void handle(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, StompPacket packet, PacketSender responseSender) {
      server.execute(() -> {
        ICooldown cap = Components.COOLDOWN.get(player);
        cap.setCooldown("stomp", 1, -1);
        cap.doAction("stomp");
        player.setNoGravity(true);
        player.world.playSound(null, player.getX(), player.getY(), player.getZ(), Sounds.STOMP_START, SoundCategory.PLAYERS, 1, 1);
      });
    }
  }

  public StompPacket() {}

  @Override
  public String getName() {
    return "stomp";
  }

  @Override
  public void deserialize(PacketByteBuf buf) {}

  @Override
  public void serialize(PacketByteBuf buf) {}
}
