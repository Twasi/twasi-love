package net.twasiplugin.love.database;

import net.twasi.core.database.lib.Repository;
import net.twasi.core.database.models.User;
import org.mongodb.morphia.query.Query;

public class LoveRepository extends Repository<LoveEntity> {

    public LoveEntity getByTwitchIds(User user, String id1, String id2) {
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
        if (user != null) loveEntity.resolve(user);
        return loveEntity;
    }

}
