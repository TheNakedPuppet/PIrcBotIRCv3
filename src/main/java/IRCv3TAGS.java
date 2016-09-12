/**
 * Created by TNP on 8/28/2016.
 */
public class IRCv3TAGS {
    final static String TAG_HOSTTARGET      = ":tmi.twitch.tv HOSTTARGET"     ;
    final static String TAG_CLEARCHAT       = ":tmi.twitch.tv CLEARCHAT"      ;
    final static String TAG_USERSTATE       = ":tmi.twitch.tv USERSTATE"      ;
    final static String TAG_RECONNECT       = ":tmi.twitch.tv RECONNECT"      ;
    final static String TAG_ROOMSTATE       = ":tmi.twitch.tv ROOMSTATE"      ;
    final static String TAG_USERNOTICE      = ":tmi.twitch.tv USERNOTICE"     ;
    final static String TAG_GLOBALUSERSTATE = ":tmi.twitch.tv GLOBALUSERSTATE";

    final static String TAG_JOIN    = ".tmi.twitch.tv JOIN" ;
    final static String TAG_PART    = ".tmi.twitch.tv PART" ;
    final static String TAG_PRIVMSG = ".tmi.twitch.tv PRIVMSG";

    final static String TAG_CAP_TAGS = ":tmi.twitch.tv CAP * ACK :twitch.tv/tags";
    final static String TAG_CAP_COMMANDS = ":tmi.twitch.tv CAP * ACK :twitch.tv/commands";
    final static String TAG_CAP_MEMBERSHIP = ":tmi.twitch.tv CAP * ACK :twitch.tv/membership";

    public static String[] TAGS = new String[]{
            TAG_HOSTTARGET,  //0
            TAG_CLEARCHAT,   //1
            TAG_USERSTATE ,  //2
            TAG_RECONNECT,   //3
            TAG_ROOMSTATE,   //4
            TAG_USERNOTICE,  //5
            TAG_GLOBALUSERSTATE, //6
            TAG_JOIN,       //7
            TAG_PART,       //8
            TAG_PRIVMSG,     //9
            TAG_CAP_TAGS,   //10
            TAG_CAP_COMMANDS,   //11
            TAG_CAP_MEMBERSHIP  //12
    };
}