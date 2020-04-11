import configuration.Configuration;
import configuration.ConnectionConfiguration;
import connection.MessageConsumerModule;
import management.ManagementModule;
import math.ArithmeticModule;
import math.CryptoModule;

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
//        ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(configuration);
//        connectionConfiguration.createConfiguration();

        CryptoModule cryptoModule = new CryptoModule(configuration);

        System.out.println("**************TEST**********");


        System.out.println(cryptoModule.raiseToPowWithNewOp(7, 110)); //result 101
        System.out.println(cryptoModule.raiseToPowWithNewOp(101, cryptoModule.newMultipleOperation("1", "110")));

        //Надо найти alphaX
        long K = 10;
        long alphaX = 55;
        long module = Long.parseLong(configuration.getModule());
        long neutral = cryptoModule.getMultiplyNeutral();
        System.out.println("Neutral " + cryptoModule.getMultiplyNeutral());
        long alphaXK = cryptoModule.raiseToPowWithNewOp(alphaX, K); //125
        long value = cryptoModule.raiseToPowWithNewOp(alphaXK, Long.parseLong(cryptoModule.multiply(String.valueOf(cryptoModule.getMultiplyNeutral()),
                String.valueOf(ArithmeticModule.modInverse((int)K, (int)module)))));
        //Сточка ниже - alphaX^e
        long val = cryptoModule.raiseToPowWithNewOp(value, cryptoModule.newMultipleOperation("1", String.valueOf(cryptoModule.getMultiplyNeutral())));
        System.out.println("VAL " + val);
        //Проверка
        System.out.println(cryptoModule.raiseToPowWithNewOp(alphaX, neutral));
//        for (int i=0; i < 137; i ++) {
//            if (cryptoModule.raiseToPowWithNewOp(alphaXK, i) == alphaX) {
//                System.out.println("Otvet :" + i);
//            }
//        }
        System.out.println(cryptoModule.raiseToPowWithNewOp(101, 110));

        System.out.println(cryptoModule.raiseToPowWithNewOp(value, Long.parseLong(cryptoModule.multiply("1",
                String.valueOf(ArithmeticModule.modInverse((int)cryptoModule.getMultiplyNeutral(), 137))))));

//        int K = 25;
//        long alphaX = 7; //Надо найти
//        long alphaXK = cryptoModule.raiseToPowWithNewOp(alphaX, K);
//        System.out.println("AlphaXK: " + alphaXK);
//        String reverseK = cryptoModule.getReverseMultiplyElement(String.valueOf(K));
//        String mult = cryptoModule.multiply(String.valueOf(K), reverseK);
//        cryptoModule.getReverseMultiplyElement(mult);
//        System.out.println("Test: " + cryptoModule.getReverseMultiplyElement("85"));
//        System.out.println("Test1: " + ArithmeticModule.modInverse(85, 137));
//        long value = cryptoModule.raiseToPowWithNewOp(alphaXK, Long.parseLong(reverseK));
//        System.out.println("Value: " + cryptoModule.raiseToPowWithNewOp(alphaXK, Long.parseLong(cryptoModule.getReverseMultiplyElement(mult))));
//        int exponent = ArithmeticModule.modInverse(Integer.parseInt(cryptoModule.multiply(reverseK, String.valueOf(K))), 137);
//        //long result = cryptoModule.raiseToPowWithNewOp(value, cryptoModule.newMultipleOperation("1", String.valueOf(cryptoModule.getMultiplyNeutral())));
//        long result = cryptoModule.raiseToPowWithNewOp(alphaXK, ArithmeticModule.modInverse(K, 137));
//
//        System.out.println("AlphaX: " + result);


//        BlockingQueue<String> queue = new LinkedBlockingQueue<>(10);
//        MessageConsumerModule messageConsumerModule = new MessageConsumerModule(queue, configuration, connectionConfiguration);
//        messageConsumerModule.start();
//        ManagementModule managementModule = new ManagementModule(queue, configuration, connectionConfiguration);
//
//        while (true) {
//            System.out.println("Enter command:");
//            String command = userCommand.nextLine();  // Read user input
//            Map<Boolean, String> result = managementModule.processUserCommand(command);
//            System.out.println(result.entrySet().iterator().next().getValue());
//            if (result.entrySet().iterator().next().getKey()) {
//                messageConsumerModule.finishReceiving();
//                break;
//            }
//        }
//        try {
//            messageConsumerModule.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}
