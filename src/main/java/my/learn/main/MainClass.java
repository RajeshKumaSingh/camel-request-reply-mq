package my.learn.main;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import my.learn.camel.routes.CamelReqResRoute;
import my.learn.jms.listener.JmsMessageListener;
import my.learn.util.Constants;

public class MainClass {

	private static final Logger LOGGER = LoggerFactory.getLogger(MainClass.class);

	public static void main(String[] args) throws URISyntaxException, Exception {

		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(Constants.ACTIVE_MQ_URL);

		// Starting Jms listener on locale active mq for n seconds
		long numberOfTimeUnit = 2;
		ExecutorService es = Executors.newSingleThreadExecutor();
		es.execute(() -> {
			JmsMessageListener jmsMessageListener = new JmsMessageListener(connectionFactory,
					Constants.REQUEST_QUEUE_NAME);
			try {
				jmsMessageListener.startJmsListner(numberOfTimeUnit);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		// Starting camel context

		CamelContext camelContext = new DefaultCamelContext();
		camelContext.addComponent("activemq", JmsComponent.jmsComponent(connectionFactory));

		try {
			camelContext.addRoutes(new CamelReqResRoute());
		} catch (Exception e) {
			e.printStackTrace();
		}

		camelContext.start();
		LOGGER.info("Started Camel Context");
		ProducerTemplate pt = camelContext.createProducerTemplate();

		// Submit request to queue via camel request-reply

		String str = null;
		Map<String, Object> headers = new HashMap<String, Object>();
		try {
			str = pt.requestBodyAndHeaders(Constants.DIRECT_REQ_RES_ROUTE, "Body Request", headers, String.class);
		} catch (Exception e) {
			str = "Better luck next time";
		}
		LOGGER.info("Message from req-reply queue: {}", str);

		camelContext.stop();
		
		LOGGER.info("Shuting down Jms listner ...");
		es.shutdownNow();
		
		LOGGER.info("Jms listner shutdown");
		LOGGER.info("The End");
	}

}