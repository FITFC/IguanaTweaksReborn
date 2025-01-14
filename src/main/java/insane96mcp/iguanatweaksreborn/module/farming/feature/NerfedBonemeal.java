package insane96mcp.iguanatweaksreborn.module.farming.feature;

import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.setup.ITCommonConfig;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.config.Blacklist;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Collections;
import java.util.List;

@Label(name = "Nerfed Bonemeal", description = "Bonemeal is no longer so OP")
public class NerfedBonemeal extends Feature {

	private final ForgeConfigSpec.EnumValue<BonemealNerf> nerfedBonemealConfig;
	private final ForgeConfigSpec.DoubleValue bonemealFailChanceConfig;
	private final Blacklist.Config itemBlacklistConfig;
	private final Blacklist.Config blockBlacklistConfig;

	private static final List<String> blockBlacklistDefault = List.of("supplementaries:flax");

	public BonemealNerf nerfedBonemeal = BonemealNerf.NERFED;
	public double bonemealFailChance = 0d;
	public Blacklist itemBlacklist;
	public Blacklist blockBlacklist;

	public NerfedBonemeal(Module module) {
		super(ITCommonConfig.builder, module);
		ITCommonConfig.builder.comment(this.getDescription()).push(this.getName());
		nerfedBonemealConfig = ITCommonConfig.builder
				.comment("Makes more Bone Meal required for Crops. Valid Values are\nDISABLED: No Bone Meal changes\nSLIGHT: Makes Bone Meal grow 1-2 crop stages\nNERFED: Makes Bone Meal grow only 1 Stage")
				.defineEnum("Nerfed Bonemeal", this.nerfedBonemeal);
		bonemealFailChanceConfig = ITCommonConfig.builder
				.comment("Makes Bone Meal have a chance to fail to grow crops. 0 to disable, 1 to disable bonemeal.")
				.defineInRange("Bonemeal Fail Chance", bonemealFailChance, 0d, 1d);
		itemBlacklistConfig = new Blacklist.Config(ITCommonConfig.builder, "Item Blacklist",
				"Items or item tags that will ignore the feature. Can be used with any item that inherits the properties of vanilla bonemeal (and it's properly implemented).\n" +
						"Each entry has an item or tag. The format is modid:item_id or #modid:item_tag.")
				.setDefaultList(Collections.emptyList())
				.setIsDefaultWhitelist(false)
				.build();
		blockBlacklistConfig = new Blacklist.Config(ITCommonConfig.builder, "Block Blacklist",
				"Blocks or block tags that will not be affected by the bonemeal nerf.\n" +
						"Each entry has a block or a block tag. The format is modid:block_id or #modid:block_tag.")
				.setDefaultList(blockBlacklistDefault)
				.setIsDefaultWhitelist(false)
				.build();
		ITCommonConfig.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.nerfedBonemeal = this.nerfedBonemealConfig.get();
		this.bonemealFailChance = this.bonemealFailChanceConfig.get();
		this.itemBlacklist = this.itemBlacklistConfig.get();
		this.blockBlacklist = this.blockBlacklistConfig.get();
	}

	/**
	 * Handles part of crops require water too
	 */
	@SubscribeEvent
	public void nerfBonemeal(BonemealEvent event) {
		if (event.isCanceled() || event.getResult() == Event.Result.DENY)
			return;
		if (!this.isEnabled())
			return;
		if (event.getWorld().isClientSide)
			return;
		BonemealResult result = applyBonemeal(event.getWorld(), event.getStack(), event.getBlock(), event.getPos());
		if (result == BonemealResult.ALLOW)
			event.setResult(Event.Result.ALLOW);
		else if (result == BonemealResult.CANCEL)
			event.setCanceled(true);
	}

	public enum BonemealResult {
		NONE,
		CANCEL,
		ALLOW
	}

	public BonemealResult applyBonemeal(Level level, ItemStack stack, BlockState state, BlockPos pos) {
		if (this.itemBlacklist.isItemBlackOrNotWhiteListed(stack.getItem()) || this.blockBlacklist.isBlockBlackOrNotWhiteListed(state.getBlock()))
			return BonemealResult.NONE;

		//If farmland is dry and cropsRequireWater is enabled then cancel the event
		if (Modules.farming.cropsGrowth.requiresWetFarmland(level, pos) && !Modules.farming.cropsGrowth.hasWetFarmland(level, pos)) {
			return BonemealResult.CANCEL;
		}

		if (this.nerfedBonemeal.equals(BonemealNerf.DISABLED))
			return BonemealResult.NONE;
		if (state.getBlock() instanceof CropBlock cropBlock) {
			int age = state.getValue(cropBlock.getAgeProperty());
			int maxAge = Collections.max(cropBlock.getAgeProperty().getPossibleValues());
			if (age == maxAge) {
				return BonemealResult.NONE;
			}

			if (level.getRandom().nextDouble() < this.bonemealFailChance) {
				return BonemealResult.ALLOW;
			}
			else if (this.nerfedBonemeal.equals(BonemealNerf.SLIGHT)) {
				age += Mth.nextInt(level.getRandom(), 1, 2);
			}
			else if (this.nerfedBonemeal.equals(BonemealNerf.NERFED)) {
				age++;
			}
			age = Mth.clamp(age, 0, maxAge);
			state = state.setValue(cropBlock.getAgeProperty(), age);
		}
		else if (state.getBlock() instanceof StemBlock) {
			int age = state.getValue(StemBlock.AGE);
			int maxAge = Collections.max(StemBlock.AGE.getPossibleValues());
			if (age == maxAge) {
				return BonemealResult.NONE;
			}

			if (level.getRandom().nextDouble() < this.bonemealFailChance) {
				return BonemealResult.ALLOW;
			}
			else if (this.nerfedBonemeal.equals(BonemealNerf.SLIGHT)) {
				age += Mth.nextInt(level.getRandom(), 1, 2);
			}
			else if (this.nerfedBonemeal.equals(BonemealNerf.NERFED)) {
				age++;
			}
			age = Mth.clamp(age, 0, maxAge);
			state = state.setValue(StemBlock.AGE, age);
		}
		else
			return BonemealResult.NONE;
		level.setBlockAndUpdate(pos, state);
		return BonemealResult.ALLOW;
	}

	public enum BonemealNerf {
		DISABLED,
		SLIGHT,
		NERFED
	}
}
