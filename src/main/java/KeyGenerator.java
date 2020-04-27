import configuration.Configuration;
import configuration.ConnectionConfiguration;
import connection.MessageConsumerModule;
import management.ManagementModule;

import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class KeyGenerator {
    //maven clean + install; Run with "java -jar ..."

    public static void main(String[] args) {
        Scanner userCommand = new Scanner(System.in);
        Configuration configuration = new Configuration("config.properties");
        configuration.getConfiguration();
        ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(configuration);
        connectionConfiguration.createConfiguration();
        BlockingQueue<String> queue = new LinkedBlockingQueue<>(10);
        MessageConsumerModule messageConsumerModule = new MessageConsumerModule(queue, configuration, connectionConfiguration);
        messageConsumerModule.start();
        ManagementModule managementModule = new ManagementModule(queue, configuration, connectionConfiguration);

        while (true) {
            System.out.println("Enter command:");
            String command = userCommand.nextLine();  // Read user input
            Map<Boolean, String> result = managementModule.processUserCommand(command);
            System.out.println(result.entrySet().iterator().next().getValue());
            if (result.entrySet().iterator().next().getKey()) {
                messageConsumerModule.finishReceiving();
                break;
            }
        }
        try {
            messageConsumerModule.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
