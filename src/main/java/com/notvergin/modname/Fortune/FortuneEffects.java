package com.notvergin.modname.Fortune;

import net.minecraft.client.particle.Particle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

import static com.notvergin.modname.main.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FortuneEffects
{
    public static Level level;
    public static int drops;

    //@SubscribeEvent
    public static void ballsBreak(BlockEvent.BreakEvent event)
    {
        level = (Level) event.getLevel();
        // pos of block broken
        BlockPos pos = event.getPos();
        BlockState block = event.getState();
        Player player = event.getPlayer();
        ItemStack tool = player.getMainHandItem();
        int enchantLvl = tool.getEnchantmentLevel(Enchantments.BLOCK_FORTUNE);
        System.out.println("Block Break");
        fortuneParticles(level, pos, enchantLvl);

//        if(!level.isClientSide())
//        {
//            if(enchantLvl > 0 && tool.is(Tags.Items.TOOLS) && block.toString().contains("ore"))
//            {
//                System.out.println("Fortune break block");
//                if(level.isClientSide())
//                {
//                    level.addParticle(ParticleTypes.HAPPY_VILLAGER,
//                            pos.getX() + (Math.random() - 0.5) * 0.5,
//                            pos.getY() + (Math.random() - 0.5) * 0.5,
//                            pos.getZ() + (Math.random() - 0.5) * 0.5,
//                            0, 0, 0);
//                }
//            }
//        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        // Get the world, block, and position
        LevelAccessor level = event.getLevel();
        Block block = event.getState().getBlock();
        BlockPos pos = event.getPos();
        Player player = event.getPlayer();
        ItemStack tool = player.getMainHandItem();
        int fortuneLevel = tool.getEnchantmentLevel(Enchantments.BLOCK_FORTUNE);
        System.out.println("Block Break");
        if(level instanceof ServerLevel level1)
        {
            ////// FIGURE OUT HOW TO GET ACTUAL ITEM DROPS //////
            LootParams.Builder dropsBuilder = new LootParams.Builder(level1)
                    .withParameter(LootContextParams.TOOL, tool)
                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                    .withOptionalParameter(LootContextParams.THIS_ENTITY, player);

            List<ItemStack> dropsList = event.getState().getDrops(dropsBuilder);
            int drops = 0;
            for(ItemStack drop : dropsList)
                drops += drop.getCount();

            System.out.println("Drops: " + drops);

            if(fortuneLevel > 0 && block.toString().contains("ore") && drops > 1)
            {
                for (int i = 0; i < 4+(fortuneLevel*2); i++)
                {
                    double offsetX = level1.random.nextDouble() - 0.5;
                    double offsetY = level1.random.nextDouble() - 0.5;
                    double offsetZ = level1.random.nextDouble() - 0.5;

                    level1.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                            pos.getX() + 0.5 + offsetX,
                            pos.getY() + 0.5 + offsetY,
                            pos.getZ() + 0.5 + offsetZ,
                            0, 0, 0, 0, 0);
                }
            }
        }
    }

    public static void fortuneParticles(Level level, BlockPos pos, int enchantLvl)
    {
        if(level.isClientSide())
        {
            Vec3 pos1 = pos.getCenter();
            for(int i=0 ; i<3+enchantLvl ; i++)
            {
                level.addParticle(ParticleTypes.HAPPY_VILLAGER, pos1.x*Math.random(), pos1.y*Math.random(), pos1.z*Math.random(), 0, 0, 0);
            }
        }
    }
}
