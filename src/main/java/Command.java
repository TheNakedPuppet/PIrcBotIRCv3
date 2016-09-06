/**
 * Created by TNP on 8/27/2016.
 */
public class Command {
    private String trigger;
    private String response;
    private CommandType type;
    private int cooldown;
    private String typeString;

    public Command(String trigger, String response, CommandType type){
        this.type = type;
        this.response = response;
        this.trigger = trigger;
        switch(type){
            case DUMMY:
                        typeString = "dummy";
                        break;
            case CONTAINS:
                        typeString = "contains";
                        break;
            default:
                        typeString = "dummy";
        }


    }
    public Command(String trigger, String response, CommandType type, int cooldown){
        this.type = type;
        this.response = response;
        this.trigger = trigger;
        this.cooldown = cooldown;
        switch(type){
            case DUMMY:
                typeString = "dummy";
                break;
            case CONTAINS:
                typeString = "contains";
                break;
            default:
                typeString = "dummy";
        }
    }

    public String getResponse(String message){
        switch(type){
            case DUMMY:
                if(message.startsWith(trigger)){
                    return response;
                }return null;
            default:
                return null;
        }
    }

    public boolean equals(Object o){
        Command c1 = (Command) o;
        return c1.trigger.equalsIgnoreCase(trigger);
    }

    public String getTrigger(){
        return trigger;
    }

    public CommandType getType(){
        return type;
    }

    public boolean setCooldown(int cd){
        cooldown = cd;
        return true;
    }

    public int getCooldown(){
        return cooldown;
    }

    public String toString(){
        return getTrigger() + " : " + getResponse(getTrigger());
    }

    public String getTypeString(){
        return typeString;
    }

    public String getResponse(){
        return response;
    }
}
