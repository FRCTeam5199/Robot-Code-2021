package frc.discordslackbot.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Simple clientside multitick command to test connection and persistance.
 */
public class Wait5TicksThenReplyCommand extends AbstractCommand {
    @Override
    public @NotNull AbstractCommandResponse run(AbstractCommandData message) {
        if (++((Wait5TicksThenReplyData) message).ticksWaited == 5)
            return new GenericCommandResponse(message, "Awaited and replied");
        return ContinuePersistingCommandResponse.PASS;
    }

    @Override
    public String getCommand() {
        return "test";
    }

    @Override
    public String sendHelp() {
        return "Mostly used to test server-client connection.";
    }

    @Override
    public boolean isMultiTickCommand() {
        return true;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"tst"};
    }

    @Override
    public AbstractCommandData extractData(MessageReceivedEvent message) {
        return new Wait5TicksThenReplyData(message);
    }

    /**
     * Need to store {@link #ticksWaited} clientside, but the server doesnt care so we dont send it
     */
    public static class Wait5TicksThenReplyData extends AbstractCommandData {
        private transient int ticksWaited = 0;

        protected Wait5TicksThenReplyData(MessageReceivedEvent message) {
            super(message);
        }

    }
}
