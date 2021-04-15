package frc.discordbot.commands;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

public class Wait5TicksThenReplyCommand extends AbstractCommand{
    @Override
    public boolean isServerSideCommand() {
        return false;
    }

    @Override
    public @Nullable AbstractCommandResponse run(AbstractCommandData message) {
        if (++((Wait5TicksThenReplyData)message).ticksWaited == 5)
            return new Wait5TicksThenReplyResponse(message);
        return null;
    }

    @Override
    public String getCommand() {
        return "test";
    }

    @Override
    public String getAliases() {
        return "tst";
    }

    @Override
    public AbstractCommandData extractData(MessageReceivedEvent message){
        return new Wait5TicksThenReplyData(message);
    }

    public static class Wait5TicksThenReplyData extends AbstractCommandData{
        private int ticksWaited = 0;

        protected Wait5TicksThenReplyData(MessageReceivedEvent message) {
            super(message);
        }

        @Override
        public boolean isMultiTickCommand() {
            return true;
        }
    }

    public static class Wait5TicksThenReplyResponse extends AbstractCommandResponse{
        protected Wait5TicksThenReplyResponse(AbstractCommandData originalData) {
            super(originalData);
        }

        @Override
        public void doYourWorst(JDA client) {
            client.getTextChannelById(CHANNEL_ID).sendMessage("Awaited and replied").submit();
        }
    }
}
