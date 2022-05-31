package agents;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

import auxiliar.Constants;
import behaviours.ReceiveMessage;
import behaviours.SendMessage;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

import jade.content.lang.sl.SLCodec;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import models.ActionDataModeration;
import models.ModerationMessage;
import models.TwitchMessage;
import models.TwitchMessageHolder;

public class EmotionsProcessAgent extends Agent{

    private static final int TIMEOUT_DURATION = 60;

    protected TwitchMessageHolder holder;
    private ActionDataModeration actionData;
    private Set<String> swearWords;
 
    @Override
    protected void setup(){
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setName("procesador-de-comandos");
        sd.setType("procesar-mensajes");
        sd.addLanguages(new SLCodec().getName());
        dfd.addServices(sd);

        try{
            DFService.register(this, dfd);
        } catch(FIPAException e){
            System.err.println("Agent dead "+ this.getLocalName()+"\n\t"+e.getMessage());
        }
        holder = new TwitchMessageHolder();
        actionData = new ActionDataModeration();
        fillSwearWords();
        addBehaviour(new ReceiveMessage(this,holder));
        addBehaviour(new ProcessMessage());
        addBehaviour(new SendMessage(this,actionData));
    }

    private void fillSwearWords(){
        swearWords = new LinkedHashSet<>();
        BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader("src/main/resources/swearwords.txt"));
			String line = reader.readLine();
			while (line != null) {
				swearWords.add(line);
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    private class ProcessMessage extends CyclicBehaviour {

        @Override
        public void action() {
            if (holder.getMessage() != null){
                //System.out.println(holder.getMessage());
                int length = holder.getMessage().getMessage().split(" ").length;
                int ratio = ratioSwearWord(holder.getMessage().getMessage(),length);
                int ratioLimit;
                if (length <= 3){
                    ratioLimit = 50;
                }
                else if (length <= 10){
                    ratioLimit = 40;
                }
                else {
                    ratioLimit = 30;
                }
                if (ratio >= ratioLimit){
                    System.out.println("ban");
                    actionData.setAction(Constants.Code.DO_BAN);
                    actionData.setModeration(twitchMessage2ModerationMessage(holder.getMessage()));
                }
                else if (ratio >= 10){
                    if (analisis(holder.getMessage().getMessage())){
                        System.out.println("ban + analisis");
                        actionData.setAction(Constants.Code.DO_BAN);
                        actionData.setModeration(twitchMessage2ModerationMessage(holder.getMessage()));
                    }
                    else {
                        System.out.println("timeout");
                        actionData.setAction(Constants.Code.DO_TIMEOUT);
                        actionData.setModeration(twitchMessage2ModerationMessage(holder.getMessage()));
                        actionData.getModeration().setTimeoutDuration(TIMEOUT_DURATION);
                    }
                }
                holder.setMessage(null); 
            }
        }
        
        private ModerationMessage twitchMessage2ModerationMessage(TwitchMessage tm){
            ModerationMessage mm = new ModerationMessage();
            mm.setCreatedBy(Constants.BOT_NAME);
            mm.setTarget(tm.getUserName());
            mm.setReason(tm.getMessage());
            return mm;
        }

        private boolean analisis(String mensaje){
            Properties pipelineProps = new Properties();
            Properties tokenizerProps = new Properties();
            pipelineProps.setProperty("annotators", "parse, sentiment");
            pipelineProps.setProperty("parse.binaryTrees", "true");
            pipelineProps.setProperty("enforceRequirements", "false");
            tokenizerProps.setProperty("annotators", "tokenize ssplit");
            StanfordCoreNLP tokenizer = new StanfordCoreNLP(tokenizerProps);
            StanfordCoreNLP pipeline = new StanfordCoreNLP(pipelineProps);
            Annotation annotation = tokenizer.process(mensaje);
            pipeline.annotate(annotation);
            // normal output
            for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
                System.out.println(sentence.get(SentimentCoreAnnotations.SentimentClass.class));
                if("Very negative".equals(sentence.get(SentimentCoreAnnotations.SentimentClass.class))){
                    return true;
                }
            }
            return false;
        }

        private int ratioSwearWord(String message, int length){
            int nSwearWords = 0;
            for (String word : swearWords){
                if (message.contains(word+" ")) {
                    nSwearWords++;
                }
            }
            return nSwearWords*100/length;
        }

    }
}
