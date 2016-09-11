/**
 * Created by TNP on 8/27/2016.
 */
public class Command {
    private String trigger;
    private String response;
    private CommandType type;
    private int cooldown;
    private String typeString;
    //TODO implement cooldown and text replacement

    public Command(){
        type = CommandType.DUMMY;
        trigger = "";
        response = "";
        cooldown = 0;
        typeString = getTypeAsString(type);
    }
    public Command(String trigger, String response, CommandType type) {
        this.type = type;
        this.response = response;
        this.trigger = trigger;
        typeString = getTypeAsString(type);
    }
    public Command(String trigger, String response, CommandType type, int cooldown){
        this.type = type;
        this.response = response;
        this.trigger = trigger;
        this.cooldown = cooldown;
        typeString = getTypeAsString(type);
    }

    public String getResponse(String message){
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
            case STATIC:
                if(message.contains((trigger))){
                    return response;
                }return null;
            default:
                return null;
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
            case STATIC:
                return "static";
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
            case "static":
                            return CommandType.STATIC;
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
    public Command setCooldown(int milliseconds){
        cooldown = milliseconds;
        return this;
    }

    public String toString(){
        return getTypeString() + " " + getTrigger() + " : " + getResponse(getTrigger()) + " cd: " + cooldown;
    }

    public boolean equals(Object o){
        Command c1 = (Command) o;
        return c1.trigger.equalsIgnoreCase(trigger);
    }
}
