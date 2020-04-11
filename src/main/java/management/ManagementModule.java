package management;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import configuration.Configuration;
import configuration.ConnectionConfiguration;
import connection.MessageProducerModule;
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
        if (userCommand.equals(Constants.EXIT_DELIMITER)) {
            result.put(true, "Exit program....");
            return result;
        } else if (userCommand.substring(0, Constants.NEW_KEY_PAIR.length()).equals(Constants.NEW_KEY_PAIR)) {
            String skey = cryptoModule.generateSecretKey();
            String pkey = String.valueOf(cryptoModule.raiseToPowWithNewOp(Long.parseLong(skey)));
            cryptoModule.saveKeyValue(pkey, PUBLIC_KEY);
            cryptoModule.saveKeyValue(skey, PRIVATE_KEY);
            result.put(false, "New secret and public keys were saved");
            return result;
        } else if (userCommand.substring(0, Constants.KEY_ROUTE_DELIMITER.length() + 1)
                .equals(Constants.KEY_ROUTE_DELIMITER + " ")) {
            String[] route = userCommand.substring(Constants.KEY_ROUTE_DELIMITER.length() + 1).split(ROUTE_DELIMITER);
            List<String> routeList = new LinkedList<>(Arrays.asList(route));
            String agentId = configuration.getAgentId();
            if (routeList.contains(agentId)) {
                routeList.remove(agentId);
            } else {
                result.put(false, "Wrong route: Route should contain current agent ID");
                return result;
            }
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
            return result;
        }
        result.put(false, "Unknown command");
        return result;
    }
}
