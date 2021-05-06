import com.google.gson.Gson;
import com.slack.api.app_backend.slash_commands.payload.SlashCommandPayload;
import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.bolt.socket_mode.SocketModeApp;

public class ServerMain {

    public static void main(String[] args) throws Exception {
        System.out.println("Ayo waiting for messages.");
        final String botToken = "";
        final String socketToken = "";
        final String slackbotChannelID = "";
        var app = new App(AppConfig.builder().singleTeamBotToken(botToken).build());
        app.command("/help", (req, ctx) -> {
            SlashCommandPayload payload = req.getPayload();
            if (payload.getChannelId().equals(slackbotChannelID)) {
                System.out.println("Recieved help command from user " + payload.getUserId());
                slackCommandData commandData = new slackCommandData(payload.getUserName(), payload.getCommand(), payload.getChannelId());
                String json = new Gson().toJson(commandData);
                System.out.println(json);
                return ctx.ack("Nice.");
            } else {
                return ctx.ack();
            }
        });
        SocketModeApp socketModeApp = new SocketModeApp(socketToken, app);
        socketModeApp.start();
    }
}

class slackCommandData {
    String username;
    String command;
    String channelID;

    public slackCommandData(String username, String command, String channelID) {
        this.username = username;
        this.command = command;
        this.channelID = channelID;
    }
}