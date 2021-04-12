package frc.discordbot.commands;

import frc.robot.Robot;
import net.dv8tion.jda.core.JDA;

public class PlaySongCommand extends AbstractCommand {
    @Override
    public boolean isServerSideCommand() {
        return false;
    }

    @Override
    public AbstractCommandResponse run(AbstractCommandData message) {
        return new PlaySongCommandResponse(message, "Playing " + Robot.chirp.playSongMostNearlyMatching(message.CONTENT.replace("!play", "").replace("!p", "").toLowerCase().trim()));
    }

    @Override
    public String getCommand() {
        return "play";
    }

    @Override
    public String getAliases() {
        return "p";
    }

    public static class PlaySongCommandResponse extends AbstractCommandResponse {
        private final String RESPONSE;

        public PlaySongCommandResponse(AbstractCommandData originalData, String response) {
            super(originalData);
            RESPONSE = response;
        }

        @Override
        public void doYourWorst(JDA client) {
            client.getTextChannelById(CHANNEL_ID).sendMessage(RESPONSE).queue();
        }
    }
}
