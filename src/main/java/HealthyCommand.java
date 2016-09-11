public class HealthyCommand extends Command{
    public HealthyCommand(){
        this.setType(CommandType.DUMMY);
        this.setTrigger("!healthy");
        this.setCooldown(30000);
        this.setResponse("%healthyID%:%healthyURL%");

    }
    @Override
    public String getResponse(String message){
        String response = "";
        if(message.startsWith(getTrigger())){
            //TODO set up link system and return string with img ID
            response = getHealthyURL(message);
            //setResponse("healthy command is coming soon fufufufu");

        }
        return response;
    }

    @Override
    public String getResponse(){
        return "%healthyID%:%healthyURL%";
    }
    public String getHealthyURL(String optionalNumber){
        String response ="";
        response = "No healthy command yet MrDestructoid";
        return response;
    }
}
