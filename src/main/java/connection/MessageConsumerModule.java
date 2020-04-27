package connection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import configuration.Configuration;
import configuration.ConnectionConfiguration;
import javax.jms.*;
import javax.jms.Queue;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import configuration.PublicKeys;
import dto.MessageDto;
import math.CryptoModule;

import static constants.Constants.*;

public class MessageConsumerModule extends Thread {
    private MessageConsumer consumer;
    private Session session;
    private Connection connection;
    private Thread queueThread;
    private Configuration configuration;
    private ConnectionConfiguration connectionConfiguration;
    private CryptoModule cryptoModule;
    private final BlockingQueue<String> queue;
    final AtomicBoolean finishFlag = new AtomicBoolean(false);
    private String cryptoValue;

    public MessageConsumerModule(BlockingQueue<String> queue, Configuration configuration, ConnectionConfiguration connectionConfiguration) {
        this.queue = queue;
        this.cryptoModule = new CryptoModule(configuration);
        this.session = connectionConfiguration.getSession();
        this.connection = connectionConfiguration.getConnection();
        this.connectionConfiguration = connectionConfiguration;
        this.configuration = configuration;
        //cryptoValue = new ArrayList<>();
        Queue receiveQueue = null;
        try {
            receiveQueue = session.createQueue(configuration.getAgentId());
        } catch (JMSException e) {
            e.printStackTrace();
        }
        try {
            consumer = session.createConsumer(receiveQueue);
            connection.start();
        } catch (JMSException e) {
            e.printStackTrace();
        }
        queueThread = new Thread() {
            public void run(){
                while (!finishFlag.get()) {
                    try {
                        String value = queue.poll(1, TimeUnit.SECONDS);
                        if (value != null) {
                            //cryptoValue.add(value);

                            cryptoValue = value;

                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        queueThread.start();

    }
    public void run() {
        TextMessage textMsg = null;
        try {
            System.out.println("Started receiving");
            while (true) {
                textMsg = (TextMessage) consumer.receive(1000);
                if (textMsg != null) {
                    System.out.println("Received: " + textMsg.getText());
                    processMessage(textMsg.getText());
                } else {
                    if (finishFlag.get())
                        break;

                }
            }
            closeBroker();
            queueThread.join();
        } catch (JMSException e) {
            System.out.println(e.getStackTrace());
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }
    private void closeBroker() {
        try {
            session.close();
            connection.close();
            System.out.println("FINISH");
        } catch (JMSException e) {
            System.out.println(e.getStackTrace());
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }
    public void finishReceiving() {
        finishFlag.compareAndSet(false, true);
    }

    public void processMessage(String message) {
        MessageDto messageDto = null;
        try {
            messageDto = new ObjectMapper().readValue(message, MessageDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        String agentId = configuration.getAgentId();
        MessageDto replyMessage = new MessageDto();
        if (messageDto != null) {
            if (messageDto.getRecipientId().equals(agentId)) {
                if (messageDto.getMessageHeader().equals(GENERATION_STEP_HEADER)) {

                    String r = cryptoModule.generateSecretKey();
                    cryptoValue = r;
                    Map<String, String> constantsMap = messageDto.getConstantsMap();
                    int maxExponentsCount = 0;
                    String maxExponentKey = "";
                    for (Map.Entry<String,String> entry : constantsMap.entrySet()) {
                        String[] exponents = entry.getKey().split(" ");
                        int exponentCount = exponents.length;
                        if (exponentCount > maxExponentsCount) {
                            maxExponentsCount = exponentCount;
                            maxExponentKey = entry.getKey();
                        }
                    }
                    String[] routePassed = messageDto.getKeyRoutePassed().split(" ");

                    if (messageDto.getKeyRouteRemaining().equals(agentId)) {
                        replyMessage.setMessageHeader(LAST_STEP_HEADER);
                        replyMessage.setKeyRouteRemaining("none");
                        replyMessage.setSenderId(agentId);
                        replyMessage.setKeyRoutePassed(messageDto.getKeyRoutePassed() + " " + agentId);
                        //String key = cryptoModule.raiseValueToPower(constantsMap.get(maxExponentKey), r);
                        String key = String.valueOf(cryptoModule.raiseToPowWithNewOp(Long.parseLong(constantsMap.get(maxExponentKey)),
                                Long.parseLong(r)));
                        for (String agent : routePassed) {
                            String[] split = maxExponentKey.split(" ");
                            List<String> list = new ArrayList<String>(Arrays.asList(split));
                            String valueForAgent, keyForAgent;
                            if(list.size() != 1) {
                                list.remove(agent);
                                String result = list.stream().map(n -> String.valueOf(n)).collect(Collectors.joining(" "));
                                //valueForAgent = cryptoModule.raiseValueToPower(constantsMap.get(result), r);
                                valueForAgent = String.valueOf(cryptoModule.raiseToPowWithNewOp(Long.parseLong(constantsMap.get(result)),
                                        Long.parseLong(r)));
                                keyForAgent = result + " " + agentId;
                            } else {
                                //valueForAgent = cryptoModule.raiseValueToPower(r);
                                valueForAgent = String.valueOf(cryptoModule.raiseToPowWithNewOp(Long.parseLong(r)));
                                keyForAgent = agentId;
                            }
                            Map<String, String> map = new HashMap<>();
                            map.put(keyForAgent, valueForAgent);
                            replyMessage.setConstantsMap(map);
                            replyMessage.setRecipientId(agent);
                            ObjectMapper mapper = new ObjectMapper();
                            String messageToSend = "";
                            try {
                                messageToSend = mapper.writeValueAsString(replyMessage);
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                            MessageProducerModule messageProducerModule = new MessageProducerModule(replyMessage.getRecipientId(),
                                    connectionConfiguration);
                            messageProducerModule.sendMessage(messageToSend);
                        }

                        System.out.println("FINAL KEY:" + key);
                    } else {
                        replyMessage.setMessageHeader(GENERATION_STEP_HEADER);
                        replyMessage.setSenderId(agentId);
                        Map<String, String> map = new HashMap<>();
                        String[] split = maxExponentKey.split(" ");
                        List<String> list = new ArrayList<String>(Arrays.asList(split));
                        if (list.size() != 1) {
                            for (String agent : routePassed) {
                                split = maxExponentKey.split(" ");
                                list = new ArrayList<String>(Arrays.asList(split));
                                list.remove(agent);
                                String result = list.stream().map(n -> String.valueOf(n)).collect(Collectors.joining(" "));
                                //String valueForAgent = cryptoModule.raiseValueToPower(constantsMap.get(result), r);
                                String valueForAgent = String.valueOf(cryptoModule.raiseToPowWithNewOp(Long.parseLong(constantsMap.get(result)),
                                        Long.parseLong(r)));

                                map.put(result + " " + agentId, valueForAgent);
                            }
                            map.put(maxExponentKey, constantsMap.get(maxExponentKey));
                            map.put(maxExponentKey + " " + agentId, String.valueOf(cryptoModule.raiseToPowWithNewOp(Long.parseLong(constantsMap.get(maxExponentKey)),
                                    Long.parseLong(r))));
                        } else {
                            map.put(list.get(0), constantsMap.get(list.get(0)));
                            //map.put(agentId, cryptoModule.raiseValueToPower(r));
                            //map.put(list.get(0) + " " + agentId, cryptoModule.raiseValueToPower(constantsMap.get(list.get(0)), r));
                            map.put(agentId, String.valueOf(cryptoModule.raiseToPowWithNewOp(Long.parseLong(r))));
                            map.put(list.get(0) + " " + agentId, String.valueOf(cryptoModule
                                    .raiseToPowWithNewOp(Long.parseLong(constantsMap.get(list.get(0))), Long.parseLong(r))));
                        }
                        replyMessage.setConstantsMap(map);
                        replyMessage.setKeyRoutePassed(messageDto.getKeyRoutePassed() + " " + agentId);
                        String routeRemained = messageDto.getKeyRouteRemaining().replace(agentId + " ", "");
                        String[] remained = routeRemained.split(" ");
                        replyMessage.setKeyRouteRemaining(routeRemained);
                        replyMessage.setRecipientId(remained[0]);

                        ObjectMapper mapper = new ObjectMapper();
                        String messageToSend = "";
                        try {
                            messageToSend = mapper.writeValueAsString(replyMessage);
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                        MessageProducerModule messageProducerModule = new MessageProducerModule(replyMessage.getRecipientId(),
                                connectionConfiguration);
                        messageProducerModule.sendMessage(messageToSend);

                    }

                } else if (messageDto.getMessageHeader().equals(LAST_STEP_HEADER)) {
                    String value = messageDto.getConstantsMap().entrySet().iterator().next().getValue();
                    //String key = cryptoModule.raiseValueToPower(value, cryptoValue);
                    String key = String.valueOf(cryptoModule.raiseToPowWithNewOp(Long.parseLong(value), Long.parseLong(cryptoValue)));
                    System.out.println("FINAL KEY:" + key);
                } else if (messageDto.getMessageHeader().equals(INIT_HEADER)) {
                    String value = messageDto.getConstantsMap().entrySet().iterator().next().getValue();
                    PublicKeys publicKeys = new PublicKeys();
                    publicKeys.init();
                    Map<String, String> map = publicKeys.getAgentIdToPublicKey();
                    map.put(messageDto.getSenderId(), value);
                    String mapAsString = map.keySet().stream()
                            .map(key -> key + PublicKeys.VALUES_DELIMITER + map.get(key))
                            .collect(Collectors.joining(", "));
                    publicKeys.refreshPKeys(mapAsString);
                } else {
                    System.out.println("ERROR: Received unknown command");
                }
            } else {
                System.out.println("Wrong recipient");
            }
        }
    }

}
