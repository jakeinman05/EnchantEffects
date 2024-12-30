package com.notvergin.modname.Flame;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

import static com.notvergin.modname.main.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class FlameEffects
{
    static Level level;
    static Player player;
    static Vec3 playerPos = new Vec3(0, 0, 0); // base player pos
    static int tickCount = 0;
    static boolean findEntities = false;
    public static List<Entity> levelEntities = new ArrayList<>();

    @SubscribeEvent
    public static void onArrowHit(ProjectileImpactEvent event)
    {
        Projectile projectile = event.getProjectile();
        if(projectile instanceof AbstractArrow arrow && arrow.isOnFire())
        {
            level = arrow.level();

            System.out.println("FireProjectileEvent");

            if(arrow.isCritArrow())
                particleEffect(level, arrow, true);
            else
                particleEffect(level, arrow, false);
        }
    }

    public static AABB getWorldSize()
    {
        // Convert player's eye position to chunk coordinates
        int chunkX = (int) Math.floor(playerPos.x) >> 4;
        int chunkZ = (int) Math.floor(playerPos.z) >> 4;

        // Calculate the range of chunks (3x3 area means -1 to +1 in both directions)
        int minChunkX = chunkX - 1;
        int maxChunkX = chunkX + 1;
        int minChunkZ = chunkZ - 1;
        int maxChunkZ = chunkZ + 1;

        // Convert chunk coordinates back to block coordinates
        int minX = minChunkX * 16;
        int maxX = (maxChunkX + 1) * 16 - 1; // +1 to include the last chunk, -1 for the inclusive bound
        int minZ = minChunkZ * 16;
        int maxZ = (maxChunkZ + 1) * 16 - 1;

        // Assuming world height is -64 to 319
        return new AABB(minX, -64, minZ, maxX, 319, maxZ);
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event)
    {
        tickCount++;

        if(findEntities)
        {
            level = event.level;
            AABB player4ChunckRad = getWorldSize();

            levelEntities = level.getEntities((Entity) null, player4ChunckRad, (entity) -> entity instanceof AbstractArrow);


            System.out.println("Counted " + levelEntities.size() + " entities");
            findEntities = false;
        }

        if(!levelEntities.isEmpty())
        {
            for(Entity entity : levelEntities)
            {
                if(entity instanceof AbstractArrow arrow)
                {
                    if(tickCount % 10 == 0 && arrow.isAlive() && !arrow.onGround())
                        level.addParticle(ParticleTypes.LAVA, entity.position().x, entity.position().y, entity.position().z, 0.0D, 0.0D, 0.0D);
                }
            }
        }

    }

    //////      FIXED PARTICLES SPAWNING AFTER PICKUP       //////
            //////      STILL NEED TO FIX       //////
        /// PARTICLES ONLY SPAWNING AFTER ARROWLOOSE
        /// PARTICLES STILL SPAWNING ON GROUND

    @SubscribeEvent
    public static void getPlayerPos(ArrowLooseEvent event)
    {
        player = event.getEntity();
        System.out.println("got PlayerPos");
        playerPos = player.getEyePosition();
        findEntities = true;
    }

    public static void particleEffect(Level level, AbstractArrow arrow, boolean isCrit)
    {
        int $$1 = -1;
        double $$2 = (double)($$1 >> 16 & 255) / (double)255.0F;
        double $$3 = (double)($$1 >> 8 & 255) / (double)255.0F;
        double $$4 = (double)($$1 & 255) / (double)255.0F;

        if(isCrit)
        {
            for(int $$5 = 0; $$5 < 5 * arrow.getBaseDamage(); ++$$5)
                level.addParticle(ParticleTypes.LAVA, arrow.getRandomX((double)0.5F), arrow.getRandomY(), arrow.getRandomZ((double)0.5F), $$2, $$3, $$4);
        }
        else
        {
            for(int $$5 = 0; $$5 < 5; ++$$5)
                level.addParticle(ParticleTypes.LAVA, arrow.getRandomX((double)0.5F), arrow.getRandomY(), arrow.getRandomZ((double)0.5F), $$2, $$3, $$4);
        }
    }

    // LivingGetProjectileEvent
    // useful for changing mechanic of what arrow/projectile
    // will be shot based on custom conditions (maybe spawning new arrows for better effects)
}
