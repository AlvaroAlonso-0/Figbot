package agents;

import java.util.Properties;

import behaviours.ReceiveMessage;
import behaviours.SendMessage;
/*
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
*/
import jade.content.lang.sl.SLCodec;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import models.ActionData;
import models.TwitchMessageHolder;

public class EmotionsProcessAgent extends Agent{

    protected TwitchMessageHolder holder;
    private ActionData actionData;
 
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
        actionData = new ActionData();
        addBehaviour(new ReceiveMessage(this,holder));
        addBehaviour(new ProcessMessage());
        addBehaviour(new SendMessage(this,actionData));
    }

    private class ProcessMessage extends CyclicBehaviour {

        @Override
        public void action() {
            if (holder.getMessage() != null){
                //System.out.println(holder.getMessage());
                //analisis(holder.getMessage().getMessage());
                holder.setMessage(null); 
            }
        }

        /*
        private void analisis(String mensaje){
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
                String output = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
                if(!"Neutral".equals(output))
                    System.out.println(output);
            }
        }
        */
    }
}
