package agents;

import com.github.twitch4j.TwitchClient;

import auxiliar.Utils;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

public class LoopAnnounceAgent extends Agent {

    private TwitchClient twitchClient;

    @Override
    protected void setup() {
        super.setup();
        twitchClient = Utils.defaultTwitchBuilder(Utils.generateCredential()).build();
        addBehaviour(new TickerBehaviour(this,300000){ // 5 minutes
            @Override
            protected void onTick() {
                twitchClient.getChat().sendAnnouncement(getArguments()[0].toString(), "Type f!help to find all commands available. Remember to behave politely with other users in chat!");
            }   
        });
    }

    
    
}
