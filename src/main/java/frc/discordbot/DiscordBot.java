package frc.discordbot;

import frc.discordbot.commands.AbstractCommand;
import frc.misc.ServerSide;
import frc.robot.robotconfigs.DefaultConfig;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;

import java.net.InetAddress;

public class DiscordBot {
    public static DiscordBot bot = null;
    private final JDA botObject;

    /**
     * Instaniates {@link #bot} if it hasnt already. Also checks for internet connection
     *
     * @param listening True if client, false if server bot object
     * @return the created bot object (not needed since it is stored at {@link #bot}
     */
    public static DiscordBot newInstance(boolean listening) {
        detectedInternet();
        if (bot == null)
            return bot = new DiscordBot(listening);
        return bot;
    }

    public static boolean detectedInternet() {
        try {
            if (!InetAddress.getByName("google.com").isReachable(1000)) {
                System.out.println("NO INTERNET DETECTED");
            } else {
                System.out.println("Internet detected !!!!!!!");
                return true;
            }
        } catch (Exception ignored) {
            System.out.println("NO INTERNET?");
        }
        return false;
    }

    private DiscordBot(boolean listener) {
        AbstractCommand.DRIVEN = listener;
        MessageHandler.loadCommands(listener);
        JDA holder = null;
        if (!listener) {
            try {
                System.out.println(DefaultConfig.BOTKEY);
                holder = new JDABuilder(AccountType.BOT).addEventListener(new MessageHandler()).setToken(DefaultConfig.BOTKEY).build();
            } catch (Exception e) {
                System.err.println("Discord bot failed to init");
                e.printStackTrace();
            }
        }
        botObject = holder;
    }

    @ServerSide
    public JDA getBotObject() {
        return botObject;
    }
}
