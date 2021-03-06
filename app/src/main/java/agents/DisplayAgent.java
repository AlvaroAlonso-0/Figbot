package agents;

import java.io.IOException;
import java.time.LocalTime;
import java.time.ZoneId;

import com.github.twitch4j.TwitchClient;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import auxiliar.Constants;
import auxiliar.Utils;
import models.ActionData;
import models.ActionDataMessage;
import models.ActionDataModeration;
import models.DisplayInfo;
import figbot.App;

public class DisplayAgent extends Agent {
    
    private ActionData actionData;
    private String timeZone;
    
    @Override
    protected void setup(){
        Utils.registerOneService(this, "visualizador-de-acciones", "visualizar-acciones");
        
        timeZone = getArguments()[0].toString();
        addBehaviour(new ReceiveMessage());
        addBehaviour(new SendChatToTwitch());
        addBehaviour(new ChatToGUI());
        addBehaviour(new Reset());
    }
    
    public class ReceiveMessage extends CyclicBehaviour{
        
        @Override
        public void action() {
            ACLMessage msg = blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
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
            twitchClient = Utils.defaultTwitchBuilder(Utils.generateCredential()).build();
        }
        
        @Override
        public void action() {
            if (actionData.getAction() >= Constants.Code.BAN || actionData.getAction() == Constants.Code.ERROR ) return;
            String message;
            switch(actionData.getAction()/100){
                case 1: message = userResponse(actionData.getAction()%100); break;
                case 2: message = dice(actionData.getAction()%100); break;
                case 3: message = streamInfo(actionData.getAction()%100); break;
                case 4: message = help(); break;
                default: return;
            }
            if(message != null){
                twitchClient.getChat().sendMessage(((ActionDataMessage) actionData).getMessage().getChannelName(), message);
            }
        }

        private String help(){
            String res = " -------------------------------------" + 
                         "------------------f!greetings [user]--------------" +
                         "------------------f!shoutout [user]---------------" +
                         "--------------------f!dice [sides]----------------" +
                         "------------------------f!time--------------------" +
                         "--------------------f!video [user]----------------" +
                         "------------------------f!title-------------------" +
                         "--------------------------------------------------";
            return res;
        }
        
        private String userResponse(int code) {
            ActionDataMessage message = (ActionDataMessage) actionData;
            int n = (int)(Math.random()*GREETINGS_MESSAGES.length);
            String res;
            switch(code/10){
                case 0: res = String.format(GREETINGS_MESSAGES[n], code%10 == 1 ? (message.getMessage().getUserName()) : message.getArgument()); break;
                case 1: res = String.format("Check out @%s channel! -> twitch.tv/%s", message.getArgument(), message.getArgument()); break;
                case 2: res = String.format("Please @%s, don't use that much caps!",message.getMessage().getUserName()); break;
                default: res = "";
            }
            return res;
        }
        
        
        private String dice(int i) {
            ActionDataMessage message = (ActionDataMessage) actionData;
            int sides = i==1 ? 6 : Integer.parseInt(message.getArgument());
            return String.format("@%s, you got the number %s", message.getMessage().getUserName(), String.valueOf(1+(int)(Math.random()*sides)));
        }
        
        private String streamInfo(int code){
            ActionDataMessage message = (ActionDataMessage) actionData;
            String res;
            switch(code){
                case 1: res = getLocalTimeMessage(message.getMessage().getChannelName()); break;   
                case 11: res = getVideoURL(message.getArgument()); break;
                case 21: res = getTitleMessage(message.getMessage().getChannelName()); break;
                default: res = null;    
            }
            return res;
        }

        private String getLocalTimeMessage(String channelName){
            LocalTime now = LocalTime.now(ZoneId.of(timeZone));
            return String.format("@%s local time is %02d:%02d", channelName, now.getHour(), now.getMinute());
        }

