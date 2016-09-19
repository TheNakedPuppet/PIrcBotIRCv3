import java.util.Date;

public class IRCv3User {
    private String name;
    private String displayName;
    private long ID;
    private int isSubscribed;
    private int isMod;
    private int isTurbo; // -1 for false, 0 for unknown, 1 for true
    private int exp;
    private int level;
    private Date dateJoined, dateLastSeen;
    private double hoursWatched;
    private int messagesSent;
    private int commandsSent;


    public IRCv3User(Channel channel, String name, String displayName, long ID, int isMod, int isSubscribed, int isTurbo ) {
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
    public IRCv3User addExp(int exp){
        this.exp += exp;
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

    public int isSubscribed() {
        return isSubscribed;
    }

    public IRCv3User setSubscribed(int subscribed) {
        isSubscribed = subscribed;
        return this;
    }

    public int isMod() {
        return isMod;
    }

    public IRCv3User setMod(int mod) {
        isMod = mod;
        return this;

    }

    public int isTurbo() {
        return isTurbo;
    }

    public void setTurbo(int turbo) {
        isTurbo = turbo;
    }

    public int getExp() {
        return exp;
    }

    public int getLevel() {
        return level;
    }
}
