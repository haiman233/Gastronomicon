package io.github.schntgaispock.gastronomicon.core.listeners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import io.github.schntgaispock.gastronomicon.Gastronomicon;
import io.github.schntgaispock.gastronomicon.util.NumberUtil;

public class WildHarvestListener implements Listener {

    private static final Map<Material, List<ItemStack>> dropsByBlock = new HashMap<>();
    private static final Map<EntityType, List<ItemStack>> dropsByMob = new HashMap<>();

    private static double BLOCK_BREAK_DROP_CHANCE;
    private static double MOB_KILL_DROP_CHANCE;

    @ParametersAreNonnullByDefault
    public static void registerDrops(Material dropFrom, ItemStack... drops) {
        for (ItemStack drop : drops) {
            if (dropsByBlock.containsKey(dropFrom)) {
                dropsByBlock.get(dropFrom).add(drop);
            } else {
                final List<ItemStack> newSet = new ArrayList<>();
                newSet.add(drop);
                dropsByBlock.put(dropFrom, newSet);
            }
        }
    }

    @ParametersAreNonnullByDefault
    public static void registerDrops(EntityType dropFrom, ItemStack... drops) {
        for (ItemStack drop : drops) {
            if (dropsByMob.containsKey(dropFrom)) {
                dropsByMob.get(dropFrom).add(drop);
            } else {
                final List<ItemStack> newSet = new ArrayList<>();
                newSet.add(drop);
                dropsByMob.put(dropFrom, newSet);
            }
        }
    }

    @Nullable
    public static List<ItemStack> getDrops(@Nonnull Material dropFrom) {
        return dropsByBlock.containsKey(dropFrom) ? Collections.unmodifiableList(dropsByBlock.get(dropFrom)) : null;
    }

    @Nullable
    public static List<ItemStack> getDrops(@Nonnull EntityType dropFrom) {
        return dropsByMob.containsKey(dropFrom) ? Collections.unmodifiableList(dropsByMob.get(dropFrom)) : null;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        final Block b = e.getBlock();
        if (b == null) return;

        final List<ItemStack> drops = getDrops(b.getType());
        if (drops == null) return;

        final ItemStack weapon = e.getPlayer().getInventory().getItemInMainHand();
        final int fortune = weapon == null ? 0 : weapon.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
        if (NumberUtil.flip(BLOCK_BREAK_DROP_CHANCE + fortune)) {
            final ItemStack drop = drops.get(NumberUtil.getRandom().nextInt(drops.size()));
            e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), drop);
        }
    }

    @EventHandler
    public void onMobKill(EntityDeathEvent e) {
        final List<ItemStack> drops = getDrops(e.getEntityType());
        if (drops == null) return;

        final Player killer = e.getEntity().getKiller();
        final int looting;
        if (killer == null || killer.getInventory().getItemInMainHand() == null) {
            looting = 0;
        }
        else {
            looting = killer.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
        }
        if (NumberUtil.flip(MOB_KILL_DROP_CHANCE + looting)) {
            final ItemStack drop = drops.get(NumberUtil.getRandom().nextInt(drops.size()));
            e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(), drop);
        }
    }

    public static void setup() {
        Bukkit.getPluginManager().registerEvents(new WildHarvestListener(), Gastronomicon.getInstance());
        BLOCK_BREAK_DROP_CHANCE = Gastronomicon.getInstance().getConfig().getDouble("drops.block-break-chance");
        MOB_KILL_DROP_CHANCE = Gastronomicon.getInstance().getConfig().getDouble("drops.mob-kill-chance");
    }

}
