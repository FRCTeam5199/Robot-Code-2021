package frc.discordbot.commands;

import frc.robot.Robot;
import net.dv8tion.jda.core.JDA;

/**
 * Uses {@link Robot#chirp} to play the song of the user's choosing
 */
public class PlaySongCommand extends AbstractCommand {
    @Override
    public AbstractCommandResponse run(AbstractCommandData message) {
        if (Robot.robotSettings.ENABLE_MUSIC) {
            return new PlaySongCommandResponse(message, "Playing " + Robot.chirp.playSongMostNearlyMatching(message.CONTENT.replace("!play", "").replace("!p", "").toLowerCase().trim()));
        } else {
            return new PlaySongCommandResponse(message, "Music is disabled in robotSettings");
        }
    }

    @Override
    public String getCommand() {
        return "play";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"p"};
    }

    /**
     * Special response that contains just a raw text to reply with
     */
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
