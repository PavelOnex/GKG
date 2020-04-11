package constants;

public class Constants {
    //Message headers
    public static final String INIT_HEADER = "init_message";
    public static final String GENERATION_STEP_HEADER = "generation_step_header";
    public static final String LAST_STEP_HEADER = "last_step_header";

    //Command delimiters
    public static final String KEY_ROUTE_DELIMITER = "route";
    public static final String EXIT_DELIMITER = "exit";
    public static final String NEW_KEY_PAIR = "new pair";

    //Keys
    public enum KEY {
        PUBLIC_KEY,
        PRIVATE_KEY
    }
}
