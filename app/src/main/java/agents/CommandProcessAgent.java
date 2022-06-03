package agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;

import auxiliar.Constants;
import auxiliar.Utils;
import behaviours.ReceiveTwitchMessage;
import behaviours.SendMessage;
import models.ActionDataMessage;
import models.TwitchMessageHolder;

public class CommandProcessAgent extends Agent{
    
    private static final String  COMMAND_TOKEN = "f!";
    private static final int COMMAND_TOKEN_LENGTH = 2;

    protected TwitchMessageHolder holder;
    private ActionDataMessage actionData;
    
 
    @Override
    protected void setup(){
        Utils.registerOneService(this, "procesador-de-comandos", "procesar-mensajes");

        holder = new TwitchMessageHolder();
        actionData = new ActionDataMessage();
        
        addBehaviour(new ReceiveTwitchMessage(this,holder));
        addBehaviour(new ProcessMessage());
        addBehaviour(new SendMessage(this,actionData));
    }

    private class ProcessMessage extends CyclicBehaviour {

        @Override
        public void action() {
            if (holder.getTwitchMessage() != null){
                actionData.setAction(processCommand(holder.getTwitchMessage().getMessage()));
                actionData.setMessage(holder.getTwitchMessage());
                holder.setTwitchMessage(null); 
            }
        }

        private int processCommand(String message){
            if (!message.matches("^" + COMMAND_TOKEN + "[\\s\\S]+")) return Constants.Code.ERROR;
            
            String [] splited_msg = message.split(" ");
            String msg = splited_msg[0].substring(COMMAND_TOKEN_LENGTH);

            int ret;
            switch(msg){
                case Constants.Commands.GREETINGS : 
                    if (splited_msg.length == 1) {
                        ret = Constants.Code.GREETINGS_SELF;
                    }else{
                        ret = Constants.Code.GREETINGS_USER;
                        actionData.setArgument(splited_msg[1]);
                    }
                    break;
                case Constants.Commands.SHOUTOUT : ret = Constants.Code.SHOUTOUT; actionData.setArgument(splited_msg.length == 1 ? holder.getTwitchMessage().getUserName() : splited_msg[1]); break;
                case Constants.Commands.DICE  :
                    if (splited_msg.length == 1){
                        ret = Constants.Code.DICE_DEFAULT;
                    }else {
                        ret = Constants.Code.DICE_N;
                        actionData.setArgument(splited_msg[1]);
                    }
                    break;
                case Constants.Commands.TIME  : ret = Constants.Code.TIME; break;
                case Constants.Commands.VIDEO : ret = Constants.Code.VIDEO; actionData.setArgument(splited_msg.length == 1 ? holder.getTwitchMessage().getChannelName() : splited_msg[1]); break;
                case Constants.Commands.TITLE : ret = Constants.Code.TITLE; break;
                case Constants.Commands.HELP : ret = Constants.Code.HELP; break;
                default: ret = Constants.Code.ERROR;
            }
            return ret;
        }
    }
}
