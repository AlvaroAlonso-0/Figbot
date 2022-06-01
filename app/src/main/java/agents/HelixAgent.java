package agents;

import java.io.IOException;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.helix.domain.StreamList;
import com.github.twitch4j.helix.domain.SubscriptionList;
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
    private Queue<ActionData> events;
    private String channel;
    private String channelID;
    private boolean moderationON = false;
    
    protected void setup(){
        Utils.registerService(this, "moderador-del-chat", "moderar-canal");
        Utils.registerService(this, "controlador-helix", "helix");
        
        oauth = Utils.generateCredential();
        events = new LinkedBlockingQueue<>();
        twitchClient = Utils.defaultTwitchBuilder(oauth)
        .withEnableHelix(true)
        .withEnablePubSub(true)
        .build();
        Object[] args = getArguments();
        channel = args[0].toString();
        channelID = args[1].toString();
        moderationON = Boolean.parseBoolean(args[2].toString());
        addBehaviour(new JoinChannels());
        if(moderationON){
            addBehaviour(new ReadModEvent());
            addBehaviour(new SendMessage());
        }
        addBehaviour(new ReceiveMessage());
        addBehaviour(new QueryAndInformHelix());
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
            ACLMessage msg;
            if((msg = receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST))) == null) return;
            if(!moderationON){
                refuseRequest(msg);
            }
            ActionDataModeration actionData = null;
            try {
                actionData = (ActionDataModeration) msg.getContentObject();
            } catch (UnreadableException e) {
                e.printStackTrace();
                refuseRequest(msg);
                return;
            }
            //TODO acciones helix
            System.out.println("ME HAN PREGUNTADO PARA BANEAR A " + actionData.getModeration().getTarget());

            switch(actionData.getAction()){
                case Constants.Code.DO_BAN: doBan(msg, actionData.getModeration()); break;
                case Constants.Code.DO_TIMEOUT:; doTimeout(msg, actionData.getModeration()); break;
                default: refuseRequest(msg);
            }
        }

        private void doBan(ACLMessage requestMessage, ModerationMessage mod){
            if(twitchClient.getChat().ban(channel, mod.getTarget(), "Figbot auto-ban: " + mod.getReason())){
                acceptRequest(requestMessage);
            }else{
                refuseRequest(requestMessage);
            }
            
        }

        private void doTimeout(ACLMessage requestMessage, ModerationMessage mod){
            if(twitchClient.getChat().timeout(channel, mod.getTarget(), Duration.ofSeconds(mod.getTimeoutDuration()), "Figbot auto-timeout: " + mod.getReason())){
                acceptRequest(requestMessage);
            }else{
                refuseRequest(requestMessage);
            }
        }

        private void acceptRequest(ACLMessage requestMessage){
            ACLMessage answer = requestMessage.createReply();
            answer.setPerformative(ACLMessage.AGREE);
            send(answer);
        }
        
        private void refuseRequest(ACLMessage requestMessage){
            ACLMessage answer = requestMessage.createReply();
            answer.setPerformative(ACLMessage.REFUSE);
            send(answer);
        }
    }
    
    public class QueryAndInformHelix extends CyclicBehaviour{
        
        @Override
        public void action() {
            ACLMessage msg;
            if((msg = receive(MessageTemplate.MatchPerformative(ACLMessage.QUERY_REF))) == null) return;
            
            String channelName = null;
            try {
                channelName = (String) msg.getContentObject();
            } catch (UnreadableException e) {
                e.printStackTrace();
            }
            ACLMessage answer = msg.createReply();
            answer.setPerformative(ACLMessage.INFORM_REF);
            answer.setContent(getHelixInfo(msg.getOntology(), channelName));
            send(answer);
        }
        
        private String getHelixInfo(String infoRequested, String channelName){
            if(channelName == null) return null;
            String info;
            switch(infoRequested){
                case "title": info = getStreamTitle(channelName); break;
                case "subs": info = getTotalSubs(channelName); break;
                default: info = null;
            }
            System.out.println("HELIX INFO: " + info);
            return info;
        }
        
        private String getStreamTitle(String channelName){
            System.out.println("voy a buscar el titulo");//TODO
            List<String> channelArgs = new LinkedList<>();
            channelArgs.add(channelName);
            StreamList resultList = twitchClient.getHelix().getStreams(null, null, null, null, null, null, channelArgs, null).execute();
            return resultList.getStreams().get(0).getTitle();
        }
        
        private String getTotalSubs(String channelName){
            System.out.println("Voy a buscar las subs");//TODO
            if(!channel.equals(channelName)) return null;
            SubscriptionList resultList = twitchClient.getHelix().getSubscriptions(Constants.Tokens.ACCESS_TOKEN, channelName, null, null,null).execute();
            return String.valueOf(resultList.getSubscriptions().size());
        }
        
        
    }
}
