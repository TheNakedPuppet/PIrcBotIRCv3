import com.sun.javafx.collections.MappingChange;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by TNP on 8/27/2016.
 */
public class Main {
    static HashMap<String,String> botNames; // botName,oauth
    static HashMap<String,String> channelBots; //channelName, botName
    static ArrayList<Bot> bots = new ArrayList<Bot>();
    //static HashMap<Channel,Bot> channels  = new HashMap<Channel, Bot>();

    public static void main(String args[]) throws Exception {
        botNames = new HashMap<String, String>();
        channelBots = new HashMap<String, String>();
        botNames.put("thenakedpuppet","oauth:7ykmzlxp3698ik1jnfw3xng86p63zj");
        channelBots.put("#thenakedpuppet","thenakedpuppet");
        channelBots.put("#neet_elysion","thenakedpuppet");


        for(String s: botNames.keySet()){
            String v = botNames.get(s);
            bots.add(new Bot(s,v));
        }
        populateChannels();

        for(Bot b: bots){
            b.init();
        }


    }
     static void populateChannels() throws Exception{
        for(String s: channelBots.keySet()){
            String v = channelBots.get(s);
            for(Bot b : bots){
                if(b.getBotName().equalsIgnoreCase(v)){
                    b.getAssignedChannels().add(new Channel(s));
                    return;
                }System.out.println("Bot name; " + b.getBotName());

            }
            throw new Exception("No bot named what name is given (" + s + ", " + v + " )");
        }
    }
}
