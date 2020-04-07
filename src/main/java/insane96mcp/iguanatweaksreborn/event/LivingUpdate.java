package insane96mcp.iguanatweaksreborn.event;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.modules.FarmingModule;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IguanaTweaksReborn.MOD_ID)
public class LivingUpdate {

	@SubscribeEvent
	public static void livingUpdateEvent(LivingEvent.LivingUpdateEvent event) {
		FarmingModule.Livestock.slowdownAnimalGrowth(event);
		FarmingModule.Livestock.slowdownBreeding(event);
		FarmingModule.Livestock.slowdownEggLay(event);
		FarmingModule.Livestock.cowMilkTick(event);
	}

	@SubscribeEvent
	public static void LivingUpdateEvent(TickEvent.PlayerTickEvent event) {
		/*if (event.player.world.isRemote())
			return;

		ServerPlayerEntity player = (ServerPlayerEntity) event.player;

		if (player.ticksExisted % 20 != 0)
			return;

		int savedHunger = player.getPersistentData().getInt(IguanaTweaksReborn.RESOURCE_PREFIX + "hunger");
		if (savedHunger != 0 && player.getFoodStats().getFoodLevel() > savedHunger){
			player.getFoodStats().addExhaustion(100);

		}
		player.getPersistentData().putInt(IguanaTweaksReborn.RESOURCE_PREFIX + "hunger", player.getFoodStats().getFoodLevel());*/

	}
}