package org.example;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

public class MessageManager extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        HistoryRecorder messageRecorder = HistoryRecorder.Access.getHistoryRecorder(event.getGuild());

        if (event.getAuthor().isBot()) {

            if (!event.getAuthor().getId().equals("1079547089261973514") || !event.getAuthor().getId().equals("1077713880031186994")) {

                return;

            }

        }

        String messageContent = event.getMessage().getContentDisplay().replace("\n", "\u0001");

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

        String[] contains = {" im ", " i'm "};
        Scanner imScanner = null;
        Scanner stringScanner;

        if (messageContent.toLowerCase().replace("’", "'").indexOf("im ") == 0|| messageContent.toLowerCase().replace("’", "'").indexOf("i'm ") == 0) {

            stringScanner = new Scanner(messageContent);
            stringScanner.next();
            event.getMessage().reply("Hi " + stringScanner.next() + ", I'm goosebot!").queue(Main::writeSend);
            stringScanner.close();

        } else {

            for (String string: contains) {

                if (messageContent.toLowerCase().replace("’", "'").contains(string)) {

                    imScanner = new Scanner(messageContent.substring(messageContent.toLowerCase().replace("’", "'").indexOf(string) + string.length()));
                    event.getMessage().reply("Hi " + imScanner.next() + ", I'm goosebot!").queue(Main::writeSend);
                    imScanner.close();
                    break;

                }

            }

        }

    }

    @Override
    public void onMessageDelete(@NotNull MessageDeleteEvent event) {

        try (Scanner fileScanner = new Scanner(Main.Access.getSent())) {

            String readLine;

            while (fileScanner.hasNextLine()) {

                readLine = fileScanner.nextLine();
                String[] splitLine = readLine.split(" ", 4);

                if (splitLine[2].equals(event.getMessageId())) {

                    Objects.requireNonNull(Objects.requireNonNull(Main.Access.getJda().getGuildById(splitLine[0])).getTextChannelById(splitLine[1])).sendMessage(splitLine[3]).queue(Main::writeSend);

                }

            }

        } catch (IOException e) {

            System.out.println("There was a issue creating the bufferedReader for the sentMessages file.");

        }

    }



}
