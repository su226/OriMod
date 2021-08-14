package su226.orimod.messages;

public class Messages {
  private static int id = 0;

  public static int nextId() {
    return id++;
  }

  public static void register() {
    ChargeFlameMessage.register();
    DebugMessage.register();
    FlapMessage.register();
    LightBurstMessage.register();
    MultiJumpEffectMessage.register();
    MultiJumpMessage.register();
    SoundMessage.register();
    SpiritArcMessage.register();
    SpiritFlameMessage.register();
    SpiritLightSyncMessage.register();
    WallJumpMessage.register();
  }
}
