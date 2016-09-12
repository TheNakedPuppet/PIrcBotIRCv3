import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    static HashMap<String,String> botNames; // botName,oauth
    static HashMap<String,String> channels; //channelName, botName
    static ArrayList<Bot> bots = new ArrayList<Bot>();

    public static void main(String args[]) throws Exception {
        botNames = new HashMap<String, String>();
        channels = new HashMap<String, String>();
        botNames.put("thenakedpuppet","oauth:7ykmzlxp3698ik1jnfw3xng86p63zj");
        channels.put("#thenakedpuppet","thenakedpuppet");
        //channels.put("#ayitzchance","thenakedpuppet");


        for(String s: botNames.keySet()){
            String v = botNames.get(s);
            bots.add(new Bot(s,v));
            System.err.println("Bot created " + s);
        }
        populateChannels();
        for(Bot b: bots){
            b.init();
        }
    }
     static void populateChannels() throws Exception{
        for(String s: channels.keySet()){
            String v = channels.get(s);
            for(Bot b : bots){
                if(b.getBotName().equalsIgnoreCase(v)){
                    b.getAssignedChannels().add(new Channel(s));
                    System.err.println("Bot name: " + b.getBotName());
                    break;
                }

            }
        }
    }
}
