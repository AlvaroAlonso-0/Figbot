package agents;

import behaviours.ReceiveMessage;
import behaviours.SendMessage;
import jade.content.lang.sl.SLCodec;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import models.TwitchMessageHolder;

public class CapsProcessAgent extends Agent{

    protected TwitchMessageHolder holder;
 
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
        addBehaviour(new ReceiveMessage(this,holder));
        addBehaviour(new ProcessMessage());
        addBehaviour(new SendMessage(this,holder));
    }

    private class ProcessMessage extends CyclicBehaviour {

        @Override
        public void action() {
            if (holder.getMessage() != null){
                if(exceedsCapsLimit(holder.getMessage().getMessage())){
                    System.out.println("Demasiadas mayusculas: " + holder.getMessage().getMessage().substring(0, 20));
                }
                holder.setMessage(null); 
            }
        }

        private boolean exceedsCapsLimit(String message){
            if(message.length() < 20) return false;
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
