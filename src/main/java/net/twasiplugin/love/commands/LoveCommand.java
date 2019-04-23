package net.twasiplugin.love.commands;

import net.twasi.core.database.models.Language;
import net.twasi.core.database.models.User;
import net.twasi.core.plugin.api.TwasiUserPlugin;
import net.twasi.core.plugin.api.customcommands.TwasiCustomCommandEvent;
import net.twasi.core.plugin.api.customcommands.TwasiPluginCommand;
import net.twasi.core.services.ServiceRegistry;
import net.twasi.core.services.providers.DataService;
import net.twasi.core.translations.renderer.TranslationRenderer;
import net.twasi.twitchapi.helix.users.response.UserDTO;
import net.twasi.twitchapi.options.TwitchRequestOptions;
import net.twasiplugin.love.database.LoveRepository;
import net.twasiplugin.love.database.LoveEntity;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static net.twasi.twitchapi.TwitchAPI.helix;

public class LoveCommand extends TwasiPluginCommand {

    private LoveRepository repo = ServiceRegistry.get(DataService.class).get(LoveRepository.class);

    public LoveCommand(TwasiUserPlugin twasiUserPlugin) {
        super(twasiUserPlugin);
    }

    private List<String> cryEmotes = Arrays.asList(":(", "BibleThump", "WutFace", "DansGame", "FailFish", "NotLikeThis", "ScaredyCat", "ResidentSleeper", "ANELE", "RuleFive", "SabaPing", "AngryJack", "SMOrc");
    private List<String> normalEmotes = Arrays.asList(":)", "TheIlluminati", "YouDontSay", "SabaPing", "HappyJack", "BrokeBack", "KevinTurtle", "ThunBeast", "CoolStoryBob", "PartyPopper", "<3");
    private List<String> loveEmotes = Arrays.asList("Kreygasm", "PogChamp", "TableHere <3", "FortHype", "ThunBeast", "HassaanChop", "bleedPurple", "SeemsGood", "MVGame", "Poooound", "<3 <3 <3");

    @Override
    protected boolean execute(TwasiCustomCommandEvent e) {
        TranslationRenderer renderer = e.getRenderer();
        User user = e.getStreamer().getUser();

        if (!e.hasArgs()) {
            e.reply(renderer.render("syntax"));
            return false;
        }

        boolean checkOthers = e.getArgs().size() >= 2;

        UserDTO resolvedUser, resolvedSecondUser = null;
        try {
            String[] names = (checkOthers) ? new String[]{e.getArgs().get(0), e.getArgs().get(1)} : new String[]{e.getArgs().get(0)};
            List<UserDTO> users = helix().users().getUsers(null, names, new TwitchRequestOptions().withAuth(user.getTwitchAccount().toAuthContext()));
            resolvedUser = users.get(0);
            if (checkOthers) resolvedSecondUser = users.get(1);
        } catch (Exception ex) {
            e.reply(renderer.render("love.error.notfound"));
            return false;
        }

        LoveEntity byTwitchIds = repo.getByTwitchIdsAndSkipResolve(
                (checkOthers) ? resolvedSecondUser.getId() : e.getSender().getTwitchId(),
                resolvedUser.getId(),
                (checkOthers) ? resolvedSecondUser.getDisplayName() : e.getSender().getDisplayName(),
                resolvedUser.getDisplayName()
        );
        renderer.bindObject("entity", byTwitchIds);

        float num = byTwitchIds.getNumber() * 1f;
        renderer.bind("cloud", Math.round(num / 100f * 9f) + "");
        if (user.getConfig().getLanguage().equals(Language.DE_DE))
            renderer.bind("cloud", Math.round(byTwitchIds.getNumber() / 100f * 7f) + "");

        Collections.shuffle(cryEmotes);
        Collections.shuffle(normalEmotes);
        Collections.shuffle(loveEmotes);
        String emote = cryEmotes.get(0);
        if (byTwitchIds.getNumber() >= 20) emote = normalEmotes.get(0);
        if (byTwitchIds.getNumber() >= 70) emote = loveEmotes.get(0);
        renderer.bind("emote", emote).bindObject("account1", byTwitchIds.getAccount1()).bindObject("account2", byTwitchIds.getAccount2()).bindObject("number", byTwitchIds.getNumber());

        e.reply(renderer.render(byTwitchIds.getLoveAnswer().getKey()));
        return true;
    }

    @Override
    public Duration getCooldown() {
        return Duration.ZERO;
    }

    public String getCommandName() {
        return "love";
    }
}
