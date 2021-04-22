package frc.discordbot.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

public class Wait5TicksThenReplyCommand extends AbstractCommand {
    @Override
    public @Nullable AbstractCommandResponse run(AbstractCommandData message) {
        if (++((Wait5TicksThenReplyData) message).ticksWaited == 5)
            return new GenericCommandResponse(message, "Awaited and replied");
        return null;
    }

    @Override
    public boolean isMultiTickCommand() {
        return true;
    }

    @Override
    public String getCommand() {
        return "test";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"tst"};
    }

    @Override
    public AbstractCommandData extractData(MessageReceivedEvent message) {
        return new Wait5TicksThenReplyData(message);
    }

    public static class Wait5TicksThenReplyData extends AbstractCommandData {
        private int ticksWaited = 0;

        protected Wait5TicksThenReplyData(MessageReceivedEvent message) {
            super(message);
        }

    }
}
