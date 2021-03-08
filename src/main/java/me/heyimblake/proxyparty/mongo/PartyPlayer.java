package me.heyimblake.proxyparty.mongo;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;

public class PartyPlayer extends BasicDAO<PartyReply, String> {


    public PartyPlayer(Class<PartyReply> entityClass, Datastore ds) {
        super(entityClass, ds);
    }

}
