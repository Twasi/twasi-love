package net.twasiplugin.love.database;

import net.twasi.core.database.lib.Repository;
import net.twasi.core.database.models.TwitchAccount;
import net.twasi.core.database.models.User;
import org.mongodb.morphia.query.Query;

public class LoveRepository extends Repository<LoveEntity> {

    public LoveEntity getByTwitchIdsAndSkipResolve(String id1, String id2, String displayName1, String displayName2) {
        Query<LoveEntity> query = store.createQuery(LoveEntity.class);
        query.or(
                query.criteria("twitchId1").equal(id1).criteria("twitchId2").equal(id2),
                query.criteria("twitchId1").equal(id2).criteria("twitchId2").equal(id1)
        );
        LoveEntity loveEntity = query.get();
        if (loveEntity == null) {
            loveEntity = LoveEntity.getInstance(id1, id2);
            add(loveEntity);
        }
        loveEntity.setAccount1(new TwitchAccount(null, displayName1, null, null, null));
        loveEntity.setAccount2(new TwitchAccount(null, displayName2, null, null, null));
        return loveEntity;
    }

    public LoveEntity getByTwitchIdsAndResolve(User user, String id1, String id2) {
        LoveEntity byTwitchIds = getByTwitchIdsAndSkipResolve(id1, id2, null, null);
        byTwitchIds.resolve(user);
        return byTwitchIds;
    }
}
