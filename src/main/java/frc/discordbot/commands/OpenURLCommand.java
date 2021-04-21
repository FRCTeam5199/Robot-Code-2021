package frc.discordbot.commands;

import frc.misc.ServerSide;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

@ServerSide
public class OpenURLCommand extends AbstractCommand {
    @Override
    public @Nullable AbstractCommandResponse run(AbstractCommandData message) {
        return new VibingCommandResponse(message);
    }

    @Override
    public String getCommand() {
        return "url";
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

    public static class VibingCommandResponse extends AbstractCommandResponse {
        private final String url;
        public VibingCommandResponse(AbstractCommandData data) {
            super(data);
            url = CONTENT.split(" ").length > 1 ? CONTENT.split(" ")[1] : "https://www.youtube.com/watch?v=bxqLsrlakK8";
        }

        @Override
        public void doYourWorst(JDA client) {
            client.getTextChannelById(CHANNEL_ID).sendMessage("I am simply vibing. Opened url <" + url + ">").queue();
            try {
                Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start " + url});
            } catch (IOException e) {
                System.out.println("Exception: " + e);
            }
        }
    }
}
