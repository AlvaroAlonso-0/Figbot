package agents;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.pubsub.events.ChatModerationEvent;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.*;

import auxiliar.Constants;
import auxiliar.Utils;
import models.ActionData;
import models.ActionDataModeration;
import models.ModerationMessage;

public class HelixAgent extends Agent{
    private TwitchClient twitchClient;
    private OAuth2Credential oauth;
    private String channel;
    private Queue<ActionData> events;
    private String channelID = "98803007";
    
    protected void setup(){
        Utils.registerService(this, "moderador-del-chat", "moderar-canal");
        
        oauth = Utils.generateCredential();
        events = new LinkedBlockingQueue<>();
        twitchClient = Utils.defaultTwitchBuilder(oauth)
        .withEnableHelix(true)
        .withEnablePubSub(true)
        .build();
        Object[] args = getArguments();
        channel = args[0].toString();
        
        addBehaviour(new JoinChannels());
        addBehaviour(new ReadModEvent());
        addBehaviour(new SendMessage());
        addBehaviour(new ReceiveMessage());
    }
    
    @Override
    protected void takeDown() {
        super.takeDown();
        twitchClient.getChat().leaveChannel(channel);
        twitchClient.close();
    }
    
    private class JoinChannels extends OneShotBehaviour{
        @Override
        public void action(){
            twitchClient.getChat().joinChannel(channel);
        }
    }
    
    private class ReadModEvent extends OneShotBehaviour {
        @Override
        public void action() {
            twitchClient.getPubSub().listenForModerationEvents(oauth, Constants.Tokens.USER_ID, channelID);
            twitchClient.getEventManager().onEvent(ChatModerationEvent.class, event -> {
                if (Constants.BOT_NAME.equals(event.getData().getCreatedBy())) return;
                
                System.out.println("Se ha realizado un: " + event.getData().getModerationAction().ordinal());//TODO
                
                events.add(buildActionDataModerationFromEvent(event));
            });
        }
        
        private ActionDataModeration buildActionDataModerationFromEvent(ChatModerationEvent event){
            ActionDataModeration mod = new ActionDataModeration();
            mod.setAction(900 + event.getData().getModerationAction().ordinal());
            ModerationMessage message = new ModerationMessage();
            message.setCreatedBy(event.getData().getCreatedBy());
            if (!event.getData().getReason().isEmpty()){
                message.setReason(event.getData().getReason().get());
            }
            if (!event.getData().getTargetedUserName().isEmpty()){
                message.setTarget(event.getData().getTargetedUserName().get());
            }
            if (event.getData().getModerationAction().ordinal() == 2){
                message.setTimeoutDuration(event.getData().getTimeoutDuration().isEmpty() ? 600 : event.getData().getTimeoutDuration().getAsInt());
            }
            mod.setModeration(message);
            return mod;
        }
    }
    
    public class SendMessage extends CyclicBehaviour{
        
        @Override
        public void action() {
            ActionData actionData;
            if(events.isEmpty() || (actionData = events.poll()).getAction() == Constants.Code.ERROR) return;
        
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
    
    public class ReceiveMessage extends CyclicBehaviour{
        
        @Override
        public void action() {
            ACLMessage msg = blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
            ActionDataModeration actionData = null;
            try {
                actionData = (ActionDataModeration) msg.getContentObject();
            } catch (UnreadableException e) {
                e.printStackTrace();
                return;
            }
            //TODO
            System.out.println("ME HAN PREGUNTADO PARA BANEAR A " + actionData.getModeration().getTarget());
            ACLMessage answer = msg.createReply();
            answer.setPerformative(ACLMessage.REFUSE);
            send(answer);
        }
    }
}
