package auxiliar;

public class Constants {

    public class Tokens{
        public static final String ACCESS_TOKEN = "aarmv79586b3u9qg6bo5zygkcrzp1a" ;
        public static final String REFRESH_TOKEN = "6kua5gujpqcz31p7z60994f5wx5636awnyp6st83sgaph8eulb";
        public static final String CLIENT_ID = "gp762nuuoqcoxypju8c569th9wz7q5";
        public static final String USER_ID = "797442019";
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
    
}