        private String getVideoURL(String channelName){
            AID[] helixAgents = null;
            try{
                DFAgentDescription[] result = DFService.search(myAgent, Utils.builDFAgentDescriptionFromType("helix"));
                helixAgents = new AID[result.length];
                for(int i=0; i<result.length; i++){
                    helixAgents[i] = result[i].getName();
                }
            } catch(FIPAException fe){
                fe.printStackTrace();
            }

            if(helixAgents == null || helixAgents.length == 0) return null;
            ACLMessage msg = null;
            try {
                for(int i = 0; i < helixAgents.length; i++){

                    send(Utils.buildInformMessage(helixAgents[i], Constants.Commands.VIDEO, channelName));
                    msg = blockingReceive(MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.INFORM_REF), MessageTemplate.MatchPerformative(ACLMessage.REFUSE)));
                    if(msg.getPerformative() == ACLMessage.INFORM_REF) break;
                }
            } catch (IOException e) {
                System.err.println("No se pudo enviar el mensaje");
                e.printStackTrace();
            }
            return (msg == null || msg.getContent() == null) ? null : String.format("@%s's last video! -> %s", channelName, msg.getContent());
        }
 
        private String getTitleMessage(String channelName){
            AID[] helixAgents = null;
            try{
                DFAgentDescription[] result = DFService.search(myAgent, Utils.builDFAgentDescriptionFromType("helix"));
                helixAgents = new AID[result.length];
                for(int i=0; i<result.length; i++){
                    helixAgents[i] = result[i].getName();
                }
            } catch(FIPAException fe){
                fe.printStackTrace();
            }

            if(helixAgents == null || helixAgents.length == 0) return null;
            ACLMessage msg = null;
            try {
                for(int i = 0; i < helixAgents.length; i++){

                    send(Utils.buildInformMessage(helixAgents[i], Constants.Commands.TITLE, channelName));
                    msg = blockingReceive(MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.INFORM_REF), MessageTemplate.MatchPerformative(ACLMessage.REFUSE)));
                    if(msg.getPerformative() == ACLMessage.INFORM_REF) break;
                }
            } catch (IOException e) {
                System.err.println("No se pudo enviar el mensaje");
                e.printStackTrace();
            }
            return (msg == null || msg.getContent() == null) ? null : String.format("@%s is streaming %s", channelName, msg.getContent());
        }
    }
    
    public class ChatToGUI extends CyclicBehaviour {
        
        @Override
        public void action() {
            if (actionData.getAction() < Constants.Code.BAN || actionData.getAction() == Constants.Code.ERROR) return;
            ActionDataModeration mod = (ActionDataModeration) actionData;
            if(mod.getAction()/10 == 91){
                if(!askHelix()) return;
                mod.setAction(mod.getAction()-10);
            }
            DisplayInfo displayInfo;
            switch(mod.getAction()){
                case Constants.Code.BAN: displayInfo = buildTARGET(mod,Constants.Message.BAN);break;
                case Constants.Code.UNBAN: displayInfo = buildTARGET(mod, Constants.Message.UNBAN);break;
                case Constants.Code.TIMEOUT: displayInfo = buildTIMEOUT(mod);break;
                case Constants.Code.DELETE: displayInfo = buildTARGET(mod,Constants.Message.DELETE);break;
                case Constants.Code.SLOW: displayInfo = buildSLOW(mod, Constants.Message.SLOW);break;
                case Constants.Code.SLOW_OFF: displayInfo = buildSLOW(mod, Constants.Message.SLOW_OFF);break;
                default: return;
            }
            App.getController().displayModerationEvent(displayInfo);
        }

        private DisplayInfo buildTARGET(ActionDataModeration mod, String actionMessage){
            String message = String.format("%s %s %s", mod.getModeration().getCreatedBy(), actionMessage, mod.getModeration().getTarget());
            if(mod.getModeration().getReason() != null){
                return new DisplayInfo(mod.getAction(), message, mod.getModeration().getReason());
            }
            return new DisplayInfo(mod.getAction(), message);
        }

        private DisplayInfo buildTIMEOUT(ActionDataModeration mod){
            String message = String.format("%s %s %s for %d seconds", mod.getModeration().getCreatedBy(), Constants.Message.TIMEOUT, mod.getModeration().getTarget(), mod.getModeration().getTimeoutDuration());
            if(mod.getModeration().getReason() != null){
                return new DisplayInfo(mod.getAction(), message , mod.getModeration().getReason());
            }
            return new DisplayInfo(mod.getAction(), message);
        }

        private DisplayInfo buildSLOW(ActionDataModeration mod, String actionMessage){
            return new DisplayInfo(mod.getAction(), String.format("%s %s", mod.getModeration().getCreatedBy(), actionMessage));
        }
        
        private boolean askHelix(){
            AID[] helixAgents = null;
            try{
                DFAgentDescription[] result = DFService.search(myAgent, Utils.builDFAgentDescriptionFromType("moderar-canal"));
                helixAgents = new AID[result.length];
                for(int i=0; i<result.length; i++){
                    helixAgents[i] = result[i].getName();
                }
            } catch(FIPAException fe){
                fe.printStackTrace();
            }

            if(helixAgents == null || helixAgents.length == 0) return false; // add log
            try {
                for(int i = 0; i < helixAgents.length; i++){
                    send(Utils.buildRequestMessage(helixAgents[i], actionData));
                    ACLMessage msg = blockingReceive(MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.AGREE), MessageTemplate.MatchPerformative(ACLMessage.REFUSE)));
                    if(msg.getPerformative() == ACLMessage.AGREE){
                        return true;
                    }
                }
            } catch (IOException e) {
                System.err.println("No se pudo enviar el mensaje");
                e.printStackTrace();
            }
            return false;
        }
    }
    
    public class Reset extends CyclicBehaviour{
        
        @Override
        public void action() {
            actionData.setAction(Constants.Code.ERROR);
        }
    }
}