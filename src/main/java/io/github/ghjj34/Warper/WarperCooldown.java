package io.github.ghjj34.Warper;

import static org.bukkit.util.NumberConversions.round;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class WarperCooldown {

    public static boolean cooldownChecker(boolean cooldown, double cooldownSet) {
        if (!cooldown) {
            return false;
        } else {
            return !((15d - ((System.currentTimeMillis() - cooldownSet) / 1000d)) <= 0);
        }
    }

    public static int getCooldownTimer(double cooldownSet) {
        return round(15d - ((System.currentTimeMillis() - cooldownSet) / 1000d));
    }

    public static class CooldownEnder implements Runnable {

        private Player sender;

        public CooldownEnder(Player sender) {
            this.sender = sender;
        }

        @Override
        public void run() {
            org.bukkit.Location soundloc = sender.getLocation();
            sender.playSound(soundloc, Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, 2f, 1f);
            sender.sendMessage("§aYour Warper is ready to use!");
        }
    }
}
