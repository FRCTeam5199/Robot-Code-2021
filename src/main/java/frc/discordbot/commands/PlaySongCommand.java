package frc.discordbot.commands;

import frc.robot.Robot;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class PlaySongCommand extends AbstractCommand {
    @Override
    public void run(MessageReceivedEvent message) {
        message.getChannel().sendMessage("Playing " + Robot.chirp.playSongMostNearlyMatching(message.getMessage().getContentRaw().replace("!play", "").replace("!p", "").toLowerCase().trim())).queue();
    }

    @Override
    public String getCommand() {
        return "play";
    }

    @Override
    public String getAliases() {
        return "p";
    }
}
