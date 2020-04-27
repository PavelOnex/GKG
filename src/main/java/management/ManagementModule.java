package management;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import configuration.Configuration;
import configuration.ConnectionConfiguration;
import configuration.PublicKeys;
import connection.MessageProducerModule;
import constants.CommandType;
import constants.Constants;
import dto.MessageDto;
import math.CryptoModule;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

import static constants.Constants.KEY.PRIVATE_KEY;
import static constants.Constants.KEY.PUBLIC_KEY;

public class ManagementModule {
    private final BlockingQueue<String> queue;
    private Configuration configuration;
    private CryptoModule cryptoModule;
    private ConnectionConfiguration connectionConfiguration;
    private static final String ROUTE_DELIMITER = " ";
    public ManagementModule(BlockingQueue<String> queue, Configuration configuration,
                            ConnectionConfiguration connectionConfiguration) {
        this.queue = queue;
        this.configuration = configuration;
        this.connectionConfiguration = connectionConfiguration;

        cryptoModule = new CryptoModule(configuration);
    }


    public Map<Boolean, String> processUserCommand(String userCommand) {
        Map<Boolean, String> result = new HashMap<>();
        // exit command
        if (userCommand.equals(CommandType.EXIT_COMMAND.getCommandName())) {
            result.put(true, "Exit program....");
            return result;
        }
        // new pair command
        if (userCommand.length() >= CommandType.INIT_KEY_PAIR_COMMAND.getCommandName().length()) {
            if (userCommand.substring(0, CommandType.INIT_KEY_PAIR_COMMAND.getCommandName().length())
                .equals(CommandType.INIT_KEY_PAIR_COMMAND.getCommandName())) {
                String skey = cryptoModule.generateSecretKey();
                String pkey = String.valueOf(cryptoModule.raiseToPowWithNewOp(Long.parseLong(skey)));
                cryptoModule.saveKeyValue(pkey, PUBLIC_KEY);
                cryptoModule.saveKeyValue(skey, PRIVATE_KEY);
                result.put(false, "New secret and public keys were saved");
                return result;
            }
        }
        // route command
        if (userCommand.length() > CommandType.ROUTE_COMMAND.getCommandName().length()) {
            if (userCommand.substring(0, CommandType.ROUTE_COMMAND.getCommandName().length() + 1)
                    .equals(CommandType.ROUTE_COMMAND.getCommandName() + " ")) {
                String[] route = userCommand.substring(CommandType.ROUTE_COMMAND.getCommandName().length() + 1).split(ROUTE_DELIMITER);
                List<String> routeList = new LinkedList<>(Arrays.asList(route));
                String agentId = configuration.getAgentId();
                if (routeList.contains(agentId)) {
                    routeList.remove(agentId);
                } else {
                    result.put(false, "Wrong route: Route should contain current agent ID");
                    processHelpCommand();
                    return result;
                }
                if (!routeList.isEmpty()) {
                    String r = cryptoModule.generateSecretKey();
                    //String constant = cryptoModule.raiseAlphaToPower(r);
                    String constant = String.valueOf(cryptoModule.raiseToPowWithNewOp(Long.parseLong(r)));

                    Map<String, String> map = new HashMap<>();
                    map.put(agentId, constant);
                    MessageDto messageDto = new MessageDto();
                    messageDto.setMessageHeader(Constants.GENERATION_STEP_HEADER);
                    messageDto.setSenderId(agentId);
                    messageDto.setKeyRoutePassed(agentId);
                    messageDto.setKeyRouteRemaining(routeList.stream().map(n -> String.valueOf(n))
                            .collect(Collectors.joining(" ")));
                    messageDto.setRecipientId(routeList.get(0));
                    messageDto.setConstantsMap(map);
                    ObjectMapper mapper = new ObjectMapper();
                    String messageToSend = "";
                    try {
                        messageToSend = mapper.writeValueAsString(messageDto);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    try {
                        queue.put(r);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    MessageProducerModule messageProducerModule = new MessageProducerModule(messageDto.getRecipientId(), connectionConfiguration);
                    messageProducerModule.sendMessage(messageToSend);
                    result.put(false, "Key generation was started");
                } else {
                    result.put(false, "Wrong route: Route doesn't contains any other clients");
                }
                return result;
            }
        }
        // publish key command
        if (userCommand.equals(CommandType.PUBLISH_PUB_KEY_COMMAND.getCommandName())) {
            String pubKey = cryptoModule.loadKeyValue(PUBLIC_KEY);
            PublicKeys publicKeys = new PublicKeys();
            publicKeys.init();
            Map<String, String> map = publicKeys.getAgentIdToPublicKey();
            map.put(configuration.getAgentId(), pubKey);
            String mapAsString = map.keySet().stream()
                    .map(key -> key + PublicKeys.VALUES_DELIMITER + map.get(key))
                    .collect(Collectors.joining(", "));
            publicKeys.refreshPKeys(mapAsString);
            MessageDto messageDto = new MessageDto();
            messageDto.setMessageHeader(Constants.INIT_HEADER);
            messageDto.setSenderId(configuration.getAgentId());
            Map<String, String> constantsMap = new HashMap<>();
            constantsMap.put(configuration.getAgentId(), pubKey);
            messageDto.setConstantsMap(constantsMap);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (!entry.getValue().equals(configuration.getAgentId())) {
                    MessageProducerModule messageProducerModule = new MessageProducerModule(entry.getKey(), connectionConfiguration);
                    messageDto.setRecipientId(entry.getKey());
                    ObjectMapper mapper = new ObjectMapper();
                    String messageToSend = "";
                    try {
                        messageToSend = mapper.writeValueAsString(messageDto);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    messageProducerModule.sendMessage(messageToSend);
                }
            }

            result.put(false, "Key published");
            return result;
        }
        result.put(false, "Error: Unknown command");
        processHelpCommand();
        return result;
    }
    private void processHelpCommand() {
        System.out.println(Constants.COMMANDS_LIST);
        System.out.println(CommandType.AGENT_INFO_COMMAND.getDescription());
        System.out.println(CommandType.EXIT_COMMAND.getDescription());
        System.out.println(CommandType.ROUTE_COMMAND.getDescription());
        System.out.println(CommandType.INIT_KEY_PAIR_COMMAND.getDescription());
        System.out.println(CommandType.PUBLISH_PUB_KEY_COMMAND.getDescription());
    }
}
