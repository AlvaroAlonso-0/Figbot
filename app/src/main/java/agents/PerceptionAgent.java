package agents;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.core.EventManager;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

import auxiliar.Constants;
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


public class PerceptionAgent extends Agent{
    
    private TwitchClient twitchClient;
    private String[] channels;
    private Queue<ChannelMessageEvent> messages;
    public static final String BOT_NAME = "figb0t";
    
    protected void setup(){
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        
        ServiceDescription sd = new ServiceDescription();
        sd.setName("lector-de-chat");
        sd.setType("leer-mensajes");
        sd.addLanguages(new SLCodec().getName());
        dfd.addServices(sd);
        
        try{
            DFService.register(this, dfd);
        } catch(FIPAException e){
            System.err.println("Agent dead xd "+ this.getLocalName()+"\n\t"+e.getMessage());
        }
        
        twitchClient = TwitchClientBuilder.builder()
        .withEnableChat(true)
        .withChatAccount(new OAuth2Credential("twitch", Constants.Tokens.ACCESS_TOKEN))
        .withDefaultEventHandler(SimpleEventHandler.class)
        .build();
        
        Object[] args = getArguments();
        channels = new String[args.length];
        for(int i = 0; i < args.length; i++){
            channels[i] = args[i].toString();
        }
        messages = new LinkedBlockingQueue<>();
        
        addBehaviour(new JoinChannels());
        addBehaviour(new ReadMessage());
        addBehaviour(new SendMessage(this));
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
    
    private class ReadMessage extends OneShotBehaviour {
        @Override
        public void action() {
            EventManager eventManager = twitchClient.getEventManager();
            eventManager.onEvent(ChannelMessageEvent.class, event -> {
                if (!event.getUser().getName().equals(BOT_NAME)) {
                    messages.add(event);
                    System.out.println("[" + event.getChannel().getName() + "] " + event.getUser().getName() + ": " + event.getMessage()); // TODO
                    doWake();
                }
            });           
        }
    }
    
    private class SendMessage extends CyclicBehaviour{
        private Agent myAgent;
        
        public SendMessage(Agent myAgent){
            this.myAgent = myAgent;
        }
        
        @Override
        public void action() {
            if (messages.isEmpty()){ 
                doWait();
                return;
            }
            
            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType("procesar-mensajes");
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
                        ChannelMessageEvent cme = messages.peek();
                        msg.setContentObject(new TwitchMessage(cme.getChannel().getName(), cme.getUser().getName(), cme.getMessage()));
                        send(msg);
                    }
                    messages.poll();
                } catch (IOException e) {
                    System.err.printf("No se pudo enviar el mensaje\n");
                    e.printStackTrace();
                }
        }
    }
}