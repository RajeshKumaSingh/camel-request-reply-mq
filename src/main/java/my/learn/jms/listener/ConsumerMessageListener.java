package my.learn.jms.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerMessageListener implements MessageListener {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerMessageListener.class);

	String consumerName;
	Session session;

	public ConsumerMessageListener(String consumerName, Session session) {
		this.consumerName = consumerName;
		this.session = session;
	}

	public void onMessage(Message message) {

		TextMessage requestMessage = null;
		Message replyMessage = null;
		MessageProducer producer = null;

		if (message instanceof TextMessage) {
			requestMessage = (TextMessage) message;
			try {
				LOGGER.info("Consumer {} received message: {}", Thread.currentThread().getName(),requestMessage.getText());
				replyMessage = session.createTextMessage("WITH RESPONSE: " + requestMessage.getText());

				if (requestMessage.getJMSCorrelationID() != null) {
					LOGGER.info("Consumer JMSCorrelationID: {}", requestMessage.getJMSCorrelationID());
					replyMessage.setJMSCorrelationID(requestMessage.getJMSCorrelationID());
				} else {
					LOGGER.info("Consumer JMSMessageId: {}", requestMessage.getJMSMessageID());
					replyMessage.setJMSCorrelationID(requestMessage.getJMSMessageID());
				}

				LOGGER.info("Consumer JMSReplyTo {}",requestMessage.getJMSReplyTo());

				producer = session.createProducer(requestMessage.getJMSReplyTo());
				producer.send(replyMessage);

			} catch (JMSException e) {
				e.printStackTrace();
			}
		}

	}

}
