package io.github.ghjj34.Warper;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.util.Location;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.libs.org.apache.maven.model.validation.DefaultModelValidator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;


public class WarperListener implements Listener {

    long trigger = 0;
    boolean cooldown = false;
    double cooldownSet;
    int cooldownTimer;
    private final WarperPlugin plugin = WarperPlugin.getInstance();

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        System.out.println("PlayerInteractEvent Working");
        Player sender = event.getPlayer();
        com.sk89q.worldedit.entity.Player player = BukkitAdapter.adapt(event.getPlayer());
        if (event.getHand() == EquipmentSlot.OFF_HAND) {
            System.out.println("Off Hand Triggered");
            event.setCancelled(true);
            return;
        } else if (event.getHand() == EquipmentSlot.HAND) {
            System.out.println("GetHand Working");
            if (event.getMaterial() == Material.BLAZE_ROD) {
                cooldown = WarperCooldown.cooldownChecker(cooldown, cooldownSet);
                if (System.currentTimeMillis() - trigger < 100) {
                    return;
                } else {
                    System.out.println("GetItem Working");
                    if (event.getAction() == Action.valueOf("RIGHT_CLICK_AIR")) {
                        System.out.println("RightAirCLick Working");
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
                            sender.spawnParticle(Particle.EXPLOSION_LARGE, preveffectloc, 1);
                            sender.playSound(effectloc, Sound.ENTITY_ENDERMAN_TELEPORT, 2f, 1f);
                            sender.spawnParticle(Particle.EXPLOSION_LARGE, effectloc, 1);
                            cooldown = true;
                            cooldownSet = System.currentTimeMillis();
                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new WarperCooldown.CooldownEnder(sender), 300L);
                            sender.sendMessage("§dAir Warped!");
                        }
                    } else if (event.getAction() == Action.valueOf(("RIGHT_CLICK_BLOCK"))) {
                        System.out.println("RightBlockCLick Working");
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
                                    System.out.println("Alt Wall Warped");
                                }
                                org.bukkit.Location effectloc = sender.getLocation();
                                sender.spawnParticle(Particle.EXPLOSION_LARGE, preveffectloc, 1);
                                sender.playSound(effectloc, Sound.ENTITY_SHULKER_SHOOT, 2f, 1f);
                                sender.spawnParticle(Particle.EXPLOSION_LARGE, effectloc, 1);
                                cooldown = true;
                                cooldownSet = System.currentTimeMillis();
                                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new WarperCooldown.CooldownEnder(sender), 300L);
                                sender.sendMessage("§dWall Warped!");
                            } else {
                                sender.sendMessage("§cWarp failed! No space ahead found.");
                            }
                        }
                    } else {
                        System.out.println("Some Other Action");
                        event.setCancelled(true);
                    }
                }
            }
        }
        trigger = System.currentTimeMillis();
        System.out.println("Action Ended");
    }
}
