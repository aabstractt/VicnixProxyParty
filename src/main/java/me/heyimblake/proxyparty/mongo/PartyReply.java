package me.heyimblake.proxyparty.mongo;

import org.mongodb.morphia.annotations.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity(value = "vicnix_party", noClassnameStored = true)

public class PartyReply {

    @Id
    private int id;

    @Indexed(options = @IndexOptions(unique = true))
    private String leader;

    @Property("members")
    private String members;


    @Property("open")
    private int open;

    public PartyReply() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public void setMembers(String members) {
        this.members = members;
    }

    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }

    public String getLeader() {
        return leader;
    }

    public Set<String> getMembers() {
        Set<String> list = new HashSet<>();
        list.addAll(Arrays.asList(this.members.split(",")));
        return Collections.unmodifiableSet(list);
    }
}
