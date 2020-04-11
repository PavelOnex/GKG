package connection;

import configuration.ConnectionConfiguration;

import javax.jms.*;

public class MessageProducerModule {

    private Queue sendQueue;

    private Connection connection;
    private Session session;
    private MessageProducer producer;

    public MessageProducerModule(String sendQueueName, ConnectionConfiguration connectionConfiguration) {
        try {
            session = connectionConfiguration.getSession();
            connection = connectionConfiguration.getConnection();
            sendQueue = session.createQueue(sendQueueName);
            producer = session.createProducer(sendQueue);

        } catch (JMSException e) {
            System.out.println(e.getStackTrace());
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    public void closeBroker() {
        try {
            connection.close();
            System.out.println("FINISH_MAIN");
        } catch (JMSException e) {
            System.out.println(e.getStackTrace());
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }

    }



    public void sendMessage(String message) {
        try {
            Message msg = null;
            msg = session.createTextMessage(message);
            System.out.println("Sending text '" + message + "'");
            producer.send(msg);
        } catch (JMSException e) {
            System.out.println(e.getLocalizedMessage());
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }
}

