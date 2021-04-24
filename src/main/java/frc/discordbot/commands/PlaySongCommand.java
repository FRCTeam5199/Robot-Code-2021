package frc.discordbot.commands;

import edu.wpi.first.wpilibj.DriverStation;
import frc.misc.Chirp;
import frc.robot.Robot;

/**
 * Uses {@link Robot#chirp} to play the song of the user's choosing
 */
public class PlaySongCommand extends AbstractCommand {
    @Override
    public AbstractCommandResponse run(AbstractCommandData message) {
        if (!message.CONTENT.contains(" ")) {
            return new GenericCommandResponse(message, Chirp.getAllSongs());
        } else if (DriverStation.getInstance().isDisabled()) {
            return new GenericCommandResponse(message, "Enable drive station to play music");
        } else if (Robot.robotSettings.ENABLE_MUSIC) {
            return new GenericCommandResponse(message, "Playing " + Robot.chirp.playSongMostNearlyMatching(message.CONTENT.replace("!play", "").replace("!p", "").toLowerCase().trim()));
        } else {
            return new GenericCommandResponse(message, "Music is disabled in robotSettings");
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
}
