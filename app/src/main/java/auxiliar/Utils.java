package auxiliar;

import java.io.IOException;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClientBuilder;

import jade.content.lang.sl.SLCodec;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.ACLMessage;
import jade.util.leap.Serializable;
import models.ModerationMessage;
import models.TwitchMessage;

public class Utils {

    private static final int LINE_LENGTH = 250;

    public static OAuth2Credential generateCredential(){
        return new OAuth2Credential("twitch", Constants.Tokens.ACCESS_TOKEN);
    }

    public static TwitchClientBuilder defaultTwitchBuilder(OAuth2Credential credential){
        return TwitchClientBuilder.builder()
        .withEnableChat(true)
        .withChatAccount(credential)
        .withDefaultEventHandler(SimpleEventHandler.class);
    }
    
    public static void registerOneService(Agent agent, String name, String type){
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(agent.getAID());
        dfd.addServices(Utils.buildServiceDescription(name, type));
        
        try{
            DFService.register(agent, dfd);
        } catch(FIPAException e){
            System.err.println("Agent dead xd "+ agent.getLocalName()+"\n\t"+e.getMessage());
        }
    }

    public static void registerTwoServices(Agent agent, String name1, String type1, String name2, String type2){
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(agent.getAID());
        dfd.addServices(buildServiceDescription(name1, type1));
        dfd.addServices(buildServiceDescription(name2, type2));
        
        try{
            DFService.register(agent, dfd);
        } catch(FIPAException e){
            System.err.println("Agent dead xd "+ agent.getLocalName()+"\n\t"+e.getMessage());
        }
    }
    
    public static ServiceDescription buildServiceDescription(String name, String type){
        ServiceDescription sd = new ServiceDescription();
        sd.setName(name);
        sd.setType(type);
        sd.addLanguages(new SLCodec().getName());
        return sd;
    }
    
    public static DFAgentDescription builDFAgentDescriptionFromType(String type){
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(type);
        template.addServices(sd);
        return template;
    }
    
    public static ACLMessage buildRequestMessage(AID receiver, Serializable content) throws IOException{
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(receiver);
        msg.setOntology("ontologia");
        msg.setLanguage(new SLCodec().getName());
        msg.setEnvelope(new Envelope());
        msg.getEnvelope().setPayloadEncoding("ISO8859_1");
        msg.setContentObject(content);
        return msg;
    }

    public static ACLMessage buildInformMessage(AID receiver, String ontology, String content) throws IOException{
        ACLMessage msg = new ACLMessage(ACLMessage.QUERY_REF);
        msg.addReceiver(receiver);
        msg.setOntology(ontology);
        msg.setLanguage(new SLCodec().getName());
        msg.setEnvelope(new Envelope());
        msg.getEnvelope().setPayloadEncoding("ISO8859_1");
        msg.setContentObject(content);
        return msg;
    }
    
    public static ModerationMessage twitchMessageToModeration(TwitchMessage tm){
        ModerationMessage mm = new ModerationMessage();
        mm.setCreatedBy(Constants.BOT_NAME);
        mm.setTarget(tm.getUserName());
        mm.setReason(tm.getMessage());
        return mm;
    }

    public static String getDisplayFormat(String origin){
        int lines = 1 + origin.length()/LINE_LENGTH;
        StringBuilder displayFormat = new StringBuilder("<html>");
        for (int i = 0; i < lines-1; i++) {
            displayFormat.append(origin.substring(LINE_LENGTH*i, LINE_LENGTH*(i+1)));
            displayFormat.append("<br>");
        }
        displayFormat.append(origin.substring(LINE_LENGTH*(lines-1)));
        displayFormat.append("</html>");
        return displayFormat.toString();
    }
}
