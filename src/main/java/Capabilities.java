/**
 * Created by TNP on 8/27/2016.
 * https://github.com/justintv/Twitch-API/blob/master/IRC.md
 */
public enum Capabilities {
    Membership, //CAP REQ :twitch.tv/membership : Adds membership state event (NAMES, JOIN, PART, or MODE)
    Commands,   //CAP REQ :twitch.tv/commands   : General notices from the server
    Tags,       //CAP REQ :twitch.tv/tags       : Adds IRC message tags to PRIVMSG, USERSTATE, NOTICE , GLOBALUSERSTATE
                //https://github.com/justintv/Twitch-API/blob/master/IRC.md#tags
}
