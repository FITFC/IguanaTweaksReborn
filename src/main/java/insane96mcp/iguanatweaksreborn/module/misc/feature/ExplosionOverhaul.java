package insane96mcp.iguanatweaksreborn.module.misc.feature;

import insane96mcp.iguanatweaksreborn.module.misc.level.ITExplosion;
import insane96mcp.iguanatweaksreborn.setup.ITCommonConfig;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.config.Blacklist;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Collections;
import java.util.List;

@Label(name = "Explosion Overhaul", description = "Various changes to explosions from knockback to shielding.")
public class ExplosionOverhaul extends Feature {

	private final ForgeConfigSpec.BooleanValue disableExplosionRandomnessConfig;
	private final ForgeConfigSpec.BooleanValue enablePoofParticlesConfig;
	private final ForgeConfigSpec.DoubleValue blockingDamageScalingConfig;
	private final ForgeConfigSpec.BooleanValue knockbackScalesWithSizeConfig;
	private final ForgeConfigSpec.BooleanValue explosionAtHalfEntityConfig;
	private final ForgeConfigSpec.BooleanValue affectJustSpawnedEntitiesConfig;
	private final ForgeConfigSpec.BooleanValue enableFlyingBlocksConfig;
	private final ForgeConfigSpec.BooleanValue creeperCollateralConfig;
	private final Blacklist.Config knockbackBlacklistConfig;
	private final Blacklist.Config entityBlacklistConfig;

	private static final List<String> knockbackBlacklistDefault = List.of("minecraft:ender_dragon", "minecraft:wither");

	public boolean disableExplosionRandomness = true;
	public boolean enablePoofParticles = false;
	public double blockingDamageScaling = 1d;
	public boolean knockbackScalesWithSize = true;
	public boolean explosionAtHalfEntity = true;
	public boolean affectJustSpawnedEntities = false;
	public boolean enableFlyingBlocks = false;
	public boolean creeperCollateral = false;
	public Blacklist knockbackBlacklist;
	public Blacklist entityBlacklist;

	public ExplosionOverhaul(Module module) {
		super(ITCommonConfig.builder, module);
		ITCommonConfig.builder.comment(this.getDescription()).push(this.getName());
		disableExplosionRandomnessConfig = ITCommonConfig.builder
				.comment("Vanilla Explosions use a random number that changes the explosion power. With this enabled the ray strength will be as the explosion size.")
				.define("Disable Explosion Randomness", disableExplosionRandomness);
		enablePoofParticlesConfig = ITCommonConfig.builder
				.comment("Somewhere around 1.15 Mojang (for performance issues) removed the poof particles from Explosions. Keep them disabled if you have a low end PC.\n" +
						"These particles aren't shown when explosion power is <= 1")
				.define("Enable Poof Particles", enablePoofParticles);
		blockingDamageScalingConfig = ITCommonConfig.builder
				.comment("How much damage will the player take when blocking an explosion with a shield. Putting 0 shields will block all the damage like Vanilla, while putting 1 shields will block no damage.")
				.defineInRange("Blocking Damage Scaling", blockingDamageScaling, 0.0d, 1.0d);
		knockbackScalesWithSizeConfig = ITCommonConfig.builder
				.comment("While enabled knockback is greatly increased by explosion size")
				.define("Knockback Scales With Size", knockbackScalesWithSize);
		explosionAtHalfEntityConfig = ITCommonConfig.builder
				.comment("Explosions will start from the middle of the entity instead of feets.")
				.define("Explosions at Half Entity", explosionAtHalfEntity);
		affectJustSpawnedEntitiesConfig = ITCommonConfig.builder
				.comment("Explosions affect even entities spawned by the explosions, like TnTs or chests content. BE AWARE that containers content will get destroyed.")
				.define("Explosion Affect Just Spawned Entities", this.affectJustSpawnedEntities);
		enableFlyingBlocksConfig = ITCommonConfig.builder
				.comment("EXPERIMENTAL! This will make explosion blast blocks away. Blocks that can't land will drop the block as a TNT would have destroyed it.")
				.define("Enable Flying Blocks", enableFlyingBlocks);
		creeperCollateralConfig = ITCommonConfig.builder
				.comment("If true, creepers explosions will drop no blocks.")
				.define("Creeper collateral", this.creeperCollateral);
		knockbackBlacklistConfig = new Blacklist.Config(ITCommonConfig.builder, "Knockback Blacklist", "A list of mobs (and optionally dimensions) that should take reduced knockback. Non-living entities are blacklisted by default.")
				.setDefaultList(knockbackBlacklistDefault)
				.setIsDefaultWhitelist(false)
				.build();
		entityBlacklistConfig = new Blacklist.Config(ITCommonConfig.builder, "Entity Blacklist", "A list of entities that should not use the mod's explosion.")
				.setDefaultList(Collections.emptyList())
				.setIsDefaultWhitelist(false)
				.build();
		ITCommonConfig.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.disableExplosionRandomness = this.disableExplosionRandomnessConfig.get();
		this.enablePoofParticles = this.enablePoofParticlesConfig.get();
		this.blockingDamageScaling = this.blockingDamageScalingConfig.get();
		this.knockbackScalesWithSize = this.knockbackScalesWithSizeConfig.get();
		this.explosionAtHalfEntity = this.explosionAtHalfEntityConfig.get();
		this.affectJustSpawnedEntities = this.affectJustSpawnedEntitiesConfig.get();
		this.enableFlyingBlocks = this.enableFlyingBlocksConfig.get();
		this.creeperCollateral = this.creeperCollateralConfig.get();
		this.knockbackBlacklist = this.knockbackBlacklistConfig.get();
		this.entityBlacklist = this.entityBlacklistConfig.get();
	}

