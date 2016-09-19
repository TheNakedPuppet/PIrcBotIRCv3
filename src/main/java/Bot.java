
import com.google.gson.JsonObject;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.PircBot;

import java.io.IOException;
import java.util.ArrayList;

public class Bot extends PircBot{

    private String ver = "0.9.18";

    private String name;
    private String oauth;
    private ArrayList<Channel> assignedChannels;
    static String _twitch = "irc.chat.twitch.tv.";
    static int _port = 6667;
    public static int users = 0;

    //TODO Move assigned channels to file
    public Bot(String name, String oauth, String initialChannel){
        this.name = name;
        this.oauth = oauth;
        this.changeNick(name);
        this.setName(name);
        this.assignedChannels = new ArrayList<Channel>();
        assignedChannels.add(new Channel(initialChannel,this));
        this.setVerbose(true);

    }
    public Bot(String name, String oauth) {
        this.name = name;
        this.oauth = oauth;
        this.assignedChannels = new ArrayList<Channel>();
        this.changeNick(name);
        this.setName(name);
        this.setVerbose(true);
    }

    public Bot init() throws IrcException, IOException {
        System.err.println("Starting Bot " + name);
        connect(_twitch , _port, oauth);
        this.sendRawLine("CAP REQ :twitch.tv/membership");
        this.sendRawLine("CAP REQ :twitch.tv/commands");
        this.sendRawLine("CAP REQ :twitch.tv/tags");
        for (Channel c: assignedChannels){
            joinChannel(c.getName());
            System.err.println("Joined chanel " + c.getName());
        }
        return this;
    }
    //TODO Fix tagged messages
    @Override
    public void onUnknown(String line){
        JsonObject j = parseTags(line);
        String err = j.get("error").getAsString();
        if(!err.equalsIgnoreCase("NONE")){
            System.err.println("ERROR: " + err);
            return;
        }
        switch(j.get("tagType").getAsString()){
            case(IRCv3TAGS.TAG_PRIVMSG):
                                onMessage(j);
                                break;
            case(IRCv3TAGS.TAG_ROOMSTATE):
                                getChannelByName(j.get("trailingData").getAsString()).setRoomstate(j);
                                break;

        }
    }

    public static JsonObject parseTags(String msg){
        JsonObject j = new JsonObject();
        String tagType = "";
        int i = 0;

        for(String tag : IRCv3TAGS.TAGS){

            if(msg.contains(tag)){
                tagType = tag;
                msg.replaceAll(tag, "");
                break;
            }
        }
        if(tagType.equals("")){
            j.addProperty("error","TAG_TYPE_UNKNOWN");
            return j;
        }
        msg = msg.replace(" " + tagType,";" + tagType);
        msg = msg.replaceAll("=;","=null;");
        String[] tagSets = msg.split(";");
        for(String tagSet : tagSets){
            if(i != tagSets.length - 1){
                String[] values = tagSet.split("=");
                j.addProperty(values[0].replace("@",""),values[1]);
            }
            else{
                String value = tagSet.replace(tagType,"");                  //THIS
                if(tagType.equalsIgnoreCase(IRCv3TAGS.TAG_PRIVMSG)) {       //IS
                    String[] values = value.split(":",2);                   //FUCKING
                    String[] lastTagSet = values[0].split("=");             //DISGUSTING
                    j.addProperty(lastTagSet[0],lastTagSet[1]);             //TWITCH
                    j.addProperty("sender",values[1].split("!")[0]);        //PLEASE
                    values = values[1].split("#")[1].split(":");            //FIX

                    j.addProperty("channel","#" + values[0].trim());
                    j.addProperty("message",values[1].trim());
                    j.addProperty("trailingData","null");
                }else {
                    j.addProperty("trailingData", value.trim());
                }
                j.addProperty("tagType",tagType);
                j.addProperty("error","NONE");
                return j;
            }
            i++;
        }
        return j;
    }

    @Override
    public void onMessage(String channel,String sender,String login,String hostname,String message){
        JsonObject json = new JsonObject();
        json.addProperty("channel",channel);
        json.addProperty("sender",sender);
        json.addProperty("login",login);
        json.addProperty("hostname",hostname);
        json.addProperty("message",message);
        json.addProperty("display-name",sender);
        onMessage(json);
    }
    public void onMessage(JsonObject json){
        String response ="";
        String channel = json.get("channel").getAsString();
        String sender = json.get("sender").getAsString();
        String message = json.get("message").getAsString();
        String login = this.getLogin();
        String hostname = this.getBotName();
        Channel currentChannel = getChannelByName(channel);
        IRCv3User user = currentChannel.getUserByName(sender);
        String display_name = json.get("display-name").getAsString();
        if(user == null){
            user  = new IRCv3User(currentChannel,name,display_name,assignID(),0,0,0);
            currentChannel.getUsers().add(user);
        }
        if(currentChannel == null) {
            System.out.println("CURRENTCHANNEL NULL, getChannelByName() found no channel");
            return;
        }

        for(Command command :  currentChannel.getCommands()){
            response = command.getResponse(json,message);
            if(response != null && !response.equals("")){
                System.out.println("Command found and triggered successfully! response = " + response);
                sendMessage(channel,Bot.formatResponse(response,json));
                return;
            }
        }
       // System.out.println(message);
    }

    public String getBotName(){
        return name;
    }

    public Channel getChannelByName(String s){
        for(Channel c: assignedChannels){
            if(c.getName().equalsIgnoreCase(s)){
                return c;
            }
        }

        return null;
    }

    public ArrayList<Channel> getAssignedChannels(){
        return assignedChannels;
    }

    public static String formatResponse(String msg,JsonObject j){
        //TODO replace all response constants
        msg = msg.replaceAll("%MESSAGE%",j.get("message").getAsString());
        msg = msg.replaceAll("%SENDER%",j.get("sender").getAsString());
        return msg;
    }

    public void addCommand(JsonObject json){
        String message = json.get("message").getAsString();

    }
    //TODO save ID database and assign IDs
    public static long assignID(){
        return ++users;
    }
}
