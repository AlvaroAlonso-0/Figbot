package agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import models.ActionData;
import models.ActionDataMessage;
import models.ActionDataModeration;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import java.time.LocalTime;
import java.time.ZoneId;

import models.TwitchMessage;
import auxiliar.Constants;
import jade.content.lang.sl.SLCodec;

public class DisplayAgent extends Agent {

    private ActionData actionData;
    private String timeZone = "Europe/Paris";

    @Override
    protected void setup(){
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setName("visualizador-de-acciones");
        sd.setType("visualizar-acciones");
        sd.addLanguages(new SLCodec().getName());
        dfd.addServices(sd);
        try{
            DFService.register(this, dfd);
        } catch(FIPAException e){
            System.err.println("Agent dead "+ this.getLocalName()+"\n\t"+e.getMessage());
        }
        addBehaviour(new ReceiveMessage());
        addBehaviour(new SendChatToTwitch());
        //addBehaviour(new SendMessage(this,actionData));
    }

    public class ReceiveMessage extends CyclicBehaviour{
            
        @Override
        public void action() {
            ACLMessage msg = myAgent.blockingReceive(MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST), MessageTemplate.MatchOntology("ontologia")));
            try {
                 actionData = (ActionData)msg.getContentObject();
            } catch (UnreadableException e) {
                e.printStackTrace();
            }
        }
    }

    public class SendChatToTwitch extends CyclicBehaviour{

        private final String [] GREETINGS_MESSAGES = {"Greetings @%s! Enjoy the show", "Hello @%s! Have a good time watching the stream", "What's up @%s? Thank you for watching the live"}; 
        private TwitchClient twitchClient;

        public SendChatToTwitch(){
            twitchClient = TwitchClientBuilder.builder()
            .withEnableChat(true)
            .withChatAccount(new OAuth2Credential("twitch", Constants.Tokens.ACCESS_TOKEN))
            .withDefaultEventHandler(SimpleEventHandler.class)
            .build();
        }


        @Override
        public void action() {
            if (actionData.getAction() == Constants.Code.ERROR) return;
            switch(actionData.getAction()/100){
                case 1:
                    writeOnChat(userResponse(actionData.getAction()%100));
                    break;
                case 2:
                    writeOnChat(dice(actionData.getAction()%100));
                    break;
                case 3:
                    writeOnChat(streamInfo(actionData.getAction()%100));
                    break;
                case 9:
                    System.out.println(((ActionDataModeration) actionData).getMessage());
            }
        }

        private void writeOnChat(String message){
            twitchClient.getChat().sendMessage( ((ActionDataMessage) actionData).getMessage().getChannelName(), message);
        }


        private String userResponse(int code) {
            int n = (int)(Math.random()*GREETINGS_MESSAGES.length);
            String res;
            ActionDataMessage message = (ActionDataMessage) actionData;
            switch(code/10){
                case 0: res = String.format(GREETINGS_MESSAGES[n], code%10 == 1 ? (message.getMessage().getUserName()) : message.getArgument()); break;
                case 1: res = String.format("Check out @%s channel! -> twitch.tv/%s", message.getArgument(), message.getArgument()); break;
                case 2: res = String.format("Please @%s, do not use a lot of caps",message.getMessage().getUserName()); break;
                default: res = "";
            }
            return res;
        }


        private String dice(int i) {
            ActionDataMessage message = (ActionDataMessage) actionData;
            int sides = i==1 ? 6 : Integer.parseInt(message.getArgument());
            return String.format("@%s, you got the number %s",message.getMessage().getUserName(), String.valueOf(1+(int)(Math.random()*sides)));
        }

        private String streamInfo(int code){
            String res;
            ActionDataMessage message = (ActionDataMessage) actionData;
            switch(code){
                case 1:
                    LocalTime now = LocalTime.now(ZoneId.of(timeZone));
                    res = String.format("@%s Local time is %d:%d",message.getMessage().getChannelName(),now.getHour(),now.getMinute());
                    break;   
                case 11:
                default:
                    res = "";
                    break;      
              }
            return res;
        }
    }

    public class SendChatToGUI extends CyclicBehaviour {

        @Override
        public void action() {
            
        }
        
    }
}