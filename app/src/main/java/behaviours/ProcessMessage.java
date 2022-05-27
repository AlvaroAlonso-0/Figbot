package behaviours;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;

public class ProcessMessage extends OneShotBehaviour{
    
    private Agent myAgent;
    public ProcessMessage(Agent agente){
        myAgent = agente;
    }
    @Override
	public void action(){
        //Recive messsage
        System.out.println("El mensaje recibido es -> ");
    }
}