package agents;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.github.philippheuer.events4j.core.EventManager;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;

import auxiliar.Constants;
import auxiliar.Utils;
import models.TwitchMessage;


public class PerceptionAgent extends Agent{
    
    private TwitchClient twitchClient;
    private String channel;
    private Queue<ChannelMessageEvent> messages;
    
    protected void setup(){
        Utils.registerService(this, "lector-de-chat", "leer-mensajes");
        
        twitchClient = Utils.defaultTwitchBuilder(Utils.generateCredential()).build();
        messages = new LinkedBlockingQueue<>();
        
        Object[] args = getArguments();
        channel = args[0].toString();
        
        addBehaviour(new JoinChannels());
        addBehaviour(new ReadMessage());
        addBehaviour(new SendMessage());
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
    
    private class ReadMessage extends OneShotBehaviour {
        @Override
        public void action() {
            EventManager eventManager = twitchClient.getEventManager();
            eventManager.onEvent(ChannelMessageEvent.class, event -> {
                if (!Constants.BOT_NAME.equals(event.getUser().getName())) {
                    messages.add(event);
                    System.out.println("[" + event.getChannel().getName() + "] " + event.getUser().getName() + ": " + event.getMessage()); // TODO
                    doWake();
                }
            });           
        }
    }
    
    private class SendMessage extends CyclicBehaviour{
        
        @Override
        public void action() {
            if (messages.isEmpty()){ 
                doWait();
                return;
            }
            
            AID[] processingAgents = null;
            try{
                DFAgentDescription[] result = DFService.search(myAgent, Utils.builDFAgentDescriptionFromType("procesar-mensajes"));
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
                    ChannelMessageEvent cme = messages.peek();
                    TwitchMessage tm = new TwitchMessage(cme.getChannel().getName(), cme.getUser().getName(), cme.getMessage());
                    send(Utils.buildRequestMessage(processingAgents[i], tm));
                }
                messages.poll();
            } catch (IOException e) {
                System.err.printf("No se pudo enviar el mensaje\n");
                e.printStackTrace();
            }
        }
    }
}