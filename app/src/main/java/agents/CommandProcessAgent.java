package agents;

import auxiliar.ASCIIDrawings;
import behaviours.ReceiveMessage;
import behaviours.SendMessage;
import jade.content.lang.sl.SLCodec;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import models.TwitchMessage;
import models.TwitchMessageHolder;

public class CommandProcessAgent extends Agent{

    private static final String  COMMAND_TOKEN = "f!";
    protected TwitchMessageHolder holder;
    
 
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
        addBehaviour(new ReceiveMessage(this,holder));
        addBehaviour(new ProcessMessage());
        addBehaviour(new SendMessage(this,holder));
    }

    private class ProcessMessage extends CyclicBehaviour {

        @Override
        public void action() {
            if (holder.getMessage() != null){
                //System.out.println(holder.getMessage());
                processCommand(holder.getMessage());
                holder.setMessage(null); 
            }
        }

        private void processCommand(TwitchMessage twitchMessage){
            if (!twitchMessage.getMessage().matches("^" + COMMAND_TOKEN + "[\\s\\S]+")) return;

            String msg = twitchMessage.getMessage().split(" ")[0].substring(2);
            System.out.println(msg);

            String ret = null;
            switch(msg){
                case "saludo" : break;
                case "amogus" : ret = ASCIIDrawings.SMALL_AMOGUS;
                                break;
                default: ret = "Este comando no existe";
            }

            System.out.println(ret);
        }
    }
}
