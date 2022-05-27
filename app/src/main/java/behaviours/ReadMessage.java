package behaviours;

import java.util.Scanner;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class ReadMessage extends CyclicBehaviour {
    private Scanner sc = new Scanner(System.in);
    private Agent myAgent;
    public ReadMessage (Agent agente){
        myAgent = agente;
    }
    @Override
    public void action() {
        String message = null;
        if(sc.hasNext()){
            message = sc.nextLine();
            System.out.println("El mensaje enviado es -> "+message);

            // Mandar mensaje al agente de procesamiento

            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType("recibir-mensajes");
            template.addServices(sd);

            try{
                DFAgentDescription[] result = DFService.search(myAgent, template);
                AID[] processingAgents = new AID[result.length];
                for(int i=0; i<result.length; i++){
                    processingAgents[i] = result[i].getName();
                }
            } catch(FIPAException fe){
                fe.printStackTrace();
            }

            /*myAgent.addBehaviour(new ReceiveMessage());

            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.addReceiver(myAgent.getAID());
            msg.setContent(message);
            send(msg);*/
        }
    }
}

    
    
    