import java.util.ArrayList;

public class IRCv3User {
    private String name;
    private String displayName;
    private long ID;
    private boolean isSubscribed, isMod, isTurbo;
    private int exp;
    private int level;
    private ArrayList<Channel> channels;
    public IRCv3User(Channel channel, String name, String displayName, long ID, boolean isMod, boolean isSubscribed, boolean isTurbo ) {
        this.name = name;
        this.displayName = displayName;
        this.ID = ID;
        this.isMod = isMod;
        this.isSubscribed = isSubscribed;
        this.isTurbo = isTurbo;
        channels.add(channel);
    }

    public IRCv3User setExp(int exp){
        //TODO set up an actual level system
        this.exp = exp;
        this.level = (exp / 10) + 1;
        return this;
    }

    public IRCv3User setLevel(int lvl){
        this.level = lvl;
        return this;
    }
}
