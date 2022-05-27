package agents;

import behaviours.ProcessMessage;
import jade.content.lang.sl.SLCodec;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class AgenteProcesamiento extends Agent{

    protected ProcessMessage processMessage;
    
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
            System.err.println("Agent dead xd "+ this.getLocalName()+"\n\t"+e.getMessage());
        }
        System.out.println("Soy el Agente de Procesamiento");
        processMessage = new ProcessMessage(this);
        addBehaviour(processMessage);
    }
}
