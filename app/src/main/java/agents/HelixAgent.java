package agents;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.core.EventManager;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.pubsub.events.ChatModerationEvent;

import auxiliar.Constants;
import behaviours.SendMessage;
import jade.content.lang.sl.SLCodec;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Envelope;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import models.TwitchMessage;
import models.ActionData;
import models.ActionDataMessage;
import models.ActionDataModeration;
import models.ModerationMessage;

public class HelixAgent extends Agent{
    private TwitchClient twitchClient;
    private OAuth2Credential oauth;
    private String[] channels;
    private Queue<ActionData> events;

    protected void setup(){
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        
        ServiceDescription sd = new ServiceDescription();
        sd.setName("moderador-del-chat");
        sd.setType("moderar-canal");
        sd.addLanguages(new SLCodec().getName());
        dfd.addServices(sd);
        
        try{
            DFService.register(this, dfd);
        } catch(FIPAException e){
            System.err.println("Agent dead xd "+ this.getLocalName()+"\n\t"+e.getMessage());
        }
        oauth = new OAuth2Credential("twitch", Constants.Tokens.ACCESS_TOKEN);
        twitchClient = TwitchClientBuilder.builder()
        .withEnableChat(true)
        .withChatAccount(oauth)
        .withDefaultEventHandler(SimpleEventHandler.class)
        .withEnableHelix(true)
        .withEnablePubSub(true)
        .build();
        events = new LinkedBlockingQueue<>();
        Object[] args = getArguments();
        channels = new String[args.length];
        for(int i = 0; i < args.length; i++){
            channels[i] = args[i].toString();
        }
        
        addBehaviour(new JoinChannels());
        addBehaviour(new ReadModEvent());
        addBehaviour(new SendMessage());
    }
    
    @Override
    protected void takeDown() {
        super.takeDown();
        for (int i = 0; i < channels.length; i++){
            twitchClient.getChat().leaveChannel(channels[i]);
        }
        twitchClient.close();
    }
    
    private class JoinChannels extends OneShotBehaviour{
        @Override
        public void action(){
            for (int i = 0; i < channels.length; i++){
                twitchClient.getChat().joinChannel(channels[i]);
            }
        }
    }
    
    private class ReadModEvent extends OneShotBehaviour {
        @Override
        public void action() {
            twitchClient.getPubSub().listenForModerationEvents(oauth, Constants.Tokens.USER_ID, "98803007");
            twitchClient.getEventManager().onEvent(ChatModerationEvent.class, event -> {
                System.out.println("Se ha realizado un: " + event.getData().getModerationAction().ordinal());
                ActionDataModeration mod = new ActionDataModeration();
                ModerationMessage message = new ModerationMessage();
                mod.setAction(900 + event.getData().getModerationAction().ordinal());
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
                mod.setMessage(message);
                events.add(mod);
            });
        }
    }

    public class SendMessage extends CyclicBehaviour{
        
        @Override
        public void action() {
            if(events.isEmpty()) return;
            ActionData actionData = events.poll();
            if (actionData.getAction() == Constants.Code.ERROR) return;
            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType("visualizar-acciones");
            template.addServices(sd);
            AID[] processingAgents = null;
            try{
                DFAgentDescription[] result = DFService.search(myAgent, template);
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
                    ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                    msg.addReceiver(processingAgents[i]);
                    msg.setOntology("ontologia");
                    msg.setLanguage(new SLCodec().getName());
                    msg.setEnvelope(new Envelope());
                    msg.getEnvelope().setPayloadEncoding("ISO8859_1");
                    msg.setContentObject(actionData);
                    myAgent.send(msg);
                }
                actionData.setAction(Constants.Code.ERROR);
            } catch (IOException e) {
                System.err.printf("No se pudo enviar el mensaje\n");
                e.printStackTrace();
            }
        }
    }
}
