package agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;

import auxiliar.Constants;
import auxiliar.Utils;
import behaviours.ReceiveTwitchMessage;
import behaviours.SendMessage;


import models.ActionDataMessage;
import models.TwitchMessageHolder;

public class CapsProcessAgent extends Agent{

    protected TwitchMessageHolder holder;
    private ActionDataMessage actionData;
 
    @Override
    protected void setup(){
        Utils.registerOneService(this, "procesador-de-mayusculas", "procesar-mensajes");

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
                if(exceedsCapsLimit(holder.getTwitchMessage().getMessage())){
                    actionData.setMessage(holder.getTwitchMessage());
                    actionData.setAction(Constants.Code.CAPS_ALERT);
                }
                holder.setTwitchMessage(null);
            }
        }

        private boolean exceedsCapsLimit(String message){
            if(message.length() < 15) return false;
            int limit = (message.length() <= 40) ? 75 : 60;
            int caps = 0;
            for(int i = 0; i < message.length(); i++){
                if(Character.isUpperCase(message.charAt(i))){
                    caps++;
                }
            }
            return (caps*100)/message.length() > limit;
        }
    }
}
