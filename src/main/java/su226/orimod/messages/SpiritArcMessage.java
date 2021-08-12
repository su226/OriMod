package su226.orimod.messages;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import su226.orimod.Mod;
import su226.orimod.others.Sounds;
import su226.orimod.particles.SpiritArcParticle;

public class SpiritArcMessage extends ProjectileHitMessage {
  public static class Handler extends ProjectileHitMessage.Handler<SpiritArcMessage> {}
  
  public SpiritArcMessage() {}

  public SpiritArcMessage(Entity owner, Entity hit, Vec3d start, Vec3d end) {
    super(owner, hit, start, end);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void spawnParticle(Vec3d velocity) {
    Minecraft mc = Minecraft.getMinecraft();
    mc.effectRenderer.addEffect(new SpiritArcParticle(mc.world, this.start, velocity));
  }

  @Override
  public SoundEvent getHitEntitySound() {
    return Sounds.ARROW_HIT_ENTITY;
  }

  @Override
  public SoundEvent getHitGroundSound() {
    return Sounds.ARROW_HIT_GROUND;
  }

  public static void register() {
    Mod.NETWORK.registerMessage(Handler.class, SpiritArcMessage.class, Messages.nextId(), Side.CLIENT);
  }
}
