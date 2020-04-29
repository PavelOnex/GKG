package constants;

public enum CommandType {
    EXIT_COMMAND("exit", "exit - Quits program."),
    ROUTE_COMMAND("route", "route <command args> - Starts key generations. " +
            "Should pass clients IDs for key generation process to command arguments.\n Example: \"route ag1 ag2 ag3\"."),
    INIT_KEY_PAIR_COMMAND("new pair", "new pair - Creates new public and private keys for current" +
            "client."),
    PUBLISH_PUB_KEY_COMMAND("publish key", "publish key - Sends public key value to all agents in group."),
    AGENT_INFO_COMMAND("info", "info - Shows current agent information."),
    HELP_COMMAND("help", "help - Shows supported console commands.");
    private String commandName;
    private String description;
    CommandType(String commandName, String description) {
        this.commandName = commandName;
        this.description = description;
    }
    public String getCommandName() {
        return commandName;
    }
    public String getDescription() {
        return description;
    }
}
