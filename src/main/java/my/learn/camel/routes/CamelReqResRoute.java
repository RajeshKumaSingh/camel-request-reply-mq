package my.learn.camel.routes;

import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;

import my.learn.util.Constants;

public class CamelReqResRoute extends RouteBuilder {

	@Override
	public void configure(){
		from(Constants.DIRECT_REQ_RES_ROUTE)
		.log("REQUEST BODY: ${body}")
		.log("REQUEST HEADERS: ${headers}")
		.setExchangePattern(ExchangePattern.InOut)
		.to(reqReplyStr());
	}
	
	private static String reqReplyStr() {
		StringBuilder sb = new StringBuilder();
		sb.append("activemq:queue:");
		sb.append(Constants.REQUEST_QUEUE_NAME);
		sb.append("?replyTo=");
		sb.append(Constants.RESPONSE_QUEUE_NAME);
		sb.append("&");
		sb.append("useMessageIDAsCorrelationID=true");
		/* 
		By using useMessageIDAsCorrelationID=true property, JMSCorrelationId will be null, 
		receiver has to set correlation is using JMSMessageId.
		
		If this property is not used then camel will send a JMSCorrelationId,
		receiver can use that as correlation id.
		 */
		return sb.toString();
		
	}

}