package frc.discordbot.commands;

import frc.misc.ServerSide;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

@ServerSide
public class VibingCommand extends AbstractCommand {
    @Override
    public @Nullable AbstractCommandResponse run(AbstractCommandData message) {
        return new VibingCommandResponse(message);
    }

    @Override
    public String getCommand() {
        return "vibetime";
    }

    @Override
    public boolean isServerSideCommand() {
        return true;
    }

    public static class VibingCommandResponse extends AbstractCommandResponse {
        public VibingCommandResponse(AbstractCommandData data) {
            super(data);
        }

        @Override
        public void doYourWorst(JDA client) {
            client.getTextChannelById(CHANNEL_ID).sendMessage("I am simply vibing").queue();
            try {
                Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start https://www.youtube.com/watch?v=bxqLsrlakK8"});
            } catch (IOException e) {
                System.out.println("Exception: " + e);
            }
        }
    }
}
