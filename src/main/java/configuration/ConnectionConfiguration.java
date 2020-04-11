package configuration;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;

public class ConnectionConfiguration {
    private Connection connection = null;
    private Session session = null;
    private Configuration configuration;

    public ConnectionConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public void createConfiguration() {
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(configuration.getBrokerAddress());
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        } catch (JMSException e) {
            System.out.println(e.getStackTrace());
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }
}
