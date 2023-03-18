package org.example;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Main {

    private static JDA jda;
    private static final String token = "Token";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm");
    private static File sentMessages;

    public static void main(String[] args) {

        jda = JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new MessageManager(), new CommandManager(), new ButtonManager())
                .build();

        jda.getPresence().setActivity(Activity.playing("/commandlist"));

        try {

            jda.awaitReady();
            System.out.println("JDA instance ready...");
            List<Guild> guilds = jda.getGuilds();

            for (Guild guild: guilds) {

                new HistoryRecorder(guild);

            }

        } catch (InterruptedException e) {

            System.out.println("'jda.awaitReady()' threw an InterruptedException");

        } catch (IOException e) {

            System.out.println("HistoryRecorder could not be created.");

        }

        sentMessages = new File("sentMessages.txt");

        try {

            if (sentMessages.createNewFile()) {

                System.out.println("Sent Message file created.");

            } else {

                System.out.println("Sent Message file already created.");

            }

        } catch (IOException e) {

            System.out.println("There was a problem creating the sent message file.");

        }

    }

    public static void writeSend(Message message) {

        try (FileWriter fileWriter = new FileWriter(sentMessages, true)) {

            if (message.getEmbeds().isEmpty()) {

                fileWriter.write(message.getGuild().getId() + " " + message.getChannel().getId() + " " + message.getId() + " " + message.getContentDisplay() + "\n");

            }

        } catch (IOException e) {

            System.out.println("There was an issue creating the fileWriter object for the sentMessages file.");

        }

    }

    public static class Access {

        public static JDA getJda() {

            return jda;

        }

        public static DateTimeFormatter getFormatter() {

            return formatter;

        }

        public static File getSent() {

            return sentMessages;

        }

    }

}
