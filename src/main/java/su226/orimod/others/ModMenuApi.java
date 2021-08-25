package su226.orimod.others;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import me.shedaniel.autoconfig.AutoConfig;
import su226.orimod.Config;

public class ModMenuApi implements com.terraformersmc.modmenu.api.ModMenuApi {
  @Override
  public ConfigScreenFactory<?> getModConfigScreenFactory() {
    return parent -> AutoConfig.getConfigScreen(Config.class, parent).get();
  }
}
