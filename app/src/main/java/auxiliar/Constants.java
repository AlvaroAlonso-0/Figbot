package auxiliar;

import java.util.TimeZone;
import java.awt.Color;

public class Constants {

    public static final String BOT_NAME = "figb0t";    

    public class Tokens{
        public static final String ACCESS_TOKEN = "9p3i0m418ephpqv464pdr5kedz5tws" ;
        public static final String REFRESH_TOKEN = "owcjg805hinr8j1lfivqbnw3srokca0mvqscqwo53e4i89pciq";
        public static final String CLIENT_ID = "gp762nuuoqcoxypju8c569th9wz7q5";
        public static final String USER_ID = "797442019";
    }

    public class Commands{
        public static final String GREETINGS = "greetings"; // default @yo , f!welcome @user
        public static final String SHOUTOUT = "shoutout"; //f!shoutout @user
        public static final String DICE = "dice"; //default 6, f!dice n
        public static final String TIME = "time"; //f!hour -> streamer local time
        public static final String VIDEO = "video"; //f!subs -> number of subs
        public static final String TITLE = "title"; //f!title -> title of the stream
        public static final String HELP = "help";   //f!help -> list of commands
    }

    public class Code {
        public static final int GREETINGS_SELF = 101;
        public static final int GREETINGS_USER = 102; 
        public static final int SHOUTOUT = 111;
        public static final int CAPS_ALERT = 121;
        public static final int DICE_DEFAULT = 201; 
        public static final int DICE_N = 202; 
        public static final int TIME = 301;
        public static final int VIDEO = 311;
        public static final int TITLE = 321;
        public static final int HELP = 401;
        public static final int BAN = 900;
        public static final int UNBAN = 901;
        public static final int TIMEOUT = 902;
        public static final int DELETE = 905;
        public static final int SLOW = 906;
        public static final int SLOW_OFF = 907;
        public static final int DO_BAN = 910;
        public static final int DO_TIMEOUT = 912;
        
        public static final int ERROR = 0;
    }

    public class Message{
        public static final String BAN = "has banned";
        public static final String UNBAN = "has unbanned";
        public static final String TIMEOUT = "has timeouted";
        public static final String DELETE = "deleted the message from";
        public static final String SLOW = "slowed the chat";
        public static final String SLOW_OFF = "disabled slow mode";
    }

    public static class Times{
        public static final String [] FORBIDDEN = {"CET", "CST6CDT","EET", "EST5EDT", "GB", "GB-Eire", "Greenwich", "MET", "MST7MDT", "NZ", "NZ-CHAT", "PRC", "PST8PDT", "ROK", "UCT", "UTC", "Universal", "W-SU", "WET"};
        public static final String [] CITIES = TimeZone.getAvailableIDs();
    }

    public static class Colors {

        public static final Color BACKGROUND = new Color(171,184,195);

        public static final Color BAN = new Color(235,20,76);   //ROJO
        public static final Color UNBAN = new Color(00,208,132);//VERDE
        public static final Color TIMEOUT = new Color(255,111,0);//NARANJA
        public static final Color INFO = new Color(255,235,59); // AMARILLO
    }

}
