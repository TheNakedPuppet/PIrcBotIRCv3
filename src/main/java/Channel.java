import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by TNP on 8/27/2016.
 */
public class Channel {
    private String name;
    private ArrayList<Command> commands;
    private String saveLocation;
    private ArrayList<IRCv3User> users;
    private String lang,emote_only,r9k,slowMode,subs_only;
    private String viewerlistAPIURL;


    public Channel(String name){
        this.name = name;
        commands = new ArrayList<Command>();
        users = new ArrayList<IRCv3User>();
        this.saveLocation = name+"Commands.json";
        this.viewerlistAPIURL = "http://tmi.twitch.tv/group/user/" + name.replace("#","") +  "/chatters";
        //addCommand(new HealthyCommand());
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
}
