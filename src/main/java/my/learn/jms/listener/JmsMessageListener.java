package my.learn.jms.listener;

import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JmsMessageListener {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JmsMessageListener.class);
	
	ConnectionFactory connectionFactory;
	String reqQueue;
	
	public JmsMessageListener(ConnectionFactory connectionFactory, String requsetqueuename) {
		this.connectionFactory = connectionFactory;
		this.reqQueue = requsetqueuename;
	}

	public void startJmsListner(long numberOfTimeUnit) throws URISyntaxException, JMSException {
		
		Connection connection = null;

		try {
			connection = connectionFactory.createConnection();

			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Queue queue = session.createQueue(reqQueue);

			// Consumer
			MessageConsumer consumer = session.createConsumer(queue);
			consumer.setMessageListener(new ConsumerMessageListener("Consumer", session));

			connection.start();
			LOGGER.info("Starting JMS Listener for {} numberOfTimeUnit", numberOfTimeUnit);
			
			TimeUnit.SECONDS.sleep(numberOfTimeUnit);
			
			session.close();
			LOGGER.info("Colsed JMS session");
			connection.close();
			LOGGER.info("Colsed JMS Listener");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}
}
