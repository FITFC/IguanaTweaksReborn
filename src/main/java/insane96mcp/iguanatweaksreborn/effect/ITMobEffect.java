package insane96mcp.iguanatweaksreborn.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ITMobEffect extends MobEffect {
	boolean canBeCured;

	public ITMobEffect(MobEffectCategory typeIn, int liquidColorIn) {
		this(typeIn, liquidColorIn, true);
	}

	public ITMobEffect(MobEffectCategory typeIn, int liquidColorIn, boolean canBeCured) {
		super(typeIn, liquidColorIn);
		this.canBeCured = canBeCured;
	}

	@Override
	public List<ItemStack> getCurativeItems() {
		return !canBeCured ? new ArrayList<>() : super.getCurativeItems();
	}
}
