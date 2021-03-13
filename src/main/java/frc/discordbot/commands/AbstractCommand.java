package frc.discordbot.commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public abstract class AbstractCommand {
    public abstract void run(MessageReceivedEvent message);

    public abstract String getCommand();

    public abstract String getAliases();
}
