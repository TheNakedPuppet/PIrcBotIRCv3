import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by TNP on 8/27/2016.
 */
public class Channel {
    private String name;
    private ArrayList<Command> commands;
    private String saveLocation;


    public Channel(String name){
        this.name = name;
        commands = new ArrayList<Command>();
        this.saveLocation = name+"Commands.json";
        addCommand(new Command("!bot","I'm a bot made by スズヤ!",CommandType.DUMMY));
        addCommand(new Command("butt","I'm a butt made by スズヤ!",CommandType.CONTAINS));
        addCommand(new Command("!test","This is a test command!",CommandType.DUMMY));
        try {
            saveToFile(saveLocation);
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
    public ArrayList<Command> readFromFile(String saveLocation) throws FileNotFoundException {
        ArrayList<Command> commands = new ArrayList<Command>();
        JsonReader jr = new JsonReader(new FileReader(new File(saveLocation)));
        Command c;
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
}
