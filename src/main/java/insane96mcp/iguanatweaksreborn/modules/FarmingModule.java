package insane96mcp.iguanatweaksreborn.modules;

import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.iguanatweaksreborn.setup.ModConfig;
import insane96mcp.iguanatweaksreborn.utils.RandomHelper;
import net.minecraft.block.*;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;

import java.util.Collections;

public class FarmingModule {

	public static void nerfBonemeal(BonemealEvent event) {
		if (event.getWorld().isRemote)
			return;

		if (!ModConfig.Modules.farming)
			return;

		if (ModConfig.Farming.nerfedBonemeal.equals(Config.CommonConfig.Farming.NerfedBonemeal.DISABLED))
			return;

		BlockState state = event.getWorld().getBlockState(event.getPos());
		if (!(state.getBlock() instanceof CropsBlock))
			return;

		boolean isBeetroot = state.getBlock() instanceof BeetrootBlock;

		int age = 0;
		int maxAge = Collections.max(CropsBlock.AGE.getAllowedValues());

		if (isBeetroot) {
			age = state.get(BeetrootBlock.BEETROOT_AGE);
			maxAge = Collections.max(BeetrootBlock.BEETROOT_AGE.getAllowedValues());
		}
		else
			age = state.get(CropsBlock.AGE);

		if (age == maxAge)
			return;

		if (ModConfig.Farming.nerfedBonemeal.equals(Config.CommonConfig.Farming.NerfedBonemeal.SLIGHT))
			age += RandomHelper.getInt(event.getWorld().getRandom(), 1, 2);
		else if (ModConfig.Farming.nerfedBonemeal.equals(Config.CommonConfig.Farming.NerfedBonemeal.NERFED))
			age++;

		if (age > maxAge)
			age = maxAge;

		if (isBeetroot) {
			state = state.with(BeetrootBlock.BEETROOT_AGE, age);
		}
		else
			state = state.with(CropsBlock.AGE, age);

		event.getWorld().setBlockState(event.getPos(), state, 3);
		event.setResult(Event.Result.ALLOW);
	}

	public static void cropsRequireWater(BlockEvent.CropGrowEvent.Pre event) {
		if (!ModConfig.Modules.farming)
			return;

		if (!ModConfig.Farming.cropsRequireWater)
			return;

		IWorld world = event.getWorld();
		BlockState sustainState = world.getBlockState(event.getPos().down());
		if (!(sustainState.getBlock() instanceof FarmlandBlock))
			return;

		int moisture = sustainState.get(FarmlandBlock.MOISTURE);

		if (moisture < 7)
			event.setResult(Event.Result.DENY);

	}

	/**
	 * Handles Crop Growth Speed Multiplier and NoSunlight Growth multiplier
	 */
	public static void cropsGrowthSpeedMultiplier(BlockEvent.CropGrowEvent.Post event) {
		if (!ModConfig.Modules.farming)
			return;

		if (ModConfig.Farming.cropsGrowthMultiplier == 1.0d && ModConfig.Farming.noSunlightGrowthMultiplier == 1.0d)
			return;

		IWorld world = event.getWorld();
		BlockState state = event.getOriginalState();

		if (!(state.getBlock() instanceof CropsBlock))
			return;

		double chance = 1d / ModConfig.Farming.cropsGrowthMultiplier;

		int skyLight = world.getLightFor(LightType.SKY, event.getPos());
		if (skyLight < ModConfig.Farming.minSunlight)
			chance *= 1d / ModConfig.Farming.noSunlightGrowthMultiplier;

		if (ModConfig.Farming.cropsGrowthMultiplier == 0.0d)
			chance = -1d;

		if (skyLight < ModConfig.Farming.minSunlight
				&& ModConfig.Farming.noSunlightGrowthMultiplier == 0.0d)
			chance = -1d;

		if (event.getWorld().getRandom().nextDouble() > chance)
			world.setBlockState(event.getPos(), state, 2);
	}

	public static void sugarCaneGrowthSpeedMultiplier(BlockEvent.CropGrowEvent.Post event) {
		if (!ModConfig.Modules.farming)
			return;

		if (ModConfig.Farming.sugarCanesGrowthMultiplier == 1.0d)
			return;

		IWorld world = event.getWorld();
		BlockState state = event.getOriginalState();

		if (!(state.getBlock() instanceof SugarCaneBlock))
			return;

		double chance = 1d / ModConfig.Farming.sugarCanesGrowthMultiplier;

		if (ModConfig.Farming.sugarCanesGrowthMultiplier == 0.0d)
			chance = -1d;

		if (event.getWorld().getRandom().nextDouble() > chance) {
			world.setBlockState(event.getPos(), state, 2);
		}
	}

	public static void cactusGrowthSpeedMultiplier(BlockEvent.CropGrowEvent.Post event) {
		if (!ModConfig.Modules.farming)
			return;

		if (ModConfig.Farming.cactusGrowthMultiplier == 1.0d)
			return;

		IWorld world = event.getWorld();
		BlockState state = event.getOriginalState();

		if (!(state.getBlock() instanceof CactusBlock))
			return;

		double chance = 1d / ModConfig.Farming.cactusGrowthMultiplier;

		if (ModConfig.Farming.cactusGrowthMultiplier == 0.0d)
			chance = -1d;

		if (event.getWorld().getRandom().nextDouble() > chance) {
			world.setBlockState(event.getPos(), state, 2);
		}
	}
}
