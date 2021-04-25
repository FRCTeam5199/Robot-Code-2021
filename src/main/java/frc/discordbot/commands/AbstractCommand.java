package frc.discordbot.commands;

import frc.discordbot.DiscordBot;
import frc.misc.ServerSide;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/**
 * The template for a command driven by
 */
public abstract class AbstractCommand implements Serializable {
    public static boolean DRIVEN;

    /**
     * Executes the command using the provided data
     *
     * @param message the message and relevant data that triggered the command to run
     * @return The result of executing the command. Should only return null if commnad {@link #isMultiTickCommand() is
     * multi tick}
     */
    //todo make this not nullable and simple make a special return
    public abstract @Nullable AbstractCommandResponse run(AbstractCommandData message);

    /**
     * The name of the command that the user will use to reference it
     *
     * @return The command name without the prefix
     */
    public abstract String getCommand();

    public abstract String sendHelp();

    public boolean isMultiTickCommand() {
        return false;
    }

    /**
     * This returns a textual representation of the arguments.
     *
     * @return a textual representation of the arguments.
     */
    public String getArgs() {
        return "";
    }

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
    public static abstract class AbstractCommandData implements Serializable, Comparable<AbstractCommandData> {
        public final String CONTENT;
        public final String MESSAGE_ID, AUTHOR_ID, GUILD_ID, CHANNEL_ID;

        /**
         * Extracts {@link #CONTENT}, {@link #MESSAGE_ID}, {@link #AUTHOR_ID}, {@link #GUILD_ID}, {@link #CHANNEL_ID}
         *
         * @param message message as recieved from {@link frc.discordbot.MessageHandler#onMessageReceived(MessageReceivedEvent)}
         */
        protected AbstractCommandData(MessageReceivedEvent message) {
            this(message.getMessage().getContentRaw(), message.getMessageId(), message.getAuthor().getId(), message.getGuild().getId(), message.getChannel().getId());
        }

        /**
         * Universal constructor, dont make public
         *
         * @param content    the raw text in the message
         * @param message_id the unique id of the message that will allow the server to find the message on return
         * @param author_id  the unique id of the author that will allow the server to find the author on return
         * @param guild_id   the unique id of the guild that will allow the server to find the guild on return
         * @param channel_id the unique id of the channel that will allow the server to find the channel on return
         */
        private AbstractCommandData(String content, String message_id, String author_id, String guild_id, String channel_id) {
            CONTENT = content;
            MESSAGE_ID = message_id;
            AUTHOR_ID = author_id;
            GUILD_ID = guild_id;
            CHANNEL_ID = channel_id;
        }

        @Override
        public int compareTo(AbstractCommandData other) {
            return MESSAGE_ID.compareTo(other.MESSAGE_ID);
        }

        /**
         * Gets a nicely formatted way to print out this object
         *
         * @return Command name: message
         */
        public String toString() {
            return this.getClass().getSimpleName() + ": " + CONTENT;
        }
    }

    /**
     * Very important. Holds the information that the server need to execute according to the {@link #doYourWorst(JDA)
     * callback}. For generic commands that dont need anything, use {@link GenericCommandResponse}
     */
    public static abstract class AbstractCommandResponse implements Serializable, Comparable<AbstractCommandResponse> {
        public final String CONTENT;
        public final String MESSAGE_ID, AUTHOR_ID, GUILD_ID, CHANNEL_ID;

        /**
         * Callback when response is read on server. For example, if need to reply to the original command, or other
         * post-command server-side processing should be implemented in inheriting classes
         *
         * @param client {@link DiscordBot#getBotObject() the bot object}
         */
        @ServerSide
        public abstract void doYourWorst(JDA client);

        protected AbstractCommandResponse(AbstractCommandData originalData) {
            CONTENT = originalData.CONTENT;
            MESSAGE_ID = originalData.MESSAGE_ID;
            AUTHOR_ID = originalData.AUTHOR_ID;
            GUILD_ID = originalData.GUILD_ID;
            CHANNEL_ID = originalData.CHANNEL_ID;
        }

        @Override
        public int compareTo(AbstractCommandResponse other) {
            return MESSAGE_ID.compareTo(other.MESSAGE_ID);
        }
    }

    /**
     * Nothing special, just an instantiatable {@link AbstractCommandResponse} with an optional callback. Passing no
     * {@link #response} will skip the callback
     */
    public static class GenericCommandResponse extends AbstractCommandResponse {
        private final String response;

        public GenericCommandResponse(AbstractCommandData message) {
            super(message);
            response = "";
        }

        public GenericCommandResponse(AbstractCommandData message, String responseText) {
            super(message);
            response = responseText;
        }

        /**
         * If {@link #response} is non-empty, then sends it as a message in reply to the original message
         *
         * @param client {@link DiscordBot#getBotObject()}
         */
        @Override
        public void doYourWorst(JDA client) {
            if (response.length() > 0)
                client.getTextChannelById(CHANNEL_ID).sendMessage(response).queue();
        }
    }

    /**
     * Nothing special, just an instantiatable {@link AbstractCommandData}
     */
    public static class GenericCommandData extends AbstractCommandData {
        public GenericCommandData(MessageReceivedEvent message) {
            super(message);
        }
    }
}
