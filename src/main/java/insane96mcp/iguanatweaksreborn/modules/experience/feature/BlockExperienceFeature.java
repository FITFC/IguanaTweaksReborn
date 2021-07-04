package insane96mcp.iguanatweaksreborn.modules.experience.feature;

import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Experience From Blocks", description = "Decrease (or Increase) experience dropped by blocks broken")
public class BlockExperienceFeature extends Feature {

	private final ForgeConfigSpec.ConfigValue<Double> blockMultiplierConfig;

	public double blockMultiplier = 3.0d;

	public BlockExperienceFeature(Module module) {
		super(Config.builder, module, true);
		Config.builder.comment(this.getDescription()).push(this.getName());
		blockMultiplierConfig = Config.builder
				.comment("Experience dropped by blocks (Ores and Spawners) will be multiplied by this multiplier. Experience dropped by blocks are still affected by 'Global Experience Multiplier'\nCan be set to 0 to make blocks drop no experience")
				.defineInRange("Experience from Blocks Multiplier", this.blockMultiplier, 0.0d, 1000d);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.blockMultiplier = this.blockMultiplierConfig.get();
    }

    @SubscribeEvent
    public void onBlockXPDrop(BlockEvent.BreakEvent event) {
		if (!this.isEnabled())
			return;
		if (this.blockMultiplier == 1.0d)
			return;

        int xpToDrop = event.getExpToDrop();
		xpToDrop *= this.blockMultiplier;
		event.setExpToDrop(xpToDrop);
    }
}
