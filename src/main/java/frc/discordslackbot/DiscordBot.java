package frc.discordslackbot;

import frc.misc.ServerSide;
import frc.misc.UtilFunctions;
import frc.robot.robotconfigs.DefaultConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

/**
 * Our special JDA manager, this is where the fun starts
 */
public class DiscordBot {
    private static DiscordBot bot = null;
    private final JDA botObject;

    /**
     * Instaniates {@link #bot} if it hasnt already. Also checks for internet connection
     *
     * @param listening True if client, false if server bot object
     */
    public static void newInstance(boolean listening) {
        UtilFunctions.detectedInternet();
        if (bot == null) {
            bot = new DiscordBot(listening);
        }
    }

    /**
     * Returns the JDA (real bot object, not the DiscordBot instance)
     *
     * @return the only JDA constructed by the only discord bot
     */
    @ServerSide
    public static JDA getBotObject() {
        return bot.botObject;
    }

    /**
     * Creates a new DiscordBot (not to be confused with a new {@link JDA} which is the actual discord api wrapper)
     *
     * @param listener allows {@link MessageHandler#loadCommands(boolean)} to be run without attempting to create a bot
     *                 on the client
     */
    private DiscordBot(boolean listener) {
        MessageHandler.loadCommands(listener);
        JDA holder = null;
        if (!listener) {
            try {
                System.out.println(DefaultConfig.BOTKEY);
                holder = JDABuilder.createDefault(DefaultConfig.BOTKEY).addEventListeners(new MessageHandler()).build();
            } catch (Exception e) {
                System.err.println("Discord bot failed to init");
                e.printStackTrace();
            }
        }
        botObject = holder;
    }
}