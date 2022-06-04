package figbot;

import jade.core.Runtime;
import jade.util.ExtendedProperties;
import jade.util.leap.Properties;
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
        ExtendedProperties extendedProperties = new ExtendedProperties();
        extendedProperties.setProperty("gui", "true");
        container = runtime.createMainContainer(new ProfileImpl((Properties) extendedProperties));
        agents = new HashMap<>();
    }

    public void start(String channelName, String timeZone){
        try {
            String[] channelArgs = {channelName};
            String[] displayArgs = {timeZone};
            addDefaultAgents();
            addVariableDependantAgents(channelArgs, displayArgs);
            startAgents();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }

    public void turnOff(){
        try {
            turnOffAgents();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
    
    private void addDefaultAgents() throws StaleProxyException{
        agents.put("command", container.createNewAgent("procesadorCommand", "agents.CommandProcessAgent", null));
        agents.put("caps", container.createNewAgent("procesadorCaps", "agents.CapsProcessAgent", null));
        agents.put("emotion", container.createNewAgent("procesadorEmotions", "agents.EmotionsProcessAgent", null));
    }
    
    private void addVariableDependantAgents(String[] channelArgs, String[] displayArgs) throws StaleProxyException{
        agents.put("perception", container.createNewAgent("percepcion", "agents.PerceptionAgent", channelArgs));
        agents.put("helix", container.createNewAgent("moderador", "agents.HelixAgent", channelArgs));
        agents.put("display", container.createNewAgent("visualizacion", "agents.DisplayAgent", displayArgs));
        agents.put("loopAnnounce", container.createNewAgent("anunciante", "agents.LoopAnnounceAgent", channelArgs));
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
