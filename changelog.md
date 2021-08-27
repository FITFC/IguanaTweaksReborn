# Changelog

## Upcoming
* Explosions now generate in the middle of the entity instead of at the bottom
* Explosions no longer give knockback if the entity is behind a (non-destroyed) wall
* Shields no longer decrease explosions' knockback
* Fixed Food hunger multiplier set to 1 changing food hunger anyway

## 2.7.5
* Reduced Diamond Armor nerf (-1.25 -> -1 toughness)
* Fixed starveSpeed showing up on action bar when hunger <= 4

## 2.7.4
* Hunger Health
    * Added Health Regen feature
        * Has two presets and a huge list of config options
            * Combat Test Preset  
              Health and Hunger work like the Combat Test Snapshots
            * IguanaTweaks Preset (default)
              Health regen boost is removed when hunger at max and the player has saturation.  
              Hunger consumption is continuous when regenerating health instead of only when regenerating one hp.  
              Health regens when hunger >= 7. Health regens one hp every 10 seconds, increased / decreased by Well Fed / Injured effects obtained when eating / taking damage.  
              You start taking Starve damage from hunger 4 instead of 0 (1 damage every 30 seconds at 4 hunger in normal difficulty), less hunger = faster starvation, also in hard you take damage faster while in easy/peaceful slower. Also also you can die out of starvation at any difficulty.  
    * Added Food Consuming Feature
        * Food is consumed slower based on hunger + saturation given
        * Makes Potions and Milk be consumed faster like the Combat Test Snapshots
    * Food healing has been moved to Health Regen Feature. Also the healing formula has changed (33% of hunger restored -> 10% of hunger + saturation restored)
    * Rotten Flesh and potions are no longer blacklisted from saturation and hunger reductions
* Combat
    * Stats Feature
        * Added Power and Arrows nerf
          Power deals half damage and arrows no longer randomly critically strike
* Protection enchantment can have the max level reduced to 3 (disabled by deafult)
* Increased experience dropped on Death (50% -> 80%)
* Milk can no longer cure Debuff Feature's effects
* Fixed wrong config option name for Tool Nerf

## 2.6.0
* Combat
    * Added Stats Feature  
      Swords, Axes and Tridents get -1 damage, Diamond armor gets -1.5 toughness, Netherite gets a total of +2 armor
      points and the Protection enchantment is disabled.
* Mining
    * Added Wrong Tool Feature  
      Disabled by default, prevents the player from Mining blocks without a proper tool. Mostly for modpack developers
* Experience
    * Added Player Experience Feature  
      Experience required to level up now scales less exponentially (vanilla level 30 requires 1392 experience, with
      this feature active it requires about the same: 1395, BUT, vanilla level 100 requires about 31k experience while
      with this active only 15k)  
      On death, players will drop 50% experience instead of at most 7 levels
* Hunger Health
    * Changed Exhaustion on block break to exhaustion while breaking a block. This means that you'll now get exhaustion
      during the breaking of the block instead of when the block breaks
* Misc
    * Added Temporary Spawners  
      Spawners will be disabled after some mobs spawned. The farther from world spawn, the more mobs the spawner will be
      able to generate before deactivating. Also, when broken, now spawners give bonus experience the farther from world
      spawn