package agents;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.core.EventManager;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

import jade.content.lang.sl.SLCodec;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;


public class AgentePercepcion extends Agent{
    
    private TwitchClient twitchClient;
    private String[] channels;
    private Queue<ChannelMessageEvent> messages;
    
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
        .withChatAccount(new OAuth2Credential("twitch", "tg10oz4dgkypcs8ofa7swvohoiey3t"))
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
                twitchClient.getChat().sendMessage(channels[i], "Ya estoy aquí");
            }
        }
    }
    
    private class ReadMessage extends OneShotBehaviour {
        @Override
        public void action() {
            EventManager eventManager = twitchClient.getEventManager();
            eventManager.onEvent(ChannelMessageEvent.class, event -> {
                System.out.println("[" + event.getChannel().getName() + "] " + event.getUser().getName() + ": " + event.getMessage());//TODO
                messages.add(event);
                doWake();
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
            
            ChannelMessageEvent message = messages.poll();
          
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.addReceiver(myAgent.getAID());
            //msg.setContent(message);
            send(msg);
        }
    }
}