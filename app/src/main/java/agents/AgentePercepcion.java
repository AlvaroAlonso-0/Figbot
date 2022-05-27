package agents;

import behaviours.ReadMessage;
import jade.content.lang.sl.SLCodec;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class AgentePercepcion extends Agent{

    protected ReadMessage readMessage;
    
    protected void setup(){
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setName("lector-de-chat");
        sd.setType("leer-mensajes");
        sd.addLanguages(new SLCodec().getName());
        dfd.addServices(sd);

        try{
            DFService.register(this, dfd);
        } catch(FIPAException e){
            System.err.println("Agent dead xd "+ this.getLocalName()+"\n\t"+e.getMessage());
        }

        

        System.out.println("Soy el Agente de Percepcion");
        readMessage = new ReadMessage(this);
        addBehaviour(readMessage);


    }
}