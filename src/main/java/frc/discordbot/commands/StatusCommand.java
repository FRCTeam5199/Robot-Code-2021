package frc.discordbot.commands;

import frc.misc.ISubsystem;
import frc.misc.SubsystemStatus;
import frc.robot.Robot;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class StatusCommand extends AbstractCommand {
    @Override
    public void run(MessageReceivedEvent message) {
        StringBuilder statuses = new StringBuilder("```diff\n");
        for (ISubsystem system : Robot.subsystems)
            statuses.append(system.getSubsystemStatus() == SubsystemStatus.FAILED ? "- " : "+ ").append(system.getSubsystemName()).append(": ").append(system.getSubsystemStatus().name()).append('\n');
        statuses.append("```");
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Activated subsystem statuses:").setDescription(statuses).setAuthor("jojo2357");
        message.getChannel().sendMessage(builder.build()).submit();
    }

    @Override
    public String getCommand() {
        return "status";
    }

    @Override
    public String getAliases() {
        return "stat";
    }
}
