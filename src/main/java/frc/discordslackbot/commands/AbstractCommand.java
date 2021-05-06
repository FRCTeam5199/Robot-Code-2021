package frc.discordslackbot.commands;

import com.slack.api.bolt.App;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.model.event.MessageEvent;
import frc.discordslackbot.DiscordBot;
import frc.discordslackbot.MessageHandler;
import frc.misc.ServerSide;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.net.URLDecoder;
import java.nio.charset.Charset;

/**
 * The template for a command from a brobot
 */
public abstract class AbstractCommand implements Serializable {
    /**
     * Executes the command using the provided data
     *
     * @param message the message and relevant data that triggered the command to run
     * @return The result of executing the command. If the command would like to persist, it should return {@link
     * ContinuePersistingCommandResponse#PASS} or make a new one with a message to send to the bois in discord
     */
    //todo make this not nullable and simple make a special return
    @NotNull
    public abstract AbstractCommandResponse run(AbstractCommandData message);

    /**
     * The name of the command that the user will use to reference it
     *
     * @return The command name without the prefix
     */
    public abstract String getCommand();

    /**
     * Gets a little blurb for {@link HelpCommand} to use when a user asks how to use this command. Should also include
     * information about arguments, if any
     *
     * @return A lil help blurb, discord markdown supported
     */
    public abstract String sendHelp();

    /**
     * Different from {@link #run}, should only ever be redefined by multitick commands and should only contain initial
     * processing required before the command goes stale {@link MessageHandler#persistPendingCommands() while waiting to
     * persist}. Such reasons may include rejecting a command based on settings, acknowledging a command is valid,
     *
     * @param message the data from the message that caused the message
     * @return A response in accordance with the return of {@link #run(AbstractCommandData)}
     */
    public AbstractCommandResponse runOnServerArrival(AbstractCommandData message) {
        return ContinuePersistingCommandResponse.PASS;
    }

    /**
     * Do I even need to explain?
     *
     * @return True if the command should be persisted, false if it should be executed and returned immediately
     */
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
     * Every command is able to refine what they are sending on the server in order to save resources on the robot.
     * Also, wifi can be an issue so the less that gets sent, the better. By default, uses {@link GenericCommandData}
     *
     * @param message the message from {@link frc.discordslackbot.MessageHandler#onMessageReceived(MessageReceivedEvent)}
     * @return the required data extracted from the passed data wrapped in a {@link AbstractCommandData packet}
     */
    public AbstractCommandData extractData(MessageReceivedEvent message) {
        return new GenericCommandData(message);
    }

    public AbstractCommandData extractData(MessageEvent message) {
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
         * @param message message as received from {@link frc.discordslackbot.MessageHandler#onMessageReceived(MessageReceivedEvent)}
         */
        protected AbstractCommandData(MessageReceivedEvent message) {
            this(message.getMessage().getContentRaw(), message.getMessageId(), message.getAuthor().getId(), message.getGuild().getId(), message.getChannel().getId());
        }

        protected AbstractCommandData(MessageEvent message) {
            this(URLDecoder.decode(message.getText(), Charset.defaultCharset()), message.getClientMsgId(), message.getUser(), message.getTeam(), message.getChannel());
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

        protected AbstractCommandResponse(AbstractCommandData originalData) {
            CONTENT = originalData.CONTENT;
            MESSAGE_ID = originalData.MESSAGE_ID;
            AUTHOR_ID = originalData.AUTHOR_ID;
            GUILD_ID = originalData.GUILD_ID;
            CHANNEL_ID = originalData.CHANNEL_ID;
        }

        protected AbstractCommandResponse() {
            CONTENT = MESSAGE_ID = AUTHOR_ID = GUILD_ID = CHANNEL_ID = "";
        }

        /**
         * Callback when response is read on server. For example, if need to reply to the original command, or other
         * post-command server-side processing should be implemented in inheriting classes
         *
         * @param client {@link DiscordBot#getBotObject() the bot object}
         */
        @ServerSide
        public abstract void doYourWorst(JDA client);

        public abstract void doYourWorst(App client);

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

        @Override
        public void doYourWorst(App client) {
            if (response.length() > 0) {
                try {
                    client.client().chatPostMessage(ChatPostMessageRequest.builder().channel(CHANNEL_ID).text(response).build());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Nothing special, just an instantiatable {@link AbstractCommandData}
     */
    public static class GenericCommandData extends AbstractCommandData {
        public GenericCommandData(MessageReceivedEvent message) {
            super(message);
        }

        public GenericCommandData(MessageEvent message) {
            super(message);
        }
    }

    /**
     * This is halfway to being an enum, hence the private constructor and public static final field.
     */
    public static class ContinuePersistingCommandResponse extends AbstractCommandResponse {
        public static final ContinuePersistingCommandResponse PASS = new ContinuePersistingCommandResponse();
        private String responseMessage = "";

        private ContinuePersistingCommandResponse() {
            super();
        }

        /**
         * This is the constructor similar to {@link GenericCommandResponse} that has an optional callback
         *
         * @param message    Message data
         * @param blurbToSay What to respond with
         */
        public ContinuePersistingCommandResponse(AbstractCommandData message, String blurbToSay) {
            super(message);
            responseMessage = blurbToSay;
            if (blurbToSay.equals(""))
                throw new IllegalArgumentException("No I cant let you do that. Use PASS instead of a new one.");
        }

        @Override
        public void doYourWorst(JDA client) {
            if (!responseMessage.equals("")) {
                client.getTextChannelById(CHANNEL_ID).sendMessage(responseMessage).queue();
            }
        }

        @Override
        public void doYourWorst(App client) {
            if (!responseMessage.equals("")) {
                try {
                    client.client().chatPostMessage(ChatPostMessageRequest.builder().channel(CHANNEL_ID).text(responseMessage).build());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
