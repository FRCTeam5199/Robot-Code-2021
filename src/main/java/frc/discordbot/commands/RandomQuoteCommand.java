package frc.discordbot.commands;

import frc.misc.QuoteOfTheDay;
import frc.misc.ServerSide;
import org.jetbrains.annotations.Nullable;

@ServerSide
public class RandomQuoteCommand extends AbstractCommand {
    @Override
    public @Nullable AbstractCommandResponse run(AbstractCommandData message) {
        return new GenericCommandResponse(message, "A wise man once said:\n" + QuoteOfTheDay.getRandomQuote());
    }

    @Override
    public String getCommand() {
        return "randomquote";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"awisemanoncesaid", "wiseman", "quote", "rq"};
    }

    @Override
    public boolean isServerSideCommand() {
        return true;
    }
}
