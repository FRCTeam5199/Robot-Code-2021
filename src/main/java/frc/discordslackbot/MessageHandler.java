package frc.discordslackbot;

import com.slack.api.model.event.MessageEvent;
import frc.discordslackbot.commands.*;
import frc.misc.ClientSide;
import frc.misc.ServerSide;
import frc.robot.Main;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * woooooooo handles messages! This class is cool because it is very two-faced. On the one hand, the member methods
 * which are inherited from {@link ListenerAdapter} work alongside {@link DiscordBot} to handle incoming server side
 * messages. The static methods defined here work with {@link frc.robot.ClientServerPipeline} to handle incoming
 * messages clientside.
 */
public class MessageHandler extends ListenerAdapter {
    public static final HashMap<String, AbstractCommand> commands = new HashMap<>();
    private static final HashMap<String, AbstractCommand> commandsAlias = new HashMap<>();
    private static final ArrayList<AbstractCommand.AbstractCommandData> pendingCommands = new ArrayList<>();
    public static MessageHandler messageHandler;
    private static boolean LISTENING;

    /**
     * Gets the current state of commands waiting to be executed
     *
     * @return true if there are pending commands
     */
    @ClientSide
    public static boolean canPersistCommands() {
        return pendingCommands.size() > 0;
    }

    /**
     * Creates commands and logs their names and aliases. Needed on both client and server side
     *
     * @param listening a debug var that should be the negation of {@link Main#IS_DS}
     */
    public static void loadCommands(boolean listening) {
        if (commands.keySet().size() > 0) return;
        LISTENING = listening;
        List<Class<? extends AbstractCommand>> classes = Arrays.asList(PlaySongCommand.class, PingCommand.class, StatusCommand.class, RoboPingCommand.class, Wait5TicksThenReplyCommand.class, DriveDistanceCommand.class, OpenURLCommand.class, HelpCommand.class, RandomQuoteCommand.class, ShutdownServerCommand.class, TurnCommand.class, CurrentTelemetryCommand.class, QueueCommand.class, ShootCommand.class);
        for (Class<? extends AbstractCommand> s : classes) {
            try {
                if (Modifier.isAbstract(s.getModifiers())) {
                    continue;
                }
                AbstractCommand c = s.getConstructor().newInstance();
                if (!commands.containsKey(c.getCommand())) {
                    commands.put(c.getCommand(), c);
                }
                for (String alias : c.getAliases()) {
                    if (!commandsAlias.containsKey(alias)) {
                        commandsAlias.put(alias, c);
                    }
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * A corrolary to {@link #onMessageReceived(MessageReceivedEvent)} that allows the client to process the data as if
     * it were the server.
     *
     * @param message the boiled-down data sent by the server
     */
    @ClientSide
    public static void onMessageReceived(AbstractCommand.AbstractCommandData message) {
        System.out.println("Recieved Message: " + message.CONTENT);
        if (message.CONTENT.charAt(0) == '!' && getCommand(message.CONTENT) != null) {
            if (LISTENING) {
                AbstractCommand command = getCommand(message.CONTENT);
                if (command != null) {
                    if (command.isMultiTickCommand()) {
                        AbstractCommand.AbstractCommandResponse resp = command.runOnServerArrival(message);
                        if (resp instanceof AbstractCommand.ContinuePersistingCommandResponse) {
                            pendingCommands.add(message);
                            if (resp != AbstractCommand.ContinuePersistingCommandResponse.PASS)
                                Main.pipeline.sendReply(resp);
                        } else
                            Main.pipeline.sendReply(resp);
                    } else {
                        AbstractCommand.AbstractCommandResponse response = command.run(message);
                        if (response instanceof AbstractCommand.ContinuePersistingCommandResponse && !command.isMultiTickCommand())
                            throw new IllegalStateException("Cannot run single tick command and get no response. Return an empty GenericCommand instead");
                        Main.pipeline.sendReply(response);
                    }
                }
            } else
                throw new IllegalStateException("How did you get here as a client?");
        }
    }

    /**
     * Searches through registered commands and gets the command matching incomming message
     *
     * @param message either the full incomming message or target string that a command should be extracted from
     * @return a command fitting the passed string, null if no matches
     */
    @Nullable
    public static AbstractCommand getCommand(String message) {
        message = message.replace("!", "").split(" ")[0];
        if (commands.containsKey(message))
            return commands.get(message);
        if (commandsAlias.containsKey(message))
            return commandsAlias.get(message);
        return null;
    }

    /**
     * Used on the robot to tick ongoing commands such as {@link Wait5TicksThenReplyCommand}
     */
    @ClientSide
    public static void persistPendingCommands() {
        if (pendingCommands.size() > 0) {
            AbstractCommand.AbstractCommandResponse result = commands.get(pendingCommands.get(0).CONTENT.substring(1).split(" ")[0]).run(pendingCommands.get(0));
            if (!(result instanceof AbstractCommand.ContinuePersistingCommandResponse)) {
                Main.pipeline.sendReply(result);
                pendingCommands.remove(0);
                persistPendingCommands();
            } else if (result != AbstractCommand.ContinuePersistingCommandResponse.PASS) {
                Main.pipeline.sendReply(result);
            }
        }
    }

    public static void onMessageRecieved(String mess) {
        System.out.println("Processing " + mess);
        if (!(mess.startsWith("robot") || mess.startsWith("brobot"))) {
            System.out.println("Blue banner? I did not hear the trigger word");
            return;
        }
        if (getCommand(mess.split(" ")[1]) != null) {
            System.out.println("I would have run " + getCommand(mess.split(" ")[1]).getCommand());
            Main.pipeline.sendData(getCommand(mess.split(" ")[1]).extractData(mess));
        } else System.out.println("No command found");
    }

    public static void onMessageReceived(MessageEvent message) {
        if (message.getChannel().equals("") || message.getBotId() != null) {
            return;
        }
        if (message.getText().charAt(0) == '!' && getCommand(message.getText()) != null) {
            System.out.println("Recieved Message: " + message.getText());
            if (!LISTENING) {
                AbstractCommand command = getCommand(message.getText());
                if (command.isServerSideCommand()) {
                    System.out.println("Running serverside " + command.extractData(message));
                    command.run(command.extractData(message)).doYourWorst(SlackBot.getBotObject());
                } else {
                    System.out.println("Sending to bot " + command.extractData(message));
                    Main.pipeline.sendData(command.extractData(message));
                }
            }
        }
    }

    public MessageHandler() {

    }

    /**
     * Called by the {@link JDA} when a new message is sent visible to the brobot
     *
     * @param message the incomming message
     */
    @Override
    @ServerSide
    public void onMessageReceived(MessageReceivedEvent message) {
        if (message.getAuthor().isBot())
            return;
        if (!message.isFromGuild())
            return;
        if (message.getMessage().getContentRaw().charAt(0) == '!' && getCommand(message.getMessage().getContentRaw()) != null) {
            System.out.println("Recieved Message: " + message.getMessage().getContentRaw());
            if (!LISTENING) {
                AbstractCommand command = getCommand(message.getMessage().getContentRaw());
                if (command == null)
                    return;
                if (command.isServerSideCommand()) {
                    try {
                        command.run(command.extractData(message)).doYourWorst(DiscordBot.getBotObject());
                    } catch (NullPointerException ignored) {
                    }
                } else {
                    System.out.println("Sending to bot " + command.extractData(message));
                    Main.pipeline.sendData(command.extractData(message));
                }
            } else
                throw new IllegalStateException("How did you get here as a client?");
        }
    }
}