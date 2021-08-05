package io.github.ghjj34.Warper;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.util.Location;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;
import java.util.Map;


public class WarperListener implements Listener {

    private Map<Player, Long> timers = new HashMap<>();
    private Map<Player, Boolean> cooldowns = new HashMap<>();
    long trigger = 0;
    long cooldownSet = 0l;
    boolean cooldown = false;
    int cooldownTimer;
    private final WarperPlugin plugin = WarperPlugin.getInstance();

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        timers.put(player,cooldownSet);
        cooldowns.put(player,cooldown);
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player sender = event.getPlayer();
        com.sk89q.worldedit.entity.Player player = BukkitAdapter.adapt(event.getPlayer());
        if (event.getHand() == EquipmentSlot.OFF_HAND) {
            event.setCancelled(true);
            return;
        } else if (event.getHand() == EquipmentSlot.HAND) {
            if (event.getMaterial() == Material.BLAZE_ROD) {
                cooldownSet = timers.get(sender);
                cooldown = cooldowns.get(sender);
                cooldown = WarperCooldown.cooldownChecker(cooldown, cooldownSet);
                if (System.currentTimeMillis() - trigger < 100) {
                    return;
                } else {
                    if (event.getAction() == Action.valueOf("RIGHT_CLICK_AIR")) {
                        if (cooldown) {
                            cooldownTimer = WarperCooldown.getCooldownTimer(cooldownSet);
                            sender.sendMessage("§6On cooldown! " + cooldownTimer + " §6seconds remain until Warp is recharged.");
                        } else {
                            org.bukkit.Location preveffectloc = sender.getLocation();
                            Location pos = player.getLocation();
                            Vector3 loc = pos.toVector();
                            Vector3 dir = pos.getDirection();
                            Vector3 adddir = dir.multiply(5);
                            Vector3 newloc = loc.add(adddir);
                            Location newpos = pos.setPosition(newloc);
                            player.setPosition(newloc);
                            player.findFreePosition(newpos);
                            org.bukkit.Location effectloc = sender.getLocation();
                            preveffectloc.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, preveffectloc, 1);
                            preveffectloc.getWorld().playSound(effectloc, Sound.ENTITY_ENDERMAN_TELEPORT, 2f, 1f);
                            preveffectloc.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, effectloc, 1);
                            cooldowns.replace(sender,true);
                            timers.replace(sender, System.currentTimeMillis());
                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new WarperCooldown.CooldownEnder(sender), 300L);
                            sender.sendMessage("§dAir Warped!");
                        }
                    } else if (event.getAction() == Action.valueOf(("RIGHT_CLICK_BLOCK"))) {
                        if (cooldown) {
                            cooldownTimer = WarperCooldown.getCooldownTimer(cooldownSet);
                            sender.sendMessage("§6On cooldown! " + cooldownTimer + " §6seconds remain until Warp is recharged.");
                        } else {
                            org.bukkit.Location preveffectloc = sender.getLocation();
                            Location wallpos = player.getLocation();
                            Vector3 wallloc = wallpos.toVector();
                            if (player.passThroughForwardWall(6)) {
                                Location checkwallpos = player.getLocation();
                                player.findFreePosition(checkwallpos);
                                Location newwallpos = player.getLocation();
                                Vector3 newwallloc = newwallpos.toVector();
                                Vector3 wallchange = newwallloc.subtract(wallloc);
                                if (wallchange.length() > 6) {
                                    Vector3 walldir = wallpos.getDirection();
                                    Vector3 addwalldir = walldir.multiply(6);
                                    Vector3 sendwallloc = wallloc.add(addwalldir);
                                    Location sendwallpos = wallpos.setPosition(sendwallloc);
                                    player.setPosition(sendwallloc);
                                    player.findFreePosition(sendwallpos);
                                }
                                org.bukkit.Location effectloc = sender.getLocation();
                                preveffectloc.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, preveffectloc, 3);
                                preveffectloc.getWorld().playSound(effectloc, Sound.ENTITY_SHULKER_SHOOT, 2f, 1f);
                                preveffectloc.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, effectloc, 3);
                                cooldowns.replace(sender,true);
                                timers.replace(sender, System.currentTimeMillis());
                                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new WarperCooldown.CooldownEnder(sender), 300L);
                                sender.sendMessage("§dWall Warped!");
                            } else {
                                sender.sendMessage("§cWarp failed! No space ahead found.");
                            }
                        }
                    } else {
                        event.setCancelled(true);
                    }
                }
            }
        }
        trigger = System.currentTimeMillis();
    }
}
