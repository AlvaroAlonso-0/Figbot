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
import models.ActionData;
import models.ActionDataMessage;
import models.TwitchMessageHolder;

public class CommandProcessAgent extends Agent{

    private static final String  COMMAND_TOKEN = "f!";
    protected TwitchMessageHolder holder;
    private ActionDataMessage actionData;
    
 
    @Override
    protected void setup(){
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setName("procesador-de-comandos");
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
                int action = processCommand(holder.getMessage().getMessage());
                actionData.setAction(action);
                actionData.setMessage(holder.getMessage());
                holder.setMessage(null); 
            }
        }

        private int processCommand(String message){
            if (!message.matches("^" + COMMAND_TOKEN + "[\\s\\S]+")) return Constants.Code.ERROR;
            
            String [] splited_msg = message.split(" ");
            String msg = splited_msg[0].substring(2);
            System.out.println(msg);

            int ret;
            switch(msg){
                case Constants.Commands.GREETINGS : 
                    if (splited_msg.length == 1) {
                        ret = Constants.Code.GREETINGS_SELF;
                    }
                    else{
                        ret = Constants.Code.GREETINGS_USER;
                        actionData.setArgument(splited_msg[1]);
                    }
                    break;
                case Constants.Commands.SHOUTOUT : ret = Constants.Code.SHOUTOUT; actionData.setArgument(splited_msg[1]); break;
                case Constants.Commands.DICE  :
                    if (splited_msg.length == 1){
                        ret = Constants.Code.DICE_DEFAULT;
                    }
                    else {
                        ret = Constants.Code.DICE_N;
                        actionData.setArgument(splited_msg[1]);
                    }
                    break;
                case Constants.Commands.TIME  : ret = Constants.Code.TIME; break;
                case Constants.Commands.SUBS  : ret = Constants.Code.SUBS; break;
                case Constants.Commands.TITLE : ret = Constants.Code.TITLE; break;
                default: ret = Constants.Code.ERROR;
            }
            return ret;
        }
    }
}
