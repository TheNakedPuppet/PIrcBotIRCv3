/**
 * Created by TNP on 8/27/2016.
 */
public class Command {
    private String trigger;
    private String response;
    private CommandType type;
    private int cooldown;

    public Command(String trigger, String response, CommandType type){
        this.type = type;
        this.response = response;
        this.trigger = trigger;
    }
    public Command(String trigger, String response, CommandType type, int cooldown){
        this.type = type;
        this.response = response;
        this.trigger = trigger;
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
        if(c1.trigger.equalsIgnoreCase(trigger)){
            return true;
        }
        return false;
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
}
