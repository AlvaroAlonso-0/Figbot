package agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;

public class Agent1 extends Agent{

    protected CyclicBehaviour cyclicBehaviour;
    
    public void setup(){
        System.out.println("Soy el Agente 1");
        cyclicBehaviour = new CyclicBehaviour(this){
            public void action(){
                block();
            }
        };
        addBehaviour(cyclicBehaviour);
    }
}
