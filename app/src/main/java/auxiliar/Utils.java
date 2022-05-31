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

    public static OAuth2Credential generateCredential(){
        return new OAuth2Credential("twitch", Constants.Tokens.ACCESS_TOKEN);
    }

    public static TwitchClientBuilder defaultTwitchBuilder(OAuth2Credential credential){
        return TwitchClientBuilder.builder()
        .withEnableChat(true)
        .withChatAccount(credential)
        .withDefaultEventHandler(SimpleEventHandler.class);
    }
    
    public static void registerService(Agent agent, String name, String type){
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(agent.getAID());
        dfd.addServices(Utils.buildServiceDescription("moderador-del-chat", "moderar-canal"));
        
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
    
    public static ModerationMessage twitchMessageToModeration(TwitchMessage tm){
        ModerationMessage mm = new ModerationMessage();
        mm.setCreatedBy(Constants.BOT_NAME);
        mm.setTarget(tm.getUserName());
        mm.setReason(tm.getMessage());
        return mm;
    }
}
