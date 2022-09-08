package dev.failures.balancetopx.Utils;

import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtil {
    public static String colorize(String message){
        Pattern hexPattern = Pattern.compile("&#([A-Fa-f0-9]){6}");

        Matcher matcher = hexPattern.matcher(message);
        while (matcher.find()) {
            String color = message.substring(matcher.start()+1,matcher.end());
            message = message.replace("&"+color,ChatColor.of(color) + "");
            matcher = hexPattern.matcher(message);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static List<String> colorizeList(List<String> lore) {
        List<String> updated = new ArrayList<>();
        for(String l: lore) {
            updated.add(colorize(l));
        }
        return updated;
    }
}
