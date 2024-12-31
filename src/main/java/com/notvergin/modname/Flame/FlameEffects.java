package com.notvergin.modname.Flame;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Set;

import static com.notvergin.modname.main.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class FlameEffects
{
    static int tickCount = 0;
    private static Set<AbstractArrow> flyingArrows = new HashSet<>();

    @SubscribeEvent
    protected static void onArrowHit(ProjectileImpactEvent event)
    {
        Projectile projectile = event.getProjectile();
        if(projectile instanceof AbstractArrow arrow && arrow.isOnFire())
        {
            // remove arrow from set
            flyingArrows.remove(arrow);

            Level level = arrow.level();
            // particle effects
            if(arrow.isCritArrow())
                particleEffect(level, arrow, true);
            else
                particleEffect(level, arrow, false);
        }
    }

    @SubscribeEvent
    public static void onWorldStart(LevelEvent.Load event) {
        // resets tickCount for arrows spawned on world load
        tickCount = 0;
    }

    @SubscribeEvent
    protected static void onWorldTick(TickEvent.LevelTickEvent event)
    {
        tickCount++;

        if(!flyingArrows.isEmpty())
        {
            // for all arrows in set
            for(AbstractArrow arrow : flyingArrows)
            {
                if(arrow.isAlive() && arrow.isOnFire())
                {
                    // particle in air every 5 ticks
                    if(tickCount % 5 == 0)
                        event.level.addParticle(ParticleTypes.LAVA, arrow.position().x, arrow.position().y, arrow.position().z, 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }

    @SubscribeEvent
    protected static void arrowSpawn(EntityJoinLevelEvent event)
    {
        if(event.getEntity() instanceof AbstractArrow arrow)
        {
            // adds new arrow to set           (ignores arrows on world load)
            if(arrow.isAlive() && arrow.isOnFire() && tickCount > 600)
                flyingArrows.add(arrow);

        }
    }

    protected static void particleEffect(Level level, AbstractArrow arrow, boolean isCrit)
    {
        int $$1 = -1;
        double R = (double)($$1 >> 16 & 255) / (double)255.0F;
        double G = (double)($$1 >> 8 & 255) / (double)255.0F;
        double B = (double)($$1 & 255) / (double)255.0F;

        if(isCrit)
        {
            for(int i = 0; i < 5 * arrow.getBaseDamage(); ++i)
                level.addParticle(ParticleTypes.LAVA, arrow.getRandomX((double)0.5F), arrow.getRandomY(), arrow.getRandomZ((double)0.5F), R, G, B);
        }
        else
        {
            for(int i = 0; i < 5; ++i)
                level.addParticle(ParticleTypes.LAVA, arrow.getRandomX((double)0.5F), arrow.getRandomY(), arrow.getRandomZ((double)0.5F), R, G, B);
        }
    }

    // LivingGetProjectileEvent
    // useful for changing mechanic of what arrow/projectile
    // will be shot based on custom conditions (maybe spawning new arrows for better effects)
}
