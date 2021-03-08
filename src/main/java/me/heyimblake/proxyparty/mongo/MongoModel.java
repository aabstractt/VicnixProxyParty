package me.heyimblake.proxyparty.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import me.heyimblake.proxyparty.ProxyParty;
import me.heyimblake.proxyparty.partyutils.Party;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import java.util.Random;

public class MongoModel {

    private MongoClient mc;
    private Morphia morphia;
    private Datastore datastore;
    private PartyPlayer partyDAO;

    public MongoModel(String uri) {
        mc = new MongoClient(new MongoClientURI(uri));
        morphia = new Morphia();
        morphia.map(PartyReply.class);

        datastore = morphia.createDatastore(mc, "VicnixCore");
        datastore.ensureIndexes();

        partyDAO = new PartyPlayer(PartyReply.class, datastore);
    }

    public void createParty(Party party){
        ProxyParty.getInstance().getProxy().getScheduler().runAsync(ProxyParty.getInstance(), ()-> {
            PartyReply reply = new PartyReply();
            reply.setId(new Random().nextInt(Integer.MAX_VALUE));
            reply.setLeader(party.getLeader().getName());
            reply.setMembers(party.parseMembers());
            reply.setOpen(party.isPartyPublic() ? 1 : 0);
            partyDAO.save(reply);
        });
    }

    public void updateParty(Party party){
        ProxyParty.getInstance().getProxy().getScheduler().runAsync(ProxyParty.getInstance(), ()-> {
            Query<PartyReply> query = datastore.createQuery(PartyReply.class).field("leader").contains(party.getLeader().getName());

            UpdateOperations<PartyReply> modified = datastore.createUpdateOperations(PartyReply.class)
                    .set("members", party.parseMembers())
                    .inc("open", (party.isPartyPublic() ? 1 : 0));

            partyDAO.update(query, modified);
        });
    }

    public void disbandParty(Party party){
        ProxyParty.getInstance().getProxy().getScheduler().runAsync(ProxyParty.getInstance(), ()-> {
            String leader = party.getLeader().getName();

            PartyReply reply = partyDAO.findOne("leader", leader);

            if(reply != null){
                partyDAO.delete(reply);
            }
        });
    }
}