package frc.discordbot.commands;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

public abstract class AbstractCommand implements Serializable {
    public static boolean DRIVEN;

    /**
     * Executes the command using the provided data
     *
     * @param message the message and relevant data that triggered the command to run
     * @return The result of executing the command. Should only return null if commnad {@link
     * AbstractCommandData#isMultiTickCommand() is multi tick}
     */
    public abstract @Nullable AbstractCommandResponse run(AbstractCommandData message);

    /**
     * The name of the command that the user will use to reference it
     *
     * @return The command name without the prefix
     */
    public abstract String getCommand();

    /**
     * Returns secondary names that will be accepted if the user calls this command
     *
     * @return array of nicknames
     */
    public String[] getAliases() {
        return new String[0];
    }

    /**
     * Simple enough, hardcoded because shouldnt be changing during runtime
     *
     * @return if the command should be run on the server instead of the client
     */
    public boolean isServerSideCommand() {
        return false;
    }

    /**
     * Every command is able to refine what they are sending on the server in order to save resouces on the robot. Also,
     * wifi can be an issue so the less that gets sent, the better. By default, uses {@link GenericCommandData}
     *
     * @param message the message from {@link frc.discordbot.MessageHandler#onMessageReceived(MessageReceivedEvent)}
     * @return the required data extracted from the passed data wrapped in a {@link AbstractCommandData packet}
     */
    public AbstractCommandData extractData(MessageReceivedEvent message) {
        return new GenericCommandData(message);
    }

    /**
     * Very important. Holds the base information that a command would need to run when back on the client (for output).
     * Computations that require more than the basic stored here should be done on the server or a new class made to
     * suit the needs of the situation
     */
    public static abstract class AbstractCommandData implements Serializable {
        public final String CONTENT;
        public final String MESSAGE_ID, AUTHOR_ID, GUILD_ID, CHANNEL_ID;

        public abstract boolean isMultiTickCommand();

        protected AbstractCommandData(MessageReceivedEvent message) {
            this(message.getMessage().getContentRaw(), message.getMessageId(), message.getAuthor().getId(), message.getGuild().getId(), message.getChannel().getId());
        }

        private AbstractCommandData(String content, String message_id, String author_id, String guild_id, String channel_id) {
            CONTENT = content;
            MESSAGE_ID = message_id;
            AUTHOR_ID = author_id;
            GUILD_ID = guild_id;
            CHANNEL_ID = channel_id;
        }
    }

    /**
     * Very important. Holds the information that the server need to execute according to the {@link #doYourWorst(JDA)
     * callback}. For generic commands that dont need anythin, use {@link GenericCommandResponse}
     */
    public static abstract class AbstractCommandResponse implements Serializable {
        public final String CONTENT;
        public final String MESSAGE_ID, AUTHOR_ID, GUILD_ID, CHANNEL_ID;

        public abstract void doYourWorst(JDA client);

        protected AbstractCommandResponse(AbstractCommandData originalData) {
            CONTENT = originalData.CONTENT;
            MESSAGE_ID = originalData.MESSAGE_ID;
            AUTHOR_ID = originalData.AUTHOR_ID;
            GUILD_ID = originalData.GUILD_ID;
            CHANNEL_ID = originalData.CHANNEL_ID;
        }
    }

    /**
     * Nothing special, just an instantiatable {@link AbstractCommandResponse} with no callback
     */
    public static class GenericCommandResponse extends AbstractCommandResponse {
        public GenericCommandResponse(AbstractCommandData message) {
            super(message);
        }

        @Override
        public void doYourWorst(JDA client) {
            //pass
        }
    }

    /**
     * Nothing special, just an instantiatable {@link AbstractCommandData}
     */
    public static class GenericCommandData extends AbstractCommandData {
        public GenericCommandData(MessageReceivedEvent message) {
            super(message);
        }

        @Override
        public boolean isMultiTickCommand() {
            return false;
        }
    }
}
