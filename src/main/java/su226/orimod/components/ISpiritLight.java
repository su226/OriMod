package su226.orimod.components;

import com.mojang.blaze3d.platform.GlStateManager;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.tick.ClientTickingComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import dev.onyxstudios.cca.api.v3.entity.PlayerComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import su226.orimod.Mod;
import su226.orimod.ModClient;
import su226.orimod.entities.SpiritLightOrb;
import su226.orimod.others.Render;
import su226.orimod.others.Stats;
import su226.orimod.packets.SpiritLightSyncPacket;

public interface ISpiritLight extends ComponentV3, ClientTickingComponent, ServerTickingComponent {
  class Player implements PlayerComponent<ISpiritLight>, ISpiritLight {
    private int amount;
    private boolean dirty = true;
    private final PlayerEntity player;
    public static int hudTick;

    public Player(PlayerEntity entity) {
      this.player = entity;
    }

    @Override
    public int get() {
      return amount;
    }

    @Override
    public void set(int value) {
      if (value != this.amount) {
        this.amount = value;
        this.dirty = true;
      }
    }

    @Override
    public void collect(int value) {
      if (value != 0) {
        this.amount += value;
        this.dirty = true;
        this.player.increaseStat(Stats.LIGHT_COLLECT, value);
      }
    }

    @Override
    public void cost(int value) {
      if (value != 0) {
        this.amount -= value;
        this.dirty = true;
        this.player.increaseStat(Stats.LIGHT_COST, value);
      }
    }

    @Override
    public void clientTick() {
      if (this.dirty) {
        hudTick = ModClient.tick;
        this.dirty = false;
      }
    }

    @Override
    public void serverTick() {
      if (this.dirty) {
        new SpiritLightSyncPacket(this.amount).sendTo((ServerPlayerEntity)this.player);
        this.dirty = false;
      }
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
      this.amount = tag.getInt("amount");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
      tag.putInt("amount", this.amount);
    }
  }
  int get();
  void set(int value);
  void collect(int value);
  void cost(int value);

  static void livingDeath(LivingEntity entity) {
    if (!entity.world.isClient) {
      if (entity instanceof PlayerEntity) {
        ISpiritLight cap = Components.SPIRIT_LIGHT.get(entity);
        int keep = (int)(cap.get() * Mod.CONFIG.spirit_light.death_penalty);
        int drop = (int)((cap.get() - keep) * Mod.CONFIG.spirit_light.death_drop);
        cap.set(keep);
        if (drop != 0) {
          entity.world.spawnEntity(new SpiritLightOrb(entity, drop));
        }
      } else if (entity.getAttacker() instanceof PlayerEntity) {
        entity.world.spawnEntity(new SpiritLightOrb(entity, (int)(entity.getMaxHealth() * Mod.CONFIG.spirit_light.drop_multiplier)));
      }
    }
  }

  static void renderHud(MatrixStack mat, float tickDelta) {
    float delta = ModClient.tick - Player.hudTick + tickDelta;
    if (delta > 40) {
      return;
    }
    float alpha = 1;
    if (delta < 10) {
      alpha = delta / 10;
    } else if (delta >= 30) {
      alpha = (40 - delta) / 10;
    }
    MinecraftClient mc = MinecraftClient.getInstance();
    int x = mc.getWindow().getScaledWidth() - 40;
    int y = 8;
    GL11.glColor4f(1, 1, 1, alpha);
    mat.push();
    mat.translate(x + 16, y + 16, 0);
    mat.multiply(new Quaternion(0, 0, delta * 3, true));
    GlStateManager.enableBlend();
    Render.Legacy.square(mat, new Vec3d(-16, -16, 0), new Vec3d(-16, 16, 0), new Vec3d(16, 16, 0), SpiritLightOrb.Render.TEXTURE);
    mat.pop();
    String str = Integer.toString(Components.SPIRIT_LIGHT.get(mc.player).get());
    mat.push();
    mat.translate(x + 16 - mc.textRenderer.getWidth(str), y + 32, 0);
    mat.scale(2, 2, 1);
    int textAlpha = (int)(alpha * 0xfe + 1) << 24;
    mc.textRenderer.drawWithShadow(mat, str, 0, 0, 0xffff55 | textAlpha);
    mat.pop();
  }
}
