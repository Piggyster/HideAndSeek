package me.piggyster.hideandseek.listener;

import me.piggyster.api.color.ColorAPI;
import me.piggyster.api.service.Service;
import me.piggyster.hideandseek.game.GameService;
import me.piggyster.hideandseek.game.HidingPlayer;
import me.piggyster.hideandseek.game.SeekingPlayer;
import me.piggyster.hideandseek.menu.SelectblockMenu;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class PlayerListener implements Listener {

    private GameService gameService;

    public PlayerListener() {
        gameService = Service.grab(GameService.class);
    }

    private static final Location SHORT_BUS = new Location(Bukkit.getWorld("event"), -494, 75, -579);

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if(!gameService.isSeeker(event.getPlayer()) && !gameService.isHider(event.getPlayer())) return;
        if(gameService.isSeeker(event.getPlayer())) {
            SeekingPlayer seekingPlayer = gameService.getSeeker(event.getPlayer());
            seekingPlayer.remove();
            gameService.removeSeeker(event.getPlayer());
        } else {
            HidingPlayer hidingPlayer = gameService.getHider(event.getPlayer());
            hidingPlayer.remove();
            gameService.removeHider(hidingPlayer.getPlayer());
        }
        event.getPlayer().teleport(SHORT_BUS);
        event.getPlayer().getInventory().clear();
    }


    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if(!gameService.isHider(event.getPlayer())) return;
        HidingPlayer player = gameService.getHider(event.getPlayer());
        if(player.isGrounded()) {
            if(event.getFrom().getBlock().getLocation().equals(event.getTo().getBlock().getLocation())) return;
            event.setCancelled(true);
        } else {
            player.updateDisguise();
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        if(!gameService.isHider(event.getPlayer())) return;
        if(!event.isSneaking()) return;
        HidingPlayer player = gameService.getHider(event.getPlayer());
        player.toggleGrounded();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(event.getHand() != EquipmentSlot.HAND) return;
        if(event.getClickedBlock() == null) return;
        if(!gameService.isSeeker(event.getPlayer())) return;
        HidingPlayer hidingPlayer = gameService.getHider(event.getClickedBlock().getLocation());
        if(hidingPlayer == null) return;
        event.getPlayer().sendMessage(ColorAPI.process("&aYou hit &2" + hidingPlayer.getPlayer().getName() + "&a!"));
        hidingPlayer.getPlayer().sendMessage(ColorAPI.process("&cYou were hit by &4" + event.getPlayer().getName() + "&c!"));
        hidingPlayer.toggleGrounded();
        boolean dead = hidingPlayer.hit();
        if(dead) {
            Bukkit.broadcastMessage(ColorAPI.process("&6&lHIDE AND SEEK &f&l- &e" + hidingPlayer.getPlayer().getName() + " has been found and eliminated. &7(" + gameService.getHiders().size() + " remaining)"));
        }
        Location loc1 = event.getPlayer().getLocation();
        Location loc2 = hidingPlayer.getPlayer().getLocation();
        hidingPlayer.getPlayer().setVelocity(getVelocity(loc2.getX() - loc1.getX(), loc2.getZ() - loc1.getZ(), 0.8));
        ItemStack itemCrackData = new ItemStack(Material.REDSTONE);
        loc2.getWorld().spawnParticle(Particle.BLOCK_CRACK, loc2, 15, itemCrackData);
        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, 1, 1);
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof Player attacker && event.getEntity() instanceof Player victim) {
            event.setCancelled(true);
            if(!gameService.isHider(victim) || !gameService.isSeeker(attacker)) {
                return;
            }
            HidingPlayer hidingPlayer = gameService.getHider(victim);
            SeekingPlayer seekingPlayer = gameService.getSeeker(victim);
            attacker.sendMessage(ColorAPI.process("&aYou hit &2" + victim.getName() + "&a!"));
            victim.sendMessage(ColorAPI.process("&cYou were hit by &4" + attacker.getName() + "&c!"));
            boolean dead = hidingPlayer.hit();
            if(dead) {
                Bukkit.broadcastMessage(ColorAPI.process("&6&lHIDE AND SEEK &f&l- &e" + victim.getName() + " has been found and eliminated. &7(" + gameService.getHiders().size() + " remaining)"));
            }
            Location loc1 = attacker.getLocation();
            Location loc2 = victim.getLocation();
            hidingPlayer.getPlayer().setVelocity(getVelocity(loc2.getX() - loc1.getX(), loc2.getZ() - loc1.getZ(), 0.8));
            ItemStack itemCrackData = new ItemStack(Material.REDSTONE);
            loc2.getWorld().spawnParticle(Particle.BLOCK_CRACK, loc2, 15, itemCrackData);
            attacker.playSound(attacker.getLocation(), Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, 1, 1);
        }

    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            if(gameService.isHider(event.getPlayer())) {
                ItemStack held = event.getPlayer().getInventory().getItemInMainHand();
                if(held.getType() != Material.CLOCK) return;
                if(!held.hasItemMeta() && !held.getItemMeta().hasDisplayName()) return;
                if(held.getItemMeta().getDisplayName().equals(ColorAPI.process("&eChange Disguise"))) {
                    SelectblockMenu menu = new SelectblockMenu(event.getPlayer());
                    menu.open();
                }
            }
        }
    }

    private Vector getVelocity(double x, double z, double speed) {
        double y = 0.3333; // this way, like normal knockback, it hits a player a little bit up
        double multiplier = Math.sqrt((speed*speed) / (x*x + y*y + z*z)); // get a constant that, when multiplied by the vector, results in the speed we want
        return new Vector(x, y, z).multiply(multiplier).setY(y);
    }

}
