package frc.discordslackbot.commands;

import frc.robot.Robot;
import org.jetbrains.annotations.NotNull;

/**
 * Skips the currently playing remote requested song. If the robot is simply vibing then this will do nothing.
 */
public class SkipSongCommand extends AbstractCommand {
    @Override
    public @NotNull AbstractCommandResponse run(AbstractCommandData message) {
        if (!Robot.robotSettings.ENABLE_MUSIC) {
            return new GenericCommandResponse(message, "Enable music in order to dj");
        }
        if (Robot.chirp.getQueueLength() > 0) {
            String out = "Skipped " + Robot.lastFoundSong.split("_")[0];
            Robot.chirp.skipSong();
            return new GenericCommandResponse(message, out);
        }
        return new GenericCommandResponse(message, "Nothing skippable is playing right now");
    }

    @Override
    public String getCommand() {
        return "skip";
    }

    @Override
    public String sendHelp() {
        return "skips the next song";
    }
}
