package net.twasiplugin.love.database;

import jdk.nashorn.internal.ir.annotations.Ignore;
import net.twasi.core.database.models.BaseEntity;
import net.twasi.core.database.models.TwitchAccount;
import net.twasi.core.database.models.User;
import net.twasi.twitchapi.helix.users.response.UserDTO;
import net.twasi.twitchapi.options.TwitchRequestOptions;
import net.twasiplugin.love.commands.LoveAnswers;
import org.mongodb.morphia.annotations.Entity;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static net.twasi.twitchapi.TwitchAPI.helix;

@Entity(value = "twasi.love", noClassnameStored = true)
public class LoveEntity extends BaseEntity {

    private String twitchId1;
    private String twitchId2;
    private LoveAnswers loveAnswer;
    private int number;

    @Ignore
    private TwitchAccount account1 = null;
    @Ignore
    private TwitchAccount account2 = null;

    public LoveEntity() {
    }

    public LoveEntity(String twitchId1, String twitchId2, LoveAnswers loveAnswer, int number) {
        this.twitchId1 = twitchId1;
        this.twitchId2 = twitchId2;
        this.loveAnswer = loveAnswer;
        this.number = number;
    }

    public static LoveEntity getInstance(String twitchId1, String twitchId2) {
        List<LoveAnswers> loveAnswers = new ArrayList<>(Arrays.asList(LoveAnswers.values()));
        Collections.shuffle(loveAnswers);
        LoveAnswers answers = loveAnswers.get(0);
        int num = ThreadLocalRandom.current().nextInt(answers.getMinLove(), answers.getMaxLove() + 1);
        return new LoveEntity(twitchId1, twitchId2, answers, num);
    }

    public String getTwitchId1() {
        return twitchId1;
    }

    public String getTwitchId2() {
        return twitchId2;
    }

    public LoveAnswers getLoveAnswer() {
        return loveAnswer;
    }

    public int getNumber() {
        return number;
    }

    public TwitchAccount getAccount1() {
        if (account1 == null) return new TwitchAccount(null, null, null, twitchId1, null);
        return account1;
    }

    public TwitchAccount getAccount2() {
        if (account2 == null) return new TwitchAccount(null, null, null, twitchId2, null);
        return account2;
    }

    public void setAccount1(TwitchAccount account1) {
        this.account1 = account1;
    }

    public void setAccount2(TwitchAccount account2) {
        this.account2 = account2;
    }

    public void resolve(User resolvingUser) {
        List<UserDTO> users = helix().users().getUsers(new String[]{twitchId1, twitchId2}, null, new TwitchRequestOptions().withAuth(resolvingUser.getTwitchAccount().toAuthContext()));
        UserDTO user1 = users.get(0);
        UserDTO user2 = users.get(1);
        if (user1 != null) {
            account1 = new TwitchAccount(user1.getLogin(), user1.getDisplayName(), null, user1.getId(), null);
        }
        if (user2 != null) {
            account2 = new TwitchAccount(user2.getLogin(), user2.getDisplayName(), null, user2.getId(), null);
        }
    }
}
