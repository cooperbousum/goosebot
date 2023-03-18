package org.example;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CommandManager extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        HistoryRecorder messageRecorder = HistoryRecorder.Access.getHistoryRecorder(event.getGuild());

        if (event.getName().equalsIgnoreCase("commandlist")) {

            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle("goose-bot! Commands List")
                    .addField("/honk", "'/honk' is used in conjunction with an integer, which is placed after the initial command, separated with a space.", true)
                    .addField("/getHistory", "'/getHistory' is used to retrieve the message history of the server. The most recent entries will be previewed, and older entries will be available through an attached text file.", true);

            MessageCreateBuilder messageBuilder = new MessageCreateBuilder()
                    .addEmbeds(embedBuilder.build())
                    .setActionRow(ButtonManager.getViewCode());

            event.reply(messageBuilder.build()).queue();

        }

        if (event.getName().equalsIgnoreCase("rules")) {

            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle("goose-bot! Commands List")
                    .addField("1", "Be kind", false)
                    .addField("2", "Keep the conversation focused", false)
                    .addField("3", "Suggestions and ideas are always appreciated, but make sure to keep them appropriate (Joke features are always appreciated)", false);

            event.replyEmbeds(embedBuilder.build()).queue();

        }

        if (event.getName().equalsIgnoreCase("honk")) {

            OptionMapping optionTimes = event.getOption("times");
            StringBuilder output;

            if (optionTimes != null) {

                output = new StringBuilder();
                output.append("Honk!\n".repeat(Math.max(0, optionTimes.getAsInt())));

                if (output.length() > 2000) {

                    event.reply("Character limit (2000) reached: Maximum 333 honks.").setEphemeral(true).queue();

                } else {

                    event.reply(output.toString()).queue(interactionHook -> Main.writeSend(interactionHook.retrieveOriginal().complete()));

                }

            } else {

                event.reply("Honk!").queue(interactionHook -> Main.writeSend(interactionHook.retrieveOriginal().complete()));

            }

        }

        if (event.getName().equalsIgnoreCase("gethistory")) {

            System.out.println("getting history for: " + messageRecorder.getGuild().getId());

            Collection<ItemComponent> components = new ArrayList<>();

            components.add(ButtonManager.getExpandHistory());

            int startIndex = messageRecorder.getFileLength() - 5;
            int endIndex = messageRecorder.getFileLength();

            if (startIndex > 0) {

                components.add(ButtonManager.getNavigateHistoryUp());

            } else {

                components.add(ButtonManager.getNavigateHistoryUp().asDisabled());

            }

            if (endIndex < messageRecorder.getFileLength()) {

                components.add(ButtonManager.getNavigateHistoryDown());

            } else {

                components.add(ButtonManager.getNavigateHistoryDown().asDisabled());

            }

            MessageCreateBuilder messageBuilder = new MessageCreateBuilder()
                    .setEmbeds(messageRecorder.getHistoryEmbeds(true, startIndex, endIndex))
                    .setActionRow(components);

            event.reply(messageBuilder.build()).queue();

        }

    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {

        List<CommandData> guildCommands = new ArrayList<>();

        event.getGuild().updateCommands().addCommands(guildCommands).queue();

        guildCommands.add(Commands.slash("rules", "Returns an embed containing the server's rules"));

        if (event.getGuild().getId().equals("1077715044814557325")) {

            event.getGuild().updateCommands().addCommands(guildCommands).queue();

        }

        List<CommandData> globalCommands = new ArrayList<>();
        globalCommands.add(Commands.slash("honk", "Honk!").addOption(OptionType.INTEGER, "times", "The number of times to honk"));
        globalCommands.add(Commands.slash("commandlist", "Returns a list of commands available to use."));
        globalCommands.add(Commands.slash("gethistory", "Returns a file containing a complete message history with a preview."));
        //globalCommands.add(Commands.slash("connect", "Connect goosebot! to your current voice channel."));
        //globalCommands.add(Commands.slash("disconnect", "Disconnect goosebot! from your current voice channel"));
        //globalCommands.add(Commands.slash("playtrack", "Plays a track from Youtube").addOption(OptionType.STRING, "query", "The query to be used in a Youtube search"));
        //globalCommands.add(Commands.slash("stoptrack", "Stops the currently playing track"));

        Main.Access.getJda().updateCommands().addCommands(globalCommands).queue();

    }

}
