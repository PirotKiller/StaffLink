package me.PirotKiller.staffLink.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class UChat {
    public String permission = format("&cYou do not have permission to access this command.");
    public String prefix = format("&1&l[&fStaff&bLink&1] &e");

    public String format(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    public void sendMessage(Player player, String message){
        player.sendMessage(prefix + format(message));
    }

}
