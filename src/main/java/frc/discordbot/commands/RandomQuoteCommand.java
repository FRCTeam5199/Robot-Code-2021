package frc.discordbot.commands;

import frc.misc.QuoteOfTheDay;
import frc.misc.ServerSide;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.Nullable;

@ServerSide
public class RandomQuoteCommand extends AbstractCommand {
    @Override
    public @Nullable AbstractCommandResponse run(AbstractCommandData message) {

        return new RandomQuoteCommandResponse(message);
    }

    @Override
    public String getCommand() {
        return "randomquote";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"awisemanoncesaid", "wiseman", "quote", "rq"};
    }

    public String getArgs() {
        return "<url to open>";
    }

    @Override
    public boolean isServerSideCommand() {
        return true;
    }

    public static class RandomQuoteCommandResponse extends AbstractCommandResponse {
        public RandomQuoteCommandResponse(AbstractCommandData data) {
            super(data);
        }

        @Override
        public void doYourWorst(JDA client) {
            client.getTextChannelById(CHANNEL_ID).sendMessage("A wise man once said:\n" + QuoteOfTheDay.getRandomQuote()).queue();
        }
    }
}
