package frc.discordbot;

import frc.discordbot.commands.*;
import frc.misc.ClientSide;
import frc.misc.ServerSide;
import frc.robot.Main;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MessageHandler extends ListenerAdapter {
    public static final HashMap<String, AbstractCommand> commands = new HashMap<>();
    private static final HashMap<String, AbstractCommand> commandsAlias = new HashMap<>();
    private static final ArrayList<AbstractCommand.AbstractCommandData> pendingCommands = new ArrayList<>();
    public static MessageHandler messageHandler;
    private static boolean LISTENING;

    public static void loadCommands(boolean listening) {
        LISTENING = listening;
        List<Class<? extends AbstractCommand>> classes = Arrays.asList(PlaySongCommand.class, PingCommand.class, StatusCommand.class, RoboPingCommand.class, Wait5TicksThenReplyCommand.class, DriveDistanceCommand.class, OpenURLCommand.class, HelpCommand.class, RandomQuoteCommand.class, ShutdownServerCommand.class);
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
                pendingCommands.add(message);
                if (command != null)
                    Main.pipeline.sendReply(command.run(message));
            } else
                throw new IllegalStateException("How did you get here as a client?");
        }
    }

    public static AbstractCommand getCommand(String message) {
        if (commands.containsKey(message.substring(1).split(" ")[0]))
            return commands.get(message.substring(1).split(" ")[0]);
        if (commandsAlias.containsKey(message.substring(1).split(" ")[0]))
            return commandsAlias.get(message.substring(1).split(" ")[0]);
        return null;
    }

    /**
     * Used on the robot to tick ongoing commands such as {@link Wait5TicksThenReplyCommand}
     */
    @ClientSide
    public static void persistPendingCommands() {
        for (int i = pendingCommands.size() - 1; i >= 0; i--) {
            if (Main.pipeline.sendReply(commands.get(pendingCommands.get(i).CONTENT.substring(1).split(" ")[0]).run(pendingCommands.get(i)))) {
                pendingCommands.remove(i);
            }
        }
    }

    public MessageHandler() {

    }

    /**
     * Called by the {@link DiscordBot#bot} when a new message is sent visible to the brobot
     *
     * @param message the incomming message
     */
    @Override
    @ServerSide
    public void onMessageReceived(MessageReceivedEvent message) {
        if (message.getAuthor().isBot())
            return;
        if (message.getGuild() == null)
            return;
        if (message.getMessage().getContentRaw().charAt(0) == '!' && getCommand(message.getMessage().getContentRaw()) != null) {
            System.out.println("Recieved Message: " + message.getMessage().getContentRaw());
            if (!LISTENING) {
                AbstractCommand command = getCommand(message.getMessage().getContentRaw());
                if (command == null)
                    return;
                if (command.isServerSideCommand()) {
                    try {
                        command.run(command.extractData(message)).doYourWorst(DiscordBot.bot.getBotObject());
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
