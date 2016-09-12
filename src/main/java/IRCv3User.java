import java.util.Date;

public class IRCv3User {
    private String name;
    private String displayName;
    private long ID;
    private boolean isSubscribed, isMod, isTurbo;
    private int exp;
    private int level;
    private Date dateJoined, dateLastSeen;
    private double hoursWatched;
    private int messagesSent;
    private int commandsSent;


    public IRCv3User(Channel channel, String name, String displayName, long ID, boolean isMod, boolean isSubscribed, boolean isTurbo ) {
        this.name = name;
        this.displayName = displayName;
        this.ID = ID;
        this.isMod = isMod;
        this.isSubscribed = isSubscribed;
        this.isTurbo = isTurbo;
        this.dateJoined = new Date();
        this.dateLastSeen = new Date();
        this.hoursWatched = 0;
        this.messagesSent = 0;
        this.commandsSent = 0;
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

    public String getName() {
        return name;
    }

    public IRCv3User setName(String name) {
        this.name = name;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public IRCv3User setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;

    }

    public long getID() {
        return ID;
    }

    public IRCv3User setID(long ID) {
        this.ID = ID;
        return this;
    }

    public boolean isSubscribed() {
        return isSubscribed;
    }

    public IRCv3User setSubscribed(boolean subscribed) {
        isSubscribed = subscribed;
        return this;
    }

    public boolean isMod() {
        return isMod;
    }

    public IRCv3User setMod(boolean mod) {
        isMod = mod;
        return this;

    }

    public boolean isTurbo() {
        return isTurbo;
    }

    public void setTurbo(boolean turbo) {
        isTurbo = turbo;
    }

    public int getExp() {
        return exp;
    }

    public int getLevel() {
        return level;
    }
}
