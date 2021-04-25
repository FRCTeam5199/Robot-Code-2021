package frc.discordbot.commands;

import frc.robot.Robot;
import org.jetbrains.annotations.Nullable;

public class QueueCommand extends AbstractCommand{
    @Override
    public @Nullable AbstractCommandResponse run(AbstractCommandData message) {
        if (!Robot.robotSettings.ENABLE_MUSIC){
            return new GenericCommandResponse(message, "Music is disabled you goon");
        }
        return new GenericCommandResponse(message, Robot.chirp.getQueue());
    }

    @Override
    public String getCommand() {
        return "queue";
    }
}
