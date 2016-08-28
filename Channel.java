import org.jibble.pircbot.PircBot;

import java.util.ArrayList;

/**
 * Created by TNP on 8/27/2016.
 */
public class Channel {
    private String name;
    private ArrayList<Command> commands;


    public Channel(String name){
        this.name = name;
        commands = new ArrayList<Command>();
        addCommand(new Command("!bot","I'm a bot made by スズヤ!",CommandType.DUMMY));
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
}
