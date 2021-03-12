package frc.discordbot;

import frc.robot.RobotSettings;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;

import java.io.IOException;
import java.net.InetAddress;

public class DiscordBot {
    public static DiscordBot bot = null;
    private final JDA botObject;

    private DiscordBot(){
        JDA holder = null;
        try {
            holder = new JDABuilder(AccountType.BOT).setToken(RobotSettings.DISCORD_BOT_TOKEN).build();
        }catch (Exception e){
            System.err.println("Discord bot failed to init");
            e.printStackTrace();
        }
        botObject = holder;
    }

    public static DiscordBot newInstance(){
        try {
            if (!InetAddress.getByName("discord.com").isReachable(1000)) {
                System.out.println("NO INTERNET DETECTED");
                return null;
            }
        } catch (IOException ignored){

        }
        if (bot == null)
            return bot = new DiscordBot();
        return bot;
    }
}
