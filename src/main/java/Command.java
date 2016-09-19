import com.google.gson.JsonObject;

/**
 * Created by TNP on 8/27/2016.
 */
public class Command {
    private Channel channel;
    private String trigger;
    private String response;
    private CommandType type;
    private int cooldown;
    private String typeString;
    private int modOnly,subOnly;
    //TODO implement cooldown

    public Command(Channel c){
        channel = c;
        type = CommandType.DUMMY;
        trigger = "";
        response = "";
        cooldown = 0;
        typeString = getTypeAsString(type);
    }
    public Command(String trigger, String response, CommandType type, Channel c) {
        channel = c;
        this.type = type;
        this.response = response;
        this.trigger = trigger;
        typeString = getTypeAsString(type);
    }
    public Command(String trigger, String response, CommandType type, int cooldown , Channel c){
        channel = c;
        this.type = type;
        this.response = response;
        this.trigger = trigger;
        this.cooldown = cooldown;
        typeString = getTypeAsString(type);
    }

    public Command(String trigger, String response, CommandType type, int cooldown,int modOnly, Channel c){
        channel = c;
        this.type = type;
        this.response = response;
        this.trigger = trigger;
        this.cooldown = cooldown;
        typeString = getTypeAsString(type);
        this.modOnly = modOnly;
    }
    public Command(String trigger, String response, CommandType type, int cooldown, int modOnly, int subOnly, Channel c){
        channel = c;
        this.type = type;
        this.response = response;
        this.trigger = trigger;
        this.cooldown = cooldown;
        typeString = getTypeAsString(type);
        this.modOnly = modOnly;
        this.subOnly = subOnly;
    }
    public String getResponse(JsonObject json, String message){
        switch(type){
            case DUMMY:
                if(message.startsWith(trigger)){
                    return response;
                }return null;
            case CONTAINS:
                if(message.contains((trigger))){
                    return response;
                }return null;

            case CONTAINS_MULTIPLE:
                if(message.contains((trigger))){
                    return response;
                }return null;
            case MATCHES:
                if(message.contains((trigger))){
                    return response;
                }return null;
            case ADD:
                if(message.startsWith(trigger)){
                    System.err.println("add reached");
                    return getSpecialResponse(json,message);
                }return null;
            case LEVEL:
                if(message.startsWith(trigger)){
                    return getSpecialResponse(json,message);
                }return null;
            default:
                System.err.println("default reached");
                return null;
        }
    }

    public String getSpecialResponse(JsonObject json, String msg){
        switch(type){
            case HEALTHY:
                //do healthy stuff
                return "Healthy stuff to return soon MrDestructoid";




            case ADD:
                if(!json.get("sender").getAsString().equalsIgnoreCase("ayitzchance")){
                    if(!json.get("sender").getAsString().equalsIgnoreCase("thenakedpuppet")){
                        return "";
                    }
                }
                System.err.println("Starting add command...");
                //TODO Parse msg for command
                //Format should be "AddTrigger Type Trigger Response | (optional cooldown) | (optional modOnly) |  (optional subOnly)
                msg = msg.replace(trigger,"").trim();
                String[] parts = msg.split(" ",3);
                    /*
                        parts[0] = Type
                        parts[1] = Trigger
                        parts[2] = Response...
                     */
                if(parts.length != 3)  return "Error parsing command (Initial message splitting failed)";
                Command c;
                CommandType cType = getCommandTypeFromString(parts[0]);
                String trigger = parts[1],response = "";
                parts = parts[2].split(" | ");
                int cooldown = 0, modOnly = 0, subOnly = 0;

                switch (parts.length){
                    case 1:
                        response = parts[0];
                        break;
                    case 2:
                        response = parts[0];
                        cooldown = Integer.parseInt(parts[1]);
                        break;
                    case 3:
                        response = parts[0];
                        cooldown = Integer.parseInt(parts[1]);
                        modOnly = Integer.parseInt(parts[2]);
                        break;
                    case 4:
                        response = parts[0];
                        cooldown = Integer.parseInt(parts[1]);
                        modOnly = Integer.parseInt(parts[2]);
                        subOnly = Integer.parseInt(parts[3]);
                        break;
                    default:
                        return "Error parsing message (parameter length out of range)";
                }

                c = new Command(trigger,response,cType,cooldown,modOnly,subOnly,channel);
                System.err.println("Adding command " + c.toString());
                channel.getCommands().add(c);
                return "Command " + trigger + " added.";



            case LEVEL:
                System.err.println("Lvl reached");
                IRCv3User user = channel.getUserByName(json.get("sender").getAsString());
                String n = user.getDisplayName();
                if(user.getDisplayName().equalsIgnoreCase(user.getName())&&!json.get("display-name").getAsString().equalsIgnoreCase(user.getName())){
                    n = json.get("display-name").getAsString();
                }
                return n + ": Level " + user.getLevel() + " (" + user.getExp() + "xp)";

            default:
                return "Error parsing message (Default Hit)";

        }
    }
    public static String getTypeAsString(CommandType type){
        switch(type){
            case DUMMY:
                return "dummy";
            case CONTAINS:
                return "contains";
            case CONTAINS_MULTIPLE:
                return "contains_multiple";
            case MATCHES:
                return "matches";
            case ADD:
                return "add";
            case HEALTHY:
                return "healthy";
            case LEVEL:
                return "level";
            default:
                return "null";
        }
    }
    public static CommandType getCommandTypeFromString(String s){
        switch (s){
            case "dummy":
                            return CommandType.DUMMY;
            case "contains":
                            return CommandType.CONTAINS;
            case "contains_multiple":
                            return  CommandType.CONTAINS_MULTIPLE;
            case "matches":
                            return CommandType.MATCHES;
            case "add":
                            return CommandType.ADD;
            case "healthy":
                            return CommandType.HEALTHY;
            case "level":
                            return CommandType.LEVEL;
            default:        return CommandType.DUMMY;
        }
    }

    public String getTrigger(){
        return trigger;
    }

    public CommandType getType(){
        return type;
    }

    public int getCooldown(){
        return cooldown;
    }

    public String getResponse(){
        return response;
    }


    public String getTypeString(){
        return typeString;
    }

    public Command setTrigger(String s){
        trigger = s;
        return this;
    }
    public Command setResponse(String s){
        response = s;
        return this;
    }
    public Command setType(CommandType s){
        type = s;
        typeString = getTypeAsString(s);
        return this;
    }
    public Command setType(String s){
        type = getCommandTypeFromString(s);
        typeString = s;
        return this;
    }
    public Command setCooldown(int seconds){
        cooldown = seconds;
        return this;
    }

    public int isModOnly(){return modOnly; }
    public void setModOnly(int b){modOnly = b;}

    public int isSubOnly(){return subOnly; }
    public void setSubOnly(int b){subOnly = b;}

    public String toString(){
        return getTypeString() + " : " + getTypeString() + " " + getTrigger() + " : " + getResponse() + " cd: " + cooldown;
    }

    public boolean equals(Object o){
        Command c1 = (Command) o;
        return c1.trigger.equalsIgnoreCase(trigger);
    }

    public boolean equals(String trigger){
        return trigger.equalsIgnoreCase(this.trigger);
    }
}