	@SubscribeEvent
	public void explosionPoofParticles(ExplosionEvent.Detonate event) {
		if (!this.isEnabled())
			return;

		if (!this.enablePoofParticles)
			return;

		Explosion e = event.getExplosion();
		if (e.level instanceof ServerLevel level && !e.getToBlow().isEmpty() && e.radius >= 2) {
			int particleCount = (int)(e.radius * 125);
			level.sendParticles(ParticleTypes.POOF, e.getPosition().x(), e.getPosition().y(), e.getPosition().z(), particleCount, e.radius / 4f, e.radius / 4f, e.radius / 4f, 0.33D);
		}
	}

	//Setting the lowest priority so other mods can change explosions params before creating the ITExplosion
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onExplosionStart(ExplosionEvent.Start event) {
		if (!this.isEnabled())
			return;

		if (event.getExplosion().getExploder() != null && this.isBlacklisted(event.getExplosion().getExploder()))
			return;

		event.setCanceled(true);
		Explosion e = event.getExplosion();
		double y = e.getPosition().y;
		if (e.source != null && this.explosionAtHalfEntity)
			y += e.source.getBbHeight() / 2d;
		ITExplosion explosion = new ITExplosion(e.level, e.source, e.getDamageSource(), e.damageCalculator, e.getPosition().x, y, e.getPosition().z, e.radius, e.fire, e.blockInteraction, this.creeperCollateral);

		if (!event.getWorld().isClientSide) {
			ServerLevel world = (ServerLevel) event.getWorld();
			explosion.gatherAffectedBlocks(!this.disableExplosionRandomness);
			if (this.enableFlyingBlocks)
				explosion.fallingBlocks();
			explosion.destroyBlocks();
			explosion.processEntities(this.blockingDamageScaling, this.knockbackScalesWithSize);
			explosion.dropItems();
			explosion.processFire();
			if (explosion.blockInteraction == Explosion.BlockInteraction.NONE) {
				explosion.clearToBlow();
			}
			for (ServerPlayer serverPlayer : world.players()) {
				if (serverPlayer.distanceToSqr(explosion.getPosition().x, explosion.getPosition().y, explosion.getPosition().z) < 4096.0D) {
					serverPlayer.connection.send(new ClientboundExplodePacket(explosion.getPosition().x, explosion.getPosition().y, event.getExplosion().getPosition().z, explosion.radius, explosion.getToBlow(), explosion.getHitPlayers().get(serverPlayer)));
				}
			}
		}
		else {
			explosion.gatherAffectedBlocks(!this.disableExplosionRandomness);
			if (this.enableFlyingBlocks)
				explosion.fallingBlocks();
			explosion.destroyBlocks();
			explosion.playSound();
			explosion.spawnParticles();
			explosion.processFire();
			explosion.finalizeExplosion(true);
		}
	}

	public boolean shouldTakeReducedKnockback(Entity entity) {
		if (!(entity instanceof LivingEntity))
			return true;

		return this.knockbackBlacklist.isEntityBlackOrNotWhitelist(entity.getType());
	}

	public boolean isBlacklisted(Entity entity) {
		return this.entityBlacklist.isEntityBlackOrNotWhitelist(entity.getType());
	}
}