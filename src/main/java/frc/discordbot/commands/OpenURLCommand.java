package frc.discordbot.commands;

import frc.misc.ServerSide;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Opens a URL on the server. Useful for fun, sharing docs, but can be used for evil. Please dont.
 */
@ServerSide
public class OpenURLCommand extends AbstractCommand {
    @Override
    public @NotNull AbstractCommandResponse run(AbstractCommandData message) {
        return new VibingCommandResponse(message);
    }

    @Override
    public String getCommand() {
        return "url";
    }

    @Override
    public String sendHelp() {
        return "Opens a provided url on the drive station. Good for sharing documentation or videos on the main computer.";
    }

    public String getArgs() {
        return "<url to open>";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"rickroll", "troll", "vibetime"};
    }

    @Override
    public boolean isServerSideCommand() {
        return true;
    }

    /**
     * Holds a url and then opens it when being processed
     */
    public static class VibingCommandResponse extends AbstractCommandResponse {
        private final String url;

        public VibingCommandResponse(AbstractCommandData data) {
            super(data);
            url = CONTENT.split(" ").length > 1 ? CONTENT.split(" ")[1] : "https://www.youtube.com/watch?v=bxqLsrlakK8";
        }

        @Override
        public void doYourWorst(JDA client) {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                client.getTextChannelById(CHANNEL_ID).sendMessage("Invalid url <" + url + ">").queue();
            } else {
                client.getTextChannelById(CHANNEL_ID).sendMessage("I am simply vibing. Opened url <" + url + ">").queue();
                try {
                    Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start " + url});
                } catch (IOException e) {
                    System.out.println("Exception: " + e);
                }
            }
        }
    }
}
