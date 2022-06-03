/*package agents;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;


import auxiliar.Constants;
import auxiliar.Utils;
import behaviours.ReceiveTwitchMessage;
import behaviours.SendMessage;
import models.ActionDataModeration;
import models.TwitchMessageHolder;

public class EmotionsProcessAgent extends Agent{
    
    private static final int TIMEOUT_DURATION = 60;
    
    protected TwitchMessageHolder holder;
    private ActionDataModeration actionData;
    private Set<String> swearWords;
    
    @Override
    protected void setup(){
        Utils.registerOneService(this, "procesador-de-emociones", "procesar-mensajes");
        
        holder = new TwitchMessageHolder();
        actionData = new ActionDataModeration();
        fillSwearWords();
        addBehaviour(new ReceiveTwitchMessage(this,holder));
        addBehaviour(new ProcessMessage());
        addBehaviour(new SendMessage(this,actionData));
    }
    
    private void fillSwearWords(){
        swearWords = new LinkedHashSet<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("src/main/resources/swearwords.txt"));
            String line = reader.readLine();
            while (line != null) {
                swearWords.add(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(reader != null) {
                try{ reader.close(); }catch(IOException e){}
            }
        }
    }
    
    private class ProcessMessage extends CyclicBehaviour {
        
        @Override
        public void action() {
            if (holder.getTwitchMessage() == null) return;
            int length = holder.getTwitchMessage().getMessage().split(" ").length;
            int ratio = ratioSwearWord(holder.getTwitchMessage().getMessage(),length);
            int ratioLimit;

            if (length <= 3){
                ratioLimit = 50;
            }else if (length <= 10){
                ratioLimit = 40;
            }else {
                ratioLimit = 30;
            }

            if (ratio >= ratioLimit){
                actionData.setAction(Constants.Code.DO_BAN);
                actionData.setModeration(Utils.twitchMessageToModeration(holder.getTwitchMessage()));
            }else if (ratio >= 10){
                actionData.setModeration(Utils.twitchMessageToModeration(holder.getTwitchMessage()));
                if (analisis(holder.getTwitchMessage().getMessage())){
                    actionData.setAction(Constants.Code.DO_BAN);
                }else {
                    actionData.setAction(Constants.Code.DO_TIMEOUT);
                    actionData.getModeration().setTimeoutDuration(TIMEOUT_DURATION);
                }
            }
            holder.setTwitchMessage(null); 
            
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
*/