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
    MultiJumpMessage.register();
    MultiJumpEffectMessage.register();
    SpiritArcMessage.register();
    SpiritFlameMessage.register();
    SoundMessage.register();
  }
}
