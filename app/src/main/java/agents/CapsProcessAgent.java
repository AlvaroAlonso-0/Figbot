package agents;

import auxiliar.Constants;
import behaviours.ReceiveMessage;
import behaviours.SendMessage;
import jade.content.lang.sl.SLCodec;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import models.ActionDataMessage;
import models.TwitchMessageHolder;

public class CapsProcessAgent extends Agent{

    protected TwitchMessageHolder holder;
    private ActionDataMessage actionData;
 
    @Override
    protected void setup(){
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setName("procesador-de-mayusculas");
        sd.setType("procesar-mensajes");
        sd.addLanguages(new SLCodec().getName());
        dfd.addServices(sd);

        try{
            DFService.register(this, dfd);
        } catch(FIPAException e){
            System.err.println("Agent dead "+ this.getLocalName()+"\n\t"+e.getMessage());
        }
        holder = new TwitchMessageHolder();
        actionData = new ActionDataMessage();
        addBehaviour(new ReceiveMessage(this,holder));
        addBehaviour(new ProcessMessage());
        addBehaviour(new SendMessage(this,actionData));
    }

    private class ProcessMessage extends CyclicBehaviour {

        @Override
        public void action() {
            if (holder.getMessage() != null){
                if(exceedsCapsLimit(holder.getMessage().getMessage())){
                    actionData.setMessage(holder.getMessage());
                    actionData.setAction(Constants.Code.CAPS_ALERT);
                }
                holder.setMessage(null);
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
