package frc.discordbot.commands;

import frc.misc.Chirp;
import frc.robot.Robot;
import org.jetbrains.annotations.NotNull;

/**
 * Gets the music queue as managed by {@link PlaySongCommand} with pretty formatting courtesy of {@link
 * Chirp#getQueueAsString()}
 */
public class QueueCommand extends AbstractCommand {
    @Override
    public @NotNull AbstractCommandResponse run(AbstractCommandData message) {
        if (!Robot.robotSettings.ENABLE_MUSIC) {
            return new GenericCommandResponse(message, "Music is disabled you goon");
        }
        return new GenericCommandResponse(message, Robot.chirp.getQueueAsString());
    }

    @Override
    public String getCommand() {
        return "queue";
    }

    @Override
    public String sendHelp() {
        return "Returns the music queue held on the robot";
    }
}
