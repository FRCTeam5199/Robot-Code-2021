package frc.discordslackbot;

import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.bolt.socket_mode.SocketModeApp;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.model.event.AppMentionEvent;
import com.slack.api.model.event.MessageEvent;
import frc.misc.ServerSide;
import frc.misc.UtilFunctions;
import frc.robot.robotconfigs.DefaultConfig;

import java.util.regex.Pattern;

public class SlackBot {
    private static SlackBot bot = null;
    private final App botObject;

    private SlackBot(boolean listener) {
        MessageHandler.loadCommands(listener);
        App holder = null;
        if (!listener) {
            try {
                holder = new App(AppConfig.builder().singleTeamBotToken(DefaultConfig.SLACKBOTKEY).scope("message").build());


                holder.event(MessageEvent.class, (req, ctx) -> {
                    System.out.println("[AAAAAAA] I see a message: " + req.getEvent().getText());
                    MessageHandler.onMessageReceived(req.getEvent());
                    return ctx.ack();
                });

                SocketModeApp socketModeApp = new SocketModeApp(DefaultConfig.SLACKSOCKETKEY, holder);
                socketModeApp.startAsync();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        botObject = holder;
    }

    public static void newInstance(boolean listening) {
        UtilFunctions.detectedInternet();
        if (bot == null) {
            bot = new SlackBot(listening);
        }
    }

    @ServerSide
    public static App getBotObject() {
        return bot.botObject;
    }

    public static void sendSlackMessage(String CHANNEL_ID, String response) {
        System.out.println("Sending message '" + response + "' to " + CHANNEL_ID);
        try {
            //bot.botObject.client().chatPostMessage(ChatPostMessageRequest.builder().channel(CHANNEL_ID).text(response).build());
            bot.botObject.slack().methods().chatPostMessage(ChatPostMessageRequest.builder().channel(CHANNEL_ID).text(response).token(DefaultConfig.SLACKBOTKEY).build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}