import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Channel {
    private String name;
    private ArrayList<Command> commands;
    private String saveLocation;

    private ArrayList<IRCv3User> users;

    private String viewerlistAPIURL;
    private HttpURLConnection channelStatusConnection;

    private String channelStatusURL;
    private String lang,emote_only,r9k,slowMode,subs_only;
    private int chatterCount;

    private ScheduledExecutorService sched = Executors.newScheduledThreadPool(1);

    public class Response {
        @SerializedName("chatter_count")
        public String chatter_count;
        @SerializedName("chatters")
        public Chatters chatters;

    }
    public class Chatters{
        @SerializedName("moderators")
        public List<String> moderators;
        @SerializedName("viewers")
        public List<String> viewers;
    }


    public Channel(String name){
        this.name = name;
        commands = new ArrayList<Command>();
        users = new ArrayList<IRCv3User>();
        this.saveLocation = "usr/"+name+"Commands.json";
        this.viewerlistAPIURL = "http://tmi.twitch.tv/group/user/" + name.replace("#","") +  "/chatters";
        this.channelStatusURL = "http://api.twitch.tv/kraken/channels/" + name.replace("#","");
        sched.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {

                try {
                    //users = getUsers(viewerlistAPIURL);
                   // getChannelStatus();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },0,10, TimeUnit.SECONDS);
        addCommand(new HealthyCommand());
        //addCommand(new Command("!bot","I'm a bot made by スズヤ!",CommandType.DUMMY));
        //addCommand(new Command("butt","I'm a butt made by スズヤ!",CommandType.CONTAINS));
        //addCommand(new Command("!test","This is a test command!",CommandType.DUMMY));

        try {
            if(new File(saveLocation).exists()){
                readFromFile(saveLocation);
            }else{
                saveToFile(saveLocation);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }


    public ArrayList<Command> getCommands() {
        return commands;
    }

    public Boolean addCommand(Command command){
        for(Command c: commands){
            if(c.equals(command)){
                return false;
            }
        }
        commands.add(command);
        try {
            saveToFile(saveLocation);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }


    public boolean removeCommand(String trigger){
        for (Command c : commands){
            if(c.getTrigger().equalsIgnoreCase(trigger)){
                commands.remove(c);
                return true;
            }
        }
        return false;
    }
    public ArrayList<Command> readFromFile(String saveLocation) throws IOException {
        commands = new ArrayList<Command>();
        String trigger="",response="";
        CommandType type = CommandType.DUMMY;
        int cooldown = 0;
        Command c;
        JsonReader jr = new JsonReader(new FileReader(new File(saveLocation)));
        jr.beginObject();
        jr.nextName();
        jr.beginArray();
        while (jr.hasNext()){
            jr.beginObject();
            while(jr.hasNext()){

                String name = jr.nextName();
                switch(name){
                    case "type":
                        type = Command.getCommandTypeFromString(jr.nextString());
                        break;
                    case "trigger":
                        trigger = jr.nextString();
                        break;
                    case "response":
                        response = jr.nextString();
                        break;
                    case "cooldown":
                        cooldown = jr.nextInt();
                        break;
                    default:
                        System.err.println("Default Hit");
                }
            }
            jr.endObject();
            if(trigger.equals("!healthy")){
                c=  new HealthyCommand().setTrigger(trigger).setResponse(response).setType(type).setCooldown(cooldown);
            }else{
                c = new Command(trigger,response,type,cooldown);
            }
            System.err.println("Loaded Command: " + c.toString());
            commands.add(c);
        }
        jr.endArray();
        jr.endObject();
        jr.close();

        return commands;
    }
    public boolean saveToFile(String saveLocation) throws IOException {
        JsonWriter writer = new JsonWriter(new FileWriter(new File(saveLocation)));
        writer.setIndent("  ");
        writer.beginObject();
        writer.name(getName());

        writer.beginArray();
        for(Command c : commands) {
            writer.beginObject();
            writer.name("type").value(c.getTypeString());
            writer.name("trigger").value(c.getTrigger());
            writer.name("response").value(c.getResponse());
            writer.name("cooldown").value(c.getCooldown());
            writer.endObject();
            System.err.println("Added Command "+ c.getTrigger());
        }
        writer.endArray();
        writer.endObject();
        writer.close();

        return true;
    }

    public void setRoomstate(JsonObject j){
        try{
            this.lang = j.get("broadcaster-lang").getAsString();
            this.emote_only = j.get("emote-only").getAsString();
            this.r9k = j.get("r9k").getAsString();
            this.slowMode = j.get("slow").getAsString();
            this.subs_only = j.get("subs-only").getAsString();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void getChannelStatus() throws Exception {
        channelStatusConnection = (HttpURLConnection) new URL(channelStatusURL).openConnection();
        channelStatusConnection.setRequestMethod("GET");
        channelStatusConnection.setRequestProperty("Client-ID","l9ck16iaqcb6hc0rtikvh4xusps00so");
        //TODO
    }


    public IRCv3User getUserByName(String name){
        for(IRCv3User user : users){
            if(user.getName().equalsIgnoreCase(name)){
                return user;
            }
        }
        return null;
    }

    public ArrayList<IRCv3User> getUsers(){
        return users;
    }

    public ArrayList<IRCv3User> getUsers(String viewerlistAPIURL)throws Exception{

        String json = readJsonFromUrl(viewerlistAPIURL);
        //System.err.println(json);
        ArrayList<IRCv3User> users = new ArrayList<IRCv3User>();

        Gson gson = new Gson();
        Response r = gson.fromJson(json,Response.class);
        chatterCount = Integer.parseInt(r.chatter_count);
        for(String n : r.chatters.moderators){
            users.add(new IRCv3User(this,n,n,Bot.assignID(),true,false,false));
            System.err.println("added moderator " + n);
        }
        for(String n : r.chatters.viewers){
            users.add(new IRCv3User(this,n,n,Bot.assignID(),false,false,false));
            System.err.println("added viewer " + n);

        }
        return users;
        /*
        JsonReader jr = new JsonReader(new FileReader(new File(saveLocation)));

        jr.beginObject(); //{
        System.err.println(jr.peek());
        jr.nextName();
        System.err.println(jr.peek());
        jr.beginArray(); //    "_links":{},
        System.err.println(jr.peek());
        jr.endArray();
        System.err.println(jr.peek());

        jr.endObject();
        System.err.println(jr.peek());

        jr.nextName();
        System.err.println(jr.peek());
        this.chatterCount = jr.nextInt(); //"chatter_count": xx,

        jr.nextName(); //"chatters":{
        jr.beginObject();

        jr.nextName();
        jr.beginArray(); //"moderators"
        while(jr.hasNext()){
            String name = jr.nextString();
            users.add(new IRCv3User(this,name,name,Bot.assignID(),true,false,false));
            System.err.println("Found user " + name);

        } jr.endArray();//moderators end

        jr.nextName();
        jr.beginArray(); //staff (skip)
        jr.endArray();

        jr.nextName();
        jr.beginArray(); //admin (skip too)
        jr.endArray();

        jr.nextName();
        jr.beginArray(); //global_mods (them too)
        jr.endArray();

        jr.nextName(); //viewers (keep them)
        jr.beginArray();
        while (jr.hasNext()){
            String name = jr.nextString();
            users.add(new IRCv3User(this,name,name,Bot.assignID(),false,false,false));
            System.err.println("Found user " + name);

        }
        jr.endArray(); //end viewers
        jr.endObject(); //end chatters
        jr.endObject(); //end it all
        jr.close();

        return users;
      */
    }

    public static String readJsonFromUrl(String urlString) throws Exception{
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }
}