package auxiliar;

public class Constants {

    public class Tokens{
        public static final String ACCESS_TOKEN = "tg10oz4dgkypcs8ofa7swvohoiey3t" ;
        public static final String REFRESH_TOKEN = "dbprutdn0jt098vaktfg7wumj6qhzyeccemr6jws4f18fuwtm7";
        public static final String CLIENT_ID = "gp762nuuoqcoxypju8c569th9wz7q5";
    }

    public class Commands{
        public static final String GREETINGS = "greetings"; // default @yo , f!welcome @user
        public static final String SHOUTOUT = "shoutout"; //f!shoutout @user
        public static final String DICE = "dice"; //default 6, f!dice n
        public static final String TIME = "time"; //f!hour -> streamer local time
        public static final String SUBS = "subs"; //f!subs -> number of subs
        public static final String TITLE = "title"; //f!title -> title of the stream
    }

    public class Code {
        public static final int GREETINGS_SELF = 101;
        public static final int GREETINGS_USER = 102; 
        public static final int SHOUTOUT = 111;
        public static final int CAPS_ALERT = 121;
        public static final int DICE_DEFAULT = 201; 
        public static final int DICE_N = 202; 
        public static final int TIME = 301;
        public static final int SUBS = 311;
        public static final int TITLE = 321;
        
        public static final int ERROR = 0;
    }
    
}
