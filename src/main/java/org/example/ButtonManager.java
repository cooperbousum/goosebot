package org.example;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Scanner;

public class ButtonManager extends ListenerAdapter {

    private static final Button collapseHistory = Button.primary("collapseHistory", "Collapse");
    private static final Button expandHistory = Button.primary("expandHistory", "Expand");
    private static final Button navigateHistoryUp = Button.primary("navigateHistoryUp", Emoji.fromUnicode("U+2B06"));
    private static final Button navigateHistoryDown = Button.primary("navigateHistoryDown", Emoji.fromUnicode("U+2B07"));

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {

        HistoryRecorder messageRecorder = HistoryRecorder.Access.getHistoryRecorder(event.getGuild());
        Scanner indexScanner = new Scanner(Objects.requireNonNull(Objects.requireNonNull(event.getMessage().getEmbeds().get(0).getFooter()).getText()));
        int startIndex = indexScanner.nextInt();
        indexScanner.next();
        int endIndex = indexScanner.nextInt();
        indexScanner.close();

        if (event.getComponentId().equals("expandHistory") || event.getComponentId().equals("collapseHistory")) {

            Collection<ItemComponent> updatedComponents = new ArrayList<>();
            boolean collapsed;

            if (event.getComponentId().equals("expandHistory")) {

                collapsed = false;
                updatedComponents.add(collapseHistory);

            } else {

                collapsed = true;
                updatedComponents.add(expandHistory);

            }

            if (startIndex > 0) {

                updatedComponents.add(navigateHistoryUp);

            } else {

                updatedComponents.add(navigateHistoryUp.asDisabled());

            }

            if (endIndex < messageRecorder.getFileLength()) {

                updatedComponents.add(navigateHistoryDown);

            } else {

                updatedComponents.add(navigateHistoryDown.asDisabled());

            }

            MessageEditData messageEdit = new MessageEditBuilder()
                    .setEmbeds(messageRecorder.getHistoryEmbeds(collapsed, startIndex, endIndex))
                    .setActionRow(updatedComponents)
                    .build();

            event.editMessage(messageEdit).queue();


        }

        if (event.getComponentId().equals("navigateHistoryUp") || event.getComponentId().equals("navigateHistoryDown")) {

            Collection<ItemComponent> updatedComponents = new ArrayList<>();
            boolean collapsed;

            if (event.getComponentId().equals("navigateHistoryUp")) {

                startIndex--;
                endIndex--;

            } else if (event.getComponentId().equals("navigateHistoryDown")){

                startIndex++;
                endIndex++;

            }

            if (event.getMessage().getEmbeds().size() > 3) {

                collapsed = false;
                updatedComponents.add(collapseHistory);

            } else {

                collapsed = true;
                updatedComponents.add(expandHistory);

            }

            if (startIndex > 0) {

                updatedComponents.add(navigateHistoryUp);

            } else {

                updatedComponents.add(navigateHistoryUp.asDisabled());

            }

            if (endIndex < messageRecorder.getFileLength()) {

                updatedComponents.add(navigateHistoryDown);

            } else {

                updatedComponents.add(navigateHistoryDown.asDisabled());

            }

            MessageEditData messageEdit = new MessageEditBuilder()
                    .setEmbeds(messageRecorder.getHistoryEmbeds(collapsed, startIndex, endIndex))
                    .setActionRow(updatedComponents)
                    .build();

            event.editMessage(messageEdit).queue();

        }

    }

    public static Button getCollapseHistory() {

        return collapseHistory;

    }

    public static Button getExpandHistory() {

        return expandHistory;

    }

    public static Button getNavigateHistoryUp() {

        return navigateHistoryUp;

    }

    public static Button getNavigateHistoryDown() {

        return navigateHistoryDown;

    }

}
