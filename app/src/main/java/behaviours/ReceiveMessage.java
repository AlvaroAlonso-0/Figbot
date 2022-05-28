package behaviours;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import models.TwitchMessage;
import models.TwitchMessageHolder;

public class ReceiveMessage extends CyclicBehaviour{

    private Agent myAgent;
    private TwitchMessageHolder holder;

    public ReceiveMessage(Agent agent,TwitchMessageHolder holder){
        this.holder = holder;
        this.myAgent = agent;
    }
        
    @Override
    public void action() {
        ACLMessage msg = myAgent.blockingReceive(MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST), MessageTemplate.MatchOntology("ontologia")));
        try {
             holder.setMessage((TwitchMessage)msg.getContentObject());
        } catch (UnreadableException e) {
            e.printStackTrace();
        }
    }
}