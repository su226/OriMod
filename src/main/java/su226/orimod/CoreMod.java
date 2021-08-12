package su226.orimod;

import java.util.Map;

import org.spongepowered.asm.launch.MixinBootstrap;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;

@MCVersion("1.12.2")
public class CoreMod implements IFMLLoadingPlugin {
  public CoreMod() {
    MixinBootstrap.init();
  }

	@Override
	public String[] getASMTransformerClass() {
		return new String[0];
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {}
}
