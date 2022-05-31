package behaviours;

import java.io.IOException;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;

import auxiliar.Constants;
import auxiliar.Utils;
import models.ActionData;

public class SendMessage extends CyclicBehaviour{
    
    private ActionData actionData;
    
    public SendMessage(Agent agent, ActionData actionData){
        super(agent);
        this.actionData = actionData;
    }
    
    @Override
    public void action() {
        if (actionData.getAction() == Constants.Code.ERROR) return;
        
        AID[] processingAgents = null;
        try{
            DFAgentDescription[] result = DFService.search(myAgent, Utils.builDFAgentDescriptionFromType("visualizar-acciones"));
            processingAgents = new AID[result.length];
            for(int i=0; i<result.length; i++){
                processingAgents[i] = result[i].getName();
            }
        } catch(FIPAException fe){
            fe.printStackTrace();
        }

        if(processingAgents == null || processingAgents.length == 0) return;

        try {
            for(int i = 0; i < processingAgents.length; i++){
                myAgent.send(Utils.buildRequestMessage(processingAgents[i], actionData));
            }
            actionData.setAction(Constants.Code.ERROR);
        } catch (IOException e) {
            System.err.printf("No se pudo enviar el mensaje\n");
            e.printStackTrace();
        }
    }
}