package org.example;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.io.*;
import java.util.*;

public class HistoryRecorder {

    private static final Map<Guild, HistoryRecorder> historyRecorderMap = new HashMap<>();
    private File historyFile;
    private Guild guild;

    public HistoryRecorder(Guild guild) throws IOException {

        if (!historyRecorderMap.containsKey(guild)) {

            this.guild = guild;
            historyRecorderMap.put(guild, this);
            historyFile = new File(guild.getId() + ".txt");

            if (historyFile.createNewFile()) {

                System.out.println("File created for server: " + guild.getName());

            } else {

                System.out.println("History file already created for server: " + guild.getName());

            }

        } else {

            System.out.println("HistoryRecorder already created for server: " + guild.getName());

        }

    }

    public void writeLine(String line) {

        try (FileWriter fileWriter = new FileWriter(this.historyFile, true)) {

            BufferedReader bufferedReader = new BufferedReader(new FileReader(this.historyFile));
            int fileLength = 0;

            while (bufferedReader.readLine() != null) {

                fileLength++;

            }

            bufferedReader = new BufferedReader(new FileReader(this.historyFile));

            for (int i = 0; i < fileLength - 1; i++) {

                bufferedReader.readLine();

            }

            try {

                Scanner previousLineScanner = new Scanner(bufferedReader.readLine());
                String string = previousLineScanner.next();

                if (string.equals(line.split(" ")[0])) {

                    RandomAccessFile randomAccessFile = new RandomAccessFile(this.historyFile, "rw");

                    randomAccessFile.seek(randomAccessFile.length() - 1);
                    randomAccessFile.write(0);
                    randomAccessFile.close();

                    fileWriter.write("\u0003" + line + "\n");

                } else {

                    fileWriter.write(line + "\n");

                }

                System.out.println("Line written: " + line);

            } catch (NullPointerException e) {

                System.out.println("No previous user in history; continuing.");
                fileWriter.write(line + "\n");

            } catch (NoSuchElementException e) {

                System.out.println("NoSuchElementException");

            }

        } catch (IOException e) {

            System.out.println("'writeLine()': the history file could not be found for server: " + guild.getName());

        }


    }

    public List<MessageEmbed> getHistoryEmbeds (boolean collapsed, int startIndex, int endIndex) {

        List<MessageEmbed> historyEmbeds = new ArrayList<>();

        MessageEmbed header = new EmbedBuilder()
                .setTitle("History Preview for **" + this.guild.getName() + "**")
                .setFooter((startIndex) + " : " + (endIndex))
                .build();

        MessageEmbed spacer = new EmbedBuilder()
                .setFooter(". . . ")
                .build();

        try {

            Scanner fileScanner = new Scanner(historyFile);

            for (int i = 0; i < startIndex; i++) {

                fileScanner.nextLine();

            }

            String line = fileScanner.nextLine();

            for (int i = startIndex; i < endIndex; i++) {

                line = line.replace("\u0001", "\n");

                String[] splitLine = line.split(" ", 5);
                User messageAuthor = Main.Access.getJda().retrieveUserById(splitLine[0]).complete();
                String[] messages = line.split("\u0003");
                int messageIndex = 0;

                String avatarUrl = messageAuthor.getAvatarUrl();

                if (avatarUrl == null) {

                    avatarUrl = messageAuthor.getDefaultAvatarUrl();

                }

                EmbedBuilder embedBuilder = new EmbedBuilder()
                        .setFooter(messageAuthor.getName(), avatarUrl);

                for (String message: messages) {

                    message = message.replace("\u0003", "");
                    String[] messageSplit = message.split(" ", 5);

                    embedBuilder.addField(messageSplit[2] + " " + messageSplit[3] + " UTC", messageSplit[4], false);
                    messageIndex++;

                }

                historyEmbeds.add(embedBuilder.build());

                if (fileScanner.hasNextLine()) {

                    line = fileScanner.nextLine();

                } else {

                    break;

                }

            }

            fileScanner.close();

        } catch (IOException e) {

            System.out.println("'getHistoryEmbeds()': the history file could not be found for server: " + guild.getName());

        } catch (NoSuchElementException e) {

            System.out.println("'getHistoryEmbeds()': The history file was empty.");

        }

        if (collapsed) {

            List<MessageEmbed> collapsedEmbeds = new ArrayList<>();

            collapsedEmbeds.add(header);
            collapsedEmbeds.add(spacer);
            collapsedEmbeds.add(historyEmbeds.get(historyEmbeds.size() - 1));

            return collapsedEmbeds;

        } else {

            List<MessageEmbed> expandedEmbeds = new ArrayList<>();

            expandedEmbeds.add(header);
            expandedEmbeds.addAll(historyEmbeds);

            return expandedEmbeds;

        }

    }

    public Guild getGuild() {

        return guild;

    }

    public int getFileLength() {

        int fileLength;

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(this.historyFile))) {

            fileLength = 0;

            while (bufferedReader.readLine() != null) {

                fileLength++;

            }

        } catch (IOException e) {

            System.out.println("fileLength could not be found; file not available.");
            return -1;

        }

        return fileLength;

    }

    public class Access {

        public static Map<Guild, HistoryRecorder> getHistoryRecorderMap() {

            return historyRecorderMap;

        }

        public static HistoryRecorder getHistoryRecorder(Guild guild) {

            return historyRecorderMap.get(guild);

        }

        public File getHistoryFile() {

            return historyFile;

        }

    }

}
