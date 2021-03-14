package me.heyimblake.proxyparty.partyutils;

public class PartyPermission {

    private final String name;

    private final String prefix;

    private final Integer size;

    public PartyPermission(String name, String prefix, Integer size) {
        this.name = name;

        this.prefix = prefix;

        this.size = size;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public Integer getSize() {
        return size;
    }
}
