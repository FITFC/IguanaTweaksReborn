package insane96mcp.iguanatweaksreborn.module.mining.utils;

import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.math.NumberUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * In this case the {@link DepthHardnessDimension#multiplier} field is used per block below the {@link DepthHardnessDimension#applyBelowY} level
 */
public class DepthHardnessDimension extends DimensionHardnessMultiplier {

	public int applyBelowY;
	public int capY;

	public DepthHardnessDimension(ResourceLocation dimension, double multiplier, int applyBelowY, int capY) {
		super(dimension, multiplier);
		this.applyBelowY = applyBelowY;
		this.capY = capY;
	}

	@Nullable
	public static DepthHardnessDimension parseLine(String line) {
		String[] split = line.split(",");
		if (split.length != 4) {
			LogHelper.warn("Invalid line \"%s\" for Depth Hardness Dimension. Format must be modid:dimensionId,hardness,applyBelowY,capY", line);
			return null;
		}
		ResourceLocation dimension = ResourceLocation.tryParse(split[0]);
		if (dimension == null) {
			LogHelper.warn(String.format("Invalid dimension \"%s\" for Depth Hardness Dimension", split[0]));
			return null;
		}

		if (!NumberUtils.isParsable(split[1])) {
			LogHelper.warn(String.format("Invalid hardness \"%s\" for Depth Hardness Dimension", split[1]));
			return null;
		}
		double hardness = Double.parseDouble(split[1]);

		if (!NumberUtils.isParsable(split[2])) {
			LogHelper.warn(String.format("Invalid Y Level \"%s\" for Depth Hardness Dimension", split[2]));
			return null;
		}
		int applyBelowY = Integer.parseInt(split[2]);

		if (!NumberUtils.isParsable(split[3])) {
			LogHelper.warn(String.format("Invalid Y cap \"%s\" for Depth Hardness Dimension", split[3]));
			return null;
		}
		int capY = Integer.parseInt(split[3]);

		return new DepthHardnessDimension(dimension, hardness, applyBelowY, capY);
	}

	public static ArrayList<DepthHardnessDimension> parseStringList(List<? extends String> list) {
		ArrayList<DepthHardnessDimension> depthHardnessDimensions = new ArrayList<>();
		for (String line : list) {
			DepthHardnessDimension depthHardnessDimension = DepthHardnessDimension.parseLine(line);
			if (depthHardnessDimension != null)
				depthHardnessDimensions.add(depthHardnessDimension);
		}
		return depthHardnessDimensions;
	}

}