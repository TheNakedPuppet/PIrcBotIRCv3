import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.PircBot;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by TNP on 8/27/2016.
 */
public class Bot extends PircBot{
    private String ver = "0.0.3";
    private String name;
    private String oauth;
    private ArrayList<Channel> assignedChannels;
    static String _twitch = "irc.chat.twitch.tv.";
    static int _port = 6667;
    private Gson gson;

    //TODO add capabilities, add command trigger check
    public Bot(String name, String oauth, String initialChannel){
        this.name = name;
        this.oauth = oauth;
        this.changeNick(name);
        this.setName(name);
        this.assignedChannels = new ArrayList<Channel>();
        assignedChannels.add(new Channel(initialChannel));
        this.setVerbose(true);

    }
    public Bot(String name, String oauth) {
        this.name = name;
        this.oauth = oauth;
        this.assignedChannels = new ArrayList<Channel>();
        this.changeNick(name);
        this.setName(name);
        this.setVerbose(true);


        gson = new Gson();
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

        System.err.println("UNKNOWN: " + line);

        System.err.println(gson.toJson(j).toString());

    }

    public static JsonObject parseTags(String msg){
        JsonObject j = new JsonObject();
        String tagType = "";
        int i = 0;

        for(String tag : IRCv3TAGS.TAGS){

            if(msg.contains(tag)){
                tagType = tag;
                System.err.println("Tag found = " + tag);
                break;
            }
        }
        if(tagType == ""){
            j.addProperty("error","TAG_TYPE_UNKNOWN");
            return j;
        }else System.err.println("Continuing...");
        msg = msg.replace(" " + tagType,";" + tagType);
        msg =msg.replaceAll("=;","=null;");
        String[] tagSets = msg.split(";");
        for(String tagSet : tagSets){
            if(i != tagSets.length-1){
                String[] values = tagSet.split("=");
                j.addProperty(values[0],values[1]);
                System.err.println("Added vals " + values[0] + " : " + values[1] + " @ i=" + i);
            }
            else{
                String value = tagSet.replace(tagType,"");
                j.addProperty("trailingData",value.trim());
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
        String response = "";
        Channel currentChannel = getChannelByName(channel);
        System.err.printf("MESSAGE channel=%s  sender =%s login=%s hostname=%s \n message=%s\n",channel,sender,login,hostname,message);
        if(currentChannel == null) {
            System.out.println("CURRENTCHANNEL NULL, getChannelByName() found no channel");
            return;
        }

        for(Command command :  currentChannel.getCommands()){

            if((response = command.getResponse(message)) != null){
                System.out.println("Command found and triggered successfully!");
                sendMessage(channel,response);
                return;
            }
        }
        System.out.println("Message didn't trigger any commands");

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
}
