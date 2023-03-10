package org.example;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Scanner;

public class MessageManager extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        HistoryRecorder messageRecorder = HistoryRecorder.Access.getHistoryRecorder(event.getGuild());

        if (event.getAuthor().isBot()) {

            if (!event.getAuthor().getId().equals("1079547089261973514")) {

                return;

            }

        }

        String messageContent = event.getMessage().getContentDisplay().replace("/n", " ");

        if (!event.getMessage().getAttachments().isEmpty()) {

            StringBuilder urlOutput = new StringBuilder();

            for (Message.Attachment attachment: event.getMessage().getAttachments()) {

                urlOutput.append(" ").append(attachment.getUrl());

            }

            assert messageRecorder != null;
            messageRecorder
                    .writeLine(event.getMessage().getAuthor().getId()
                            + " at: " + event.getMessage().getTimeCreated().format(Main.Access.getFormatter())
                            + " " + messageContent + urlOutput
                    );

        } else {

            messageRecorder
                    .writeLine(event.getMessage().getAuthor().getId()
                            + " at: " + event.getMessage().getTimeCreated().format(Main.Access.getFormatter())
                            + " " + messageContent
                    );

        }

        String[] contains = {"im ", " im ", "i'm ", " i'm "};
        Scanner imScanner = null;

        for (String string: contains) {

            if (messageContent.toLowerCase().contains(string)) {

                imScanner = new Scanner(messageContent.substring(messageContent.toLowerCase().indexOf(string) + string.length()));
                event.getMessage().reply("Hi " + imScanner.next() + ", I'm goosebot!").queue();
                break;

            }

        }

    }

}
