package figbot;

import jade.core.Runtime;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import java.util.HashMap;
import java.util.Map;

public class Figbot {
    
    AgentContainer container;
    Map<String,AgentController> agents;

    public Figbot() {
        Runtime runtime = jade.core.Runtime.instance();
        container = runtime.createMainContainer(new ProfileImpl());
        agents = new HashMap<>();
    }

    public void start(String channelName){
        try {
            String[] channelArgs = {channelName};
            String[] displayArgs = {"Europe/Paris"};//TODO
            addDefaultAgents();
            addVariableDependantAgents(channelArgs, displayArgs);
            startAgents();
        } catch (StaleProxyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void turnOff(){
        try {
            turnOffAgents();
        } catch (StaleProxyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private void addDefaultAgents() throws StaleProxyException{
        agents.put("command", container.createNewAgent("procesadorCommand", "agents.CommandProcessAgent", null));
        agents.put("caps", container.createNewAgent("procesadorCaps", "agents.CapsProcessAgent", null));
        agents.put("emotion", container.createNewAgent("procesadorEmotions", "agents.EmotionsProcessAgent", null));
    }
    
    private void addVariableDependantAgents(String[] channelArgs, String[] displayArgs) throws StaleProxyException{
        agents.put("percepcion", container.createNewAgent("percepcion", "agents.PerceptionAgent", channelArgs));
        agents.put("helix", container.createNewAgent("moderador", "agents.HelixAgent", channelArgs));
        agents.put("display", container.createNewAgent("visualizacion", "agents.DisplayAgent", displayArgs));
    }
    
    private void startAgents() throws StaleProxyException{
        for(AgentController agent : agents.values()){
            agent.start();
        }
    }

    private void turnOffAgents() throws StaleProxyException{
        for(String agentName : agents.keySet()){
            agents.get(agentName).kill();
            agents.remove(agentName);
        }
    }
}
