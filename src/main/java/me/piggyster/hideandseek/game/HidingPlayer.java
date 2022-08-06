package me.piggyster.hideandseek.game;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import me.piggyster.api.color.ColorAPI;
import me.piggyster.api.menu.item.SimpleItem;
import me.piggyster.api.service.Service;
import me.piggyster.hideandseek.HideAndSeekPlugin;
import me.piggyster.hideandseek.disguise.DisguiseType;
import me.piggyster.hideandseek.nms.Floatingblock;
import me.piggyster.hideandseek.nms.Miniblock;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEffect;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.item.EntityFallingBlock;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

public class HidingPlayer {

    private static final HideAndSeekPlugin plugin = HideAndSeekPlugin.getInstance();

    private static GameService gameService;

    private Player player;
    private DisguiseType type;
    private Miniblock armorStand;
    private Entity entity;
    private boolean grounded;
    private int lives;
    private long lastChange;

    private Floatingblock floatingblock;

    private static final SimpleItem CLOCK = new SimpleItem(Material.CLOCK).setName("&eChange Disguise");

    public HidingPlayer(Player player, DisguiseType type) {
        this.player = player;
        this.type = type;
        lives = 3;
        lastChange = System.currentTimeMillis();
        player.getInventory().clear();
        player.getInventory().setItem(0, CLOCK.build());
        player.setInvisible(true);
        if(gameService == null) {
            gameService = Service.grab(GameService.class);
        }
    }

    public void updateDisguise() {
        if(grounded) return;
        if(type.isBlock()) {
            if (armorStand != null) armorStand.remove();
            armorStand = new Miniblock(type.getMaterial(), player.getLocation().subtract(0, 1.35, 0));
            ((CraftWorld)player.getWorld()).getHandle().addEntity(armorStand);
        } else {
            if(entity != null) entity.remove();
            entity = player.getWorld().spawnEntity(player.getLocation(), type.getEntityType());
            entity.setGravity(false);
            entity.setInvulnerable(true);
            entity.setRotation(player.getLocation().getYaw(), player.getLocation().getPitch());
        }
    }

    public void removeDisguise() {
        if(armorStand != null) armorStand.remove();
        armorStand = null;
        if(entity != null) entity.remove();
        entity = null;
    }

    public void toggleGrounded() {
        if(grounded) {
            grounded = false;
            updateDisguise();
            BlockData data = player.getLocation().getBlock().getBlockData();
            Bukkit.getOnlinePlayers().forEach(p -> {
                p.sendBlockChange(player.getLocation(), data);
            });
            if(floatingblock != null) {
                floatingblock.a(net.minecraft.world.entity.Entity.RemovalReason.b);
                floatingblock = null;
            }
        } else {
            grounded = true;
            if(type.isBlock()) {
                removeDisguise();
                floatingblock = new Floatingblock(player.getLocation().getBlock().getLocation().add(0.5, 0, 0.5), type.getMaterial());
                floatingblock.setYRot(player.getLocation().getYaw());
                floatingblock.setXRot(player.getLocation().getPitch());
                ((CraftWorld) player.getWorld()).getHandle().addEntity(floatingblock);
                PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(floatingblock.getId());
                Bukkit.getOnlinePlayers().forEach(p -> {
                    if(p == player) return;
                    ((CraftPlayer) p).getHandle().b.sendPacket(packet);
                });
            }
        }
    }

    public boolean canChangeBlock() {
        return System.currentTimeMillis() - lastChange > 90000;
    }

    public boolean isGrounded() {
        return grounded;
    }

    public DisguiseType getDisguiseType() {
        return type;
    }

    public void remove() {
        player.setInvisible(false);
        removeDisguise();
        Bukkit.getOnlinePlayers().forEach(p -> {
            p.sendBlockChange(player.getLocation(), player.getLocation().getBlock().getBlockData());
        });
    }

    public Player getPlayer() {
        return player;
    }

    public boolean hit() {
        lives--;
        if(lives <= 0) {
            remove();
            gameService.addSeeker(player);
            return true;

        }
        return false;

    }

    public void setDisguiseType(DisguiseType type) {
        this.type = type;
        lastChange = System.currentTimeMillis();
        if(grounded) {
            toggleGrounded();
        }
    }


}
