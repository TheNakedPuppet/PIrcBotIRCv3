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

    private Bot bot;
    private ArrayList<IRCv3User> users;

    private String viewerlistAPIURL;
    private HttpURLConnection channelStatusConnection;

    private String channelStatusURL;
    private String lang,emote_only,r9k,slowMode,subs_only, status, game;
    private boolean isPartner;
    private int chatterCount, views, followers;

    private ScheduledExecutorService sched = Executors.newScheduledThreadPool(2);

    public class ViewerListResponse {
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



    public Channel(String name, Bot b){
        bot = b;
        this.name = name;
        commands = new ArrayList<Command>();
        users = new ArrayList<IRCv3User>();
        this.saveLocation = "cmd/"+name+"Commands.json";
        this.viewerlistAPIURL = "https://tmi.twitch.tv/group/user/" + name.replace("#","") +  "/chatters";
        this.channelStatusURL = "https://api.twitch.tv/kraken/channels/" + name.replace("#","");
        //commands.add(new Command("!suzuya add","",CommandType.ADD,0,1,0,this));
        //commands.add(new Command("!level","",CommandType.LEVEL,0,0,0,this));

        sched.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    users = getUsers(viewerlistAPIURL);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },0,10, TimeUnit.SECONDS);

        sched.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    getChannelStatus();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },0,1,TimeUnit.MINUTES);

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
        int modOnly = 0, subOnly = 0;
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
                    case "modOnly":
                        modOnly = jr.nextInt();
                        break;
                    case "subOnly":
                        subOnly = jr.nextInt();
                        break;
                    default:
                        System.err.println("Default Hit");
                }
            }
            jr.endObject();
            switch(type){
                case HEALTHY:
                    c = new Command("!healthy",response,CommandType.HEALTHY,cooldown,modOnly,subOnly,this);
                    break;
                case ADD:
                    //TODO CHANGE BOT COMMAND NAME
                    c = new Command("!suzuya add","",CommandType.ADD,cooldown,modOnly,subOnly,this);
                    break;
                case LEVEL:
                    c = new Command("!level","",CommandType.LEVEL,cooldown,modOnly,subOnly,this);
                default:
                    c = new Command(trigger,response,type,cooldown,modOnly,subOnly,this);

            }
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
            this.emote_only = j.get("emote-only").getAsString();
            this.r9k = j.get("r9k").getAsString();
            this.slowMode = j.get("slow").getAsString();
            this.subs_only = j.get("subs-only").getAsString();
        }catch(Exception e){
            e.printStackTrace();
        }
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
        //TODO load users from file
        String json = readJsonFromUrl(viewerlistAPIURL);
        ArrayList<IRCv3User> newUsers = new ArrayList<IRCv3User>();

        Gson gson = new Gson();
        ViewerListResponse r = gson.fromJson(json,ViewerListResponse.class);
        chatterCount = Integer.parseInt(r.chatter_count);
        ArrayList<String> currentUsers = new ArrayList<String>();
        for(IRCv3User u : getUsers()){
            currentUsers.add(u.getName());
        }
        for(String n : r.chatters.moderators){
            if(currentUsers.contains(n)){
                newUsers.add(getUserByName(n).addExp(10));
            }
            else {
                newUsers.add(new IRCv3User(this, n, n, Bot.assignID(), 1, 0, 0).addExp(10));
            }
        }
        for(String n : r.chatters.viewers){
            if(currentUsers.contains(n)){
                newUsers.add(getUserByName(n).addExp(10));
            }else {
                newUsers.add(new IRCv3User(this, n, n, Bot.assignID(), 0, 0, 0).addExp(10));
            }
        }
        return newUsers;
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

    private class Roomstate{
        @SerializedName("status")
        public String status;
        @SerializedName("game")
        public String game;
        @SerializedName("partner")
        public boolean partner;
        @SerializedName("views")
        public int views;
        @SerializedName("followers")
        public int followers;
    }

    public void setRoomstate(Roomstate r){
        status = r.status;
        game = r.game;
        isPartner = r.partner;
        views = r.views;
        followers = r.followers;
    }

    public void getChannelStatus() throws Exception {
        channelStatusConnection = (HttpURLConnection) new URL(channelStatusURL).openConnection();
        channelStatusConnection.setRequestMethod("GET");
        channelStatusConnection.setRequestProperty("Accept","application/vnd.twitchtv.v3+json");
        channelStatusConnection.setRequestProperty("Client-ID","l9ck16iaqcb6hc0rtikvh4xusps00so");
        channelStatusConnection.connect();
        channelStatusConnection.getContentType();
        BufferedReader br = new BufferedReader(new InputStreamReader(channelStatusConnection.getInputStream()));
        String content = br.readLine();
        br.close();
        Gson gson = new Gson();
        setRoomstate(gson.fromJson(content,Roomstate.class));
    }

}