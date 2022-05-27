package behaviours;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import models.TwitchMessage;
import models.TwitchMessageHolder;

// TODO
public class SendMessage extends CyclicBehaviour{

    private Agent myAgent;
    private TwitchMessageHolder holder;

    public SendMessage(Agent agent,TwitchMessageHolder holder){
        this.holder = holder;
        this.myAgent = agent;
    }
        
    @Override
    public void action() {

    }
}