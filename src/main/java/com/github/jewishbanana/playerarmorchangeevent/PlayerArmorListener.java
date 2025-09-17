package com.github.jewishbanana.playerarmorchangeevent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.RedstoneWire;
import org.bukkit.block.data.type.RedstoneWire.Connection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.jewishbanana.playerarmorchangeevent.PlayerArmorChangeEvent.Reason;

public class PlayerArmorListener implements Listener {
	
    private final Set<EquipmentSlot> armorSlots = new HashSet<>(Arrays.asList(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET));
   
    public PlayerArmorListener(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getResult() == Result.DENY || event.getClickedInventory() == null || !(event.getWhoClicked() instanceof Player))
            return;
        if (event.getSlotType() == SlotType.ARMOR) {
        	Player player = (Player) event.getWhoClicked();
        	switch (event.getAction()) {
        	case PLACE_ALL:
        	case PLACE_SOME:
        	case PLACE_ONE:
        		PlayerArmorChangeEvent placeEvent = new PlayerArmorChangeEvent(player, getEquipmentSlotFromRawID(event.getSlot()), new ItemStack(Material.AIR), event.getCursor(), Reason.INVENTORY_ACTION);
                Bukkit.getPluginManager().callEvent(placeEvent);
                if (placeEvent.isCancelled()) {
                    event.setResult(Result.DENY);
                    if (player.getGameMode() == GameMode.CREATIVE) {
                    	if (player.getInventory().firstEmpty() != -1)
                    		player.getInventory().addItem(event.getCursor());
                    	else
                    		player.setItemOnCursor(event.getCursor());
                    }
                }
                return;
        	case PICKUP_ALL:
        	case PICKUP_HALF:
        	case PICKUP_SOME:
        	case PICKUP_ONE:
        		PlayerArmorChangeEvent pickupEvent = new PlayerArmorChangeEvent(player, getEquipmentSlotFromRawID(event.getSlot()), event.getCurrentItem(), new ItemStack(Material.AIR), Reason.INVENTORY_ACTION);
                Bukkit.getPluginManager().callEvent(pickupEvent);
                if (pickupEvent.isCancelled()) {
                    event.setResult(Result.DENY);
                    if (player.getGameMode() == GameMode.CREATIVE) {
                    	if (player.getInventory().firstEmpty() != -1)
                    		player.getInventory().addItem(event.getCursor());
                    	else
                    		player.setItemOnCursor(event.getCursor());
                    }
                }
                return;
        	case SWAP_WITH_CURSOR:
        		PlayerArmorChangeEvent swapCursorEvent = new PlayerArmorChangeEvent(player, getEquipmentSlotFromRawID(event.getSlot()), event.getCurrentItem(), event.getCursor(), Reason.INVENTORY_ACTION);
                Bukkit.getPluginManager().callEvent(swapCursorEvent);
                if (swapCursorEvent.isCancelled()) {
                    event.setResult(Result.DENY);
                    if (player.getGameMode() == GameMode.CREATIVE) {
                    	player.setItemOnCursor(new ItemStack(Material.AIR));
                    	if (player.getInventory().firstEmpty() != -1)
                    		player.getInventory().addItem(event.getCursor());
                    	else
                    		player.setItemOnCursor(event.getCursor());
                    }
                }
        		return;
        	case HOTBAR_SWAP:
        		ItemStack item = player.getInventory().getItem(event.getHotbarButton());
        		PlayerArmorChangeEvent hotbarEvent = new PlayerArmorChangeEvent(player, getEquipmentSlotFromRawID(event.getSlot()), event.getCurrentItem() != null ? event.getCurrentItem() : new ItemStack(Material.AIR), item != null ? item : new ItemStack(Material.AIR), Reason.INVENTORY_ACTION);
                Bukkit.getPluginManager().callEvent(hotbarEvent);
                if (hotbarEvent.isCancelled())
                    event.setResult(Result.DENY);
                return;
        	case MOVE_TO_OTHER_INVENTORY:
        		PlayerArmorChangeEvent moveToOtherEvent = new PlayerArmorChangeEvent(player, getEquipmentSlotFromRawID(event.getSlot()), event.getCurrentItem(), new ItemStack(Material.AIR), Reason.INVENTORY_ACTION);
                Bukkit.getPluginManager().callEvent(moveToOtherEvent);
                if (moveToOtherEvent.isCancelled())
                    event.setResult(Result.DENY);
                return;
            default:
            	return;
        	}
        } else if (event.isShiftClick() && event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY && event.getClickedInventory().getType() == InventoryType.PLAYER && isNotNullOrAir(event.getCurrentItem()) && getTopInventoryType(event) == InventoryType.CRAFTING) {
        	Player player = (Player) event.getWhoClicked();
        	EquipmentSlot slot = event.getCurrentItem().getType().getEquipmentSlot();
        	if (!slot.toString().equals("BODY") && !isNotNullOrAir(player.getEquipment().getItem(slot))) {
                PlayerArmorChangeEvent armorEvent = new PlayerArmorChangeEvent(player, slot, new ItemStack(Material.AIR), event.getCurrentItem(), Reason.INVENTORY_ACTION);
                Bukkit.getPluginManager().callEvent(armorEvent);
                if (armorEvent.isCancelled()) {
                    event.setResult(Result.DENY);
                    if (player.getGameMode() == GameMode.CREATIVE) {
                    	player.setItemOnCursor(new ItemStack(Material.AIR));
                    	if (player.getInventory().firstEmpty() != -1)
                    		player.getInventory().addItem(event.getCursor());
                    	else
                    		player.setItemOnCursor(event.getCursor());
                    }
                }
                return;
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryDrag(InventoryDragEvent event) {
    	if (event.getResult() == Result.DENY || !(event.getWhoClicked() instanceof Player))
    		return;
    	for (Map.Entry<Integer, ItemStack> entry : event.getNewItems().entrySet()) {
    		final int slot = entry.getKey();
    		if (entry.getValue() != null && slot > 4 && slot < 9 && getViewSlotType(event, slot) == SlotType.ARMOR) {
    			Player player = (Player) event.getWhoClicked();
    			PlayerArmorChangeEvent armorEvent = new PlayerArmorChangeEvent(player, getEquipmentSlotFromID(slot), new ItemStack(Material.AIR), entry.getValue(), Reason.INVENTORY_ACTION);
                Bukkit.getPluginManager().callEvent(armorEvent);
                if (armorEvent.isCancelled()) {
                    event.setResult(Result.DENY);
                    return;
                }
    		}
    	}
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
    	if (event.useItemInHand() == Result.DENY 
    			|| !(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) 
    			|| event.getHand() == null 
    			|| (event.hasBlock() && isInteractable(event.getClickedBlock())))
    		return;
    	Player player = event.getPlayer();
    	ItemStack item = event.getHand() == EquipmentSlot.HAND ? player.getEquipment().getItemInMainHand() : player.getEquipment().getItemInOffHand();
    	if (!isNotNullOrAir(item) || item.getType() == Material.CARVED_PUMPKIN)
    		return;
    	EquipmentSlot slot = item.getType().getEquipmentSlot();
    	if (!armorSlots.contains(slot))
    		return;
    	ItemStack current = player.getEquipment().getItem(slot);
    	PlayerArmorChangeEvent armorEvent = new PlayerArmorChangeEvent(player, slot, current != null ? current : new ItemStack(Material.AIR), item, Reason.RIGHT_CLICK);
    	Bukkit.getPluginManager().callEvent(armorEvent);
    	if (armorEvent.isCancelled())
    		event.setCancelled(true);
    }
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockDispenseArmor(BlockDispenseArmorEvent event) {
    	if (!(event.getTargetEntity() instanceof Player))
            return;
        PlayerArmorChangeEvent armorEvent = new PlayerArmorChangeEvent((Player) event.getTargetEntity(), event.getItem().getType().getEquipmentSlot(), new ItemStack(Material.AIR), event.getItem(), Reason.DISPENSER);
        Bukkit.getPluginManager().callEvent(armorEvent);
        if (armorEvent.isCancelled())
            event.setCancelled(true);
    }
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onItemDamage(PlayerItemDamageEvent event) {
        if (((Damageable) event.getItem().getItemMeta()).getDamage()+event.getDamage() < event.getItem().getType().getMaxDurability())
            return;
        EquipmentSlot slot = event.getItem().getType().getEquipmentSlot();
        if (!armorSlots.contains(slot) || !event.getPlayer().getInventory().getItem(slot).equals(event.getItem()))
        	return;
        PlayerArmorChangeEvent armorEvent = new PlayerArmorChangeEvent(event.getPlayer(), event.getItem().getType().getEquipmentSlot(), event.getItem(), new ItemStack(Material.AIR), Reason.ITEM_BREAK);
        Bukkit.getPluginManager().callEvent(armorEvent);
        if (armorEvent.isCancelled())
            event.setCancelled(true);
    }
    private boolean isNotNullOrAir(ItemStack item) {
        return item == null ? false : item.getType() != Material.AIR;
    }
    private boolean isInteractable(Block block) {
        Material type = block.getType();
        if (!type.isInteractable())
            return false;
        if (Tag.STAIRS.isTagged(type) || Tag.FENCES.isTagged(type) || Tag.CANDLES.isTagged(type) || Tag.CANDLE_CAKES.isTagged(type) || Tag.CAULDRONS.isTagged(type))
            return false;
        switch (type) {
        case MOVING_PISTON:
        case PUMPKIN:
        case CAKE:
            return false;
        case REDSTONE_WIRE:
            RedstoneWire wire = (RedstoneWire) block.getBlockData();
            for (BlockFace face : Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST))
                if (wire.getFace(face) == Connection.NONE)
                    continue;
                else if (wire.getFace(face) == Connection.SIDE) {
                    if (block.getRelative(face).getType() == Material.REDSTONE_WIRE)
                        return false;
                } else
                    return false;
            return true;
        default:
            return true;
        }
    }
    private InventoryType getTopInventoryType(InventoryEvent event) {
        try {
            Object view = event.getView();
            Method getTopInventory = view.getClass().getMethod("getTopInventory");
            getTopInventory.setAccessible(true);
            Inventory inv = (Inventory) getTopInventory.invoke(view);
            return inv == null ? null : inv.getType();
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    private SlotType getViewSlotType(InventoryEvent event, int rawSlot) {
        try {
            Object view = event.getView();
            Method getInventorySlot = view.getClass().getMethod("getSlotType", int.class);
            getInventorySlot.setAccessible(true);
            return (SlotType) getInventorySlot.invoke(view, rawSlot);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    private EquipmentSlot getEquipmentSlotFromRawID(int slot) {
    	switch (slot) {
    	case 36:
    		return EquipmentSlot.FEET;
    	case 37:
    		return EquipmentSlot.LEGS;
    	case 38:
    		return EquipmentSlot.CHEST;
    	case 39:
    		return EquipmentSlot.HEAD;
    	}
    	return null;
    }
    private EquipmentSlot getEquipmentSlotFromID(int slot) {
    	switch (slot) {
    	case 8:
    		return EquipmentSlot.FEET;
    	case 7:
    		return EquipmentSlot.LEGS;
    	case 6:
    		return EquipmentSlot.CHEST;
    	case 5:
    		return EquipmentSlot.HEAD;
    	}
    	return null;
    }
}
