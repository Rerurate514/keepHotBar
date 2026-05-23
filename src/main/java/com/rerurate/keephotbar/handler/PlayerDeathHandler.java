package com.rerurate.keephotbar.handler;

import com.rerurate.keephotbar.Keephotbar;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class PlayerDeathHandler {
    private static final int HOTBAR_SIZE = 9;

    private final java.util.Map<java.util.UUID, List<ItemStack>> savedHotbars = new java.util.HashMap<>();

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        List<ItemStack> hotbarItems = new ArrayList<>();
        for (int slot = 0; slot < HOTBAR_SIZE; slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            hotbarItems.add(stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
        }

        savedHotbars.put(player.getUUID(), hotbarItems);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerDrops(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        List<ItemStack> hotbarItems = savedHotbars.get(player.getUUID());
        if (hotbarItems == null) {
            return;
        }

        for (ItemStack saved : hotbarItems) {
            if (saved.isEmpty()) continue;

            event.getDrops().removeIf(drop -> {
                ItemStack dropStack = drop.getItem();
                if (dropStack.getItem() == saved.getItem()
                        && dropStack.getCount() == saved.getCount()
                        && ItemStack.isSameItemSameTags(dropStack, saved)) {
                    return true;
                }
                return false;
            });
        }
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        java.util.UUID uuid = player.getUUID();

        if (!savedHotbars.containsKey(uuid)) {
            return;
        }

        List<ItemStack> hotbarItems = savedHotbars.remove(uuid);

        for (int slot = 0; slot < HOTBAR_SIZE && slot < hotbarItems.size(); slot++) {
            ItemStack stack = hotbarItems.get(slot);
            if (!stack.isEmpty()) {
                player.getInventory().setItem(slot, stack.copy());
            }
        }
    }

    @SubscribeEvent
    public void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        java.util.UUID uuid = player.getUUID();

        if (!savedHotbars.containsKey(uuid)) {
            return;
        }

        List<ItemStack> hotbarItems = savedHotbars.remove(uuid);

        for (int slot = 0; slot < HOTBAR_SIZE && slot < hotbarItems.size(); slot++) {
            ItemStack stack = hotbarItems.get(slot);
            if (!stack.isEmpty()) {
                player.getInventory().setItem(slot, stack.copy());
            }
        }
    }
}
