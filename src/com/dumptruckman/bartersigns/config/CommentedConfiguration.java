package com.dumptruckman.bartersigns.config;

import com.dumptruckman.bartersigns.util.FileMgmt;
import org.bukkit.util.config.Configuration;
import org.yaml.snakeyaml.events.SequenceStartEvent;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author dumptruckman
 */
public class CommentedConfiguration extends Configuration {

    private HashMap<String, String> comments;
    private File file;

    public CommentedConfiguration(File file) {
        super(file);
        comments = new HashMap<String, String>();
        this.file = file;
    }

    @Override
    public boolean save() {
        boolean saved = super.save();

        if (!comments.isEmpty() && saved) {
            String[] yamlContents =
                    FileMgmt.convertFileToString(file).split("[" + System.getProperty("line.separator") + "]");

            String newContents = "";
            boolean commentedPath = false;
            String currentPath = "";
            int depth = 0;
            for (String line : yamlContents) {
                if (line.contains(": ") || line.charAt(line.length() - 1) == ':') {
                    int index = 0;
                    index = line.indexOf(": ");
                    if (index < 0) {
                        index = line.length() - 1;
                    }
                    if (currentPath.isEmpty()) {
                        currentPath = line.substring(0, index);
                    } else {
                        int whiteSpace = 0;
                        for (int n = 0; n < line.length(); n++) {
                            if (line.charAt(n) == ' ') {
                                whiteSpace++;
                            } else {
                                break;
                            }
                        }
                        if (whiteSpace / 4 > depth) {
                            // Path is deeper
                            currentPath += "." + line.substring(whiteSpace, index);
                            depth++;
                            commentedPath = false;
                        } else if (whiteSpace / 4 < depth) {
                            // Path is shallower
                            int newDepth = whiteSpace / 4;
                            for (int i = 0; i < depth - newDepth; i++) {
                                currentPath = currentPath.replace(currentPath.substring(currentPath.lastIndexOf(".")), "");
                            }
                            int lastIndex = currentPath.lastIndexOf(".");
                            if (lastIndex < 0) {
                                currentPath = "";
                            } else {
                                currentPath = currentPath.replace(currentPath.substring(currentPath.lastIndexOf(".")), "");
                                currentPath += ".";
                            }
                            currentPath += line.substring(whiteSpace, index);
                            depth = newDepth;
                            commentedPath = false;
                        } else {
                            // Path is same depth
                            currentPath = currentPath.replace(currentPath.substring(currentPath.lastIndexOf(".")), "");
                            currentPath += "." + line.substring(whiteSpace, index);
                            commentedPath = false;
                        }
                    }
                }
                String comment = "";
                if (!commentedPath) {
                    comment = comments.get(currentPath);
                    commentedPath = true;
                }
                if (comment != null) {
                    line = comment + System.getProperty("line.separator") + line;
                }
                newContents += line + System.getProperty("line.separator");
            }
            try {
                FileMgmt.stringToFile(newContents, file);
            } catch (IOException e) {
                saved = false;
            }
        }

        return saved;
    }

    public void addComment(String path, String...commentLines) {
        StringBuilder commentstring = new StringBuilder();
        String leadingSpaces = "";
        for (int n = 0; n < path.length(); n++) {
            if (path.charAt(n) == '.') {
                leadingSpaces += "    ";
            }
        }
        for (String line : commentLines) {
            line = leadingSpaces + "# " + line;
            if (commentstring.length() > 0) {
                commentstring.append("\r\n");
            }
            commentstring.append(line);
        }
        comments.put(path, commentstring.toString());
    }
}
