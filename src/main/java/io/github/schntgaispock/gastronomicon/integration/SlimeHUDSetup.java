package io.github.schntgaispock.gastronomicon.integration;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import io.github.schntgaispock.gastronomicon.core.items.workstations.automatic.ElectricKitchen;
import io.github.schntgaispock.slimehud.SlimeHUD;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import lombok.experimental.UtilityClass;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;

@UtilityClass
public class SlimeHUDSetup {

    @SuppressWarnings("deprecation")
    public static void setup() {
        SlimeHUD.getHudController().registerCustomHandler(ElectricKitchen.class, request -> {
            final BlockMenu menu = BlockStorage.getInventory(request.getLocation());
            if (menu == null) return "";
            final ItemStack item = menu.getItemInSlot(15);
            if (item == null) return "";
            final SlimefunItem sfItem = SlimefunItem.getByItem(item);
            if (sfItem == null || !sfItem.getId().equals("GN_CHEF_ANDROID")) return "";
            final List<String> lore = item.getLore();
            if (lore == null || lore.size() < 1) return "";
            return "&7| " + lore.get(0);
        });
    }

}
