package com.github.jewishbanana.playerarmorchangeevent;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * This event is called when a player changes their armor. This event mimics the paper version and is a substitute for spigot/bukkit compatibility on servers.
 * <br><br>
 * Keep in mind that this event is only fired when vanilla mechanics change armor (e.g. inventory clicks, armor breaking, etc.)
 * <br><br>
 * <STRONG>Armor that is changed or modified by third party plugins will not fire this event!</STRONG>
 */
public class PlayerArmorChangeEvent extends PlayerEvent implements Cancellable {
   
    private static final HandlerList handlers = new HandlerList();
   
    private boolean cancelled;
    private ItemStack oldItem;
    private ItemStack newItem;
    private EquipmentSlot slot;
    private Reason reason;

    public PlayerArmorChangeEvent(@NotNull Player player, EquipmentSlot slot, @NotNull ItemStack oldItem, @NotNull ItemStack newItem, Reason reason) {
        super(player);
        this.slot = slot;
        this.oldItem = oldItem;
        this.newItem = newItem;
        this.reason = reason;
    }
    /**
     * Gets the item that is currently equipped on the player in the armor slot that is being changed. If no armor is present this will return an AIR itemstack.
     * 
     * @return The item currently equipped in the armor slot
     */
    public @NotNull ItemStack getOldItem() {
        return oldItem;
    }
    /**
     * Gets the item that is going to be equipped in the players armor slot that is being changed. If no armor is being equipped then this will return an AIR itemstack.
     * 
     * @return The item that will be equipped in the slot as a result from this event
     */
    public @NotNull ItemStack getNewItem() {
        return newItem;
    }
    /**
     * Gets the armor equipment slot that is changing on the player.
     * 
     * @return The armor equipment slot being changed by this event
     */
    public EquipmentSlot getSlot() {
        return slot;
    }
    /**
     * Gets the reason that the armor is being changed on the player. This is the vanilla related action that is causing this event.
     * 
     * @return The reason the armor is being changed
     */
    public Reason getReason() {
        return reason;
    }
    @Override
    public boolean isCancelled() {
        return cancelled;
    }
    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
    /**
     * Represents a vanilla reason of why a {@link PlayerArmorChangeEvent} is being fired.
     */
    public enum Reason {
    	/**
    	 * Represents an inventory related action such as the player manually placing the item in their armor slot, or shift clicking the armor to equip it.
    	 */
        INVENTORY_ACTION,
        /**
         * Represents a player right clicking an armor piece in their hand to equip it.
         */
        RIGHT_CLICK,
        /**
         * Represents a dispenser block firing and equipping the armor onto the player.
         */
        DISPENSER,
        /**
         * Represents the armor piece breaking as a result of its durability reaching 0.
         */
        ITEM_BREAK;
    }
}
