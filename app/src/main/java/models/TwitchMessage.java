package models;

import jade.util.leap.Serializable;

public class TwitchMessage implements Serializable{
    
    private static final long serialVersionUID = 84534545L;
    private String channelName;
    private String userName;
    private String message;

    public TwitchMessage(String channelName, String userName, String message) {
        this.channelName = channelName;
        this.userName = userName;
        this.message = message;
    }
    public String getChannelName() {
        return channelName;
    }
    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    
    @Override
    public String toString() {
        return "TwitchMessage [channelName=" + channelName + ", message=" + message + ", userName=" + userName + "]";
    }

}
