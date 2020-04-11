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
        // 23^11 = 16
        // 23^8 = 20
        // 13^-1 = 16
        // mod = 137
        // k=31
        // m=62

        // aX^y
        System.out.println(cryptoModule.raiseAlphaXToPower(16, 8));
        // aY^x
        System.out.println(cryptoModule.raiseAlphaXToPower(20, 11));
//        System.out.println(cryptoModule.raiseToPowWithNewOp(23, 11));
//        System.out.println(cryptoModule.raiseToPowWithNewOp(23, 8));

        // Проверка верхнего (не робит)
        System.out.println(cryptoModule.newMultipleOperation("11", "8")); // 32
        System.out.println(cryptoModule.raiseToPowWithNewOp(32)); // 37

        // Тест 1
//        System.out.println(cryptoModule.raiseToPowWithNewOp(17));
//        System.out.println(cryptoModule.newMultipleOperation(String.valueOf(cryptoModule.raiseToPowWithNewOp(7)),
//                String.valueOf(cryptoModule.raiseToPowWithNewOp(10))));



        System.out.println(cryptoModule.raiseAlphaXToPower(16, 13));
        System.out.println(cryptoModule.raiseAlphaXToPower(124, 16));
        System.out.println(cryptoModule.raiseAlphaXToPower(16, 110));
        System.out.println(cryptoModule.raiseToPowWithNewOp(76, ArithmeticModule.modInverse(110, 137)));

        System.out.println(cryptoModule.getReverseMultiplyElement("13"));
//        System.out.println(cryptoModule.newMultipleOperation("1", "110"));



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
        System.out.println("VAL " + value);
        //Проверка
        System.out.println(cryptoModule.raiseToPowWithNewOp(alphaX, neutral));
//        for (int i=0; i < 200; i ++) {
//            if (cryptoModule.raiseToPowWithNewOp(alphaXK, i) == alphaX) {
//                System.out.println("Otvet :" + i);
//            }
//        }
        System.out.println(cryptoModule.raiseToPowWithNewOp(101, 110));

        System.out.println(cryptoModule.raiseToPowWithNewOp(value, Long.parseLong(cryptoModule.multiply("1",
                String.valueOf(ArithmeticModule.modInverse((int)cryptoModule.getMultiplyNeutral(), 137))))));




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
