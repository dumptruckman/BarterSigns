package com.dumptruckman.bartersigns.locale;

import com.dumptruckman.bartersigns.font.Font;
import com.dumptruckman.bartersigns.config.ConfigIO;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.util.config.Configuration;

/**
 *
 * @author dumptruckman
 */
public class Language {

    Configuration language;

    /**
     * Loads up the language File into the Configuration
     * @param langFile
     */
    public Language(File langFile) {
        language = new ConfigIO(langFile).load();
    }

    /**
     * Gets a list of the messages for a given path.  Color codes will be
     * converted and any lines too long will be split into an extra element in
     * the list.  %n notated variables n the message will be replaced with the
     * optional arguments passed in.
     * @param path Path of the message in the language yaml file.
     * @param args Optional arguments to replace %n variable notations
     * @return A List of formatted Strings
     */
    public List<String> lang(String path, String... args) {
        // Gets the messages for the path submitted
        List<Object> list = language.getList(path);

        List<String> message = new ArrayList<String>();
        // Parse each item in list
        for (int i = 0; i < list.size(); i++) {
            String temp = list.get(i).toString();
            // Replaces & with the Section character
            temp = temp.replaceAll("&", Character.toString((char)167));
            // If there are arguments, %n notations in the message will be
            // replaced
            if (args != null) {
                for (int j = 0; j < args.length; j++) {
                    temp = temp.replace("%" + (j + 1), args[j]);
                }
            }
            // Pass the line into the line breaker
            List<String> lines = new Font().splitString(temp);
            // Add the broken up lines into the final message list to return
            for (int j = 0; j < lines.size(); j++) {
                message.add(lines.get(j));
            }
        }
        return message;
    }

    /**
     * Sends a List of messages
     * @param message List of Strings to send as messages to sender
     * @param sender The entity to send the messages to
     */
    public void sendMessage(List<String> message, CommandSender sender) {
        for (int i = 0; i < message.size(); i++) {
            sender.sendMessage(message.get(i));
        }
    }
}
