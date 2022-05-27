package behaviours;

import java.util.Scanner;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class ReceiveMessage extends CyclicBehaviour {
    private Scanner sc = new Scanner(System.in);
    private Agent myAgent;
    public ReceiveMessage (Agent agente){
        myAgent = agente;
    }
    @Override
    public void action() {
        ACLMessage message = myAgent.receive();
        if(message!=null){
            System.out.println("El mensaje enviado es -> "+message);

            // Mandar mensaje al agente de procesamiento
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.addReceiver(myAgent.getAID());
        }
    }
}