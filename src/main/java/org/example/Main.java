package org.example;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Main {

    private static JDA jda;
    private static final String token = "MTA3NzcxMzg4MDAzMTE4Njk5NA.GiCRlB.3R5IfoljH1rKtDZmCtwQy7om4p2-3CZ9NEOLhM";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm");

    public static void main(String[] args) {

        jda = JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new MessageManager(), new CommandManager(), new ButtonManager())
                .build();

        jda.getPresence().setActivity(Activity.playing("/commandlist"));

        try {

            jda.awaitReady();
            List<Guild> guilds = jda.getGuilds();

            for (Guild guild: guilds) {

                new HistoryRecorder(guild);

            }

        } catch (InterruptedException e) {

            System.out.println("'jda.awaitReady()' threw an InterruptedException");

        } catch (IOException e) {

            System.out.println("HistoryRecorder could not be created.");

        }

    }

    public static class Access {

        public static JDA getJda() {

            return jda;

        }

        public static DateTimeFormatter getFormatter() {

            return formatter;

        }

    }

}