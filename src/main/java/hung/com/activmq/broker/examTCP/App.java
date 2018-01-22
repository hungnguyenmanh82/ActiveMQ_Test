package hung.com.activmq.broker.examTCP;

import java.net.URI;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;

/**
 *Broker (or JMS provider) cần phải đc khởi tạo trc. Nó có thread rieng để xử lý.
 *  http://activemq.apache.org/tcp-transport-reference.html
 *  http://activemq.apache.org/uri-protocols.html 
 *
 */
public class App {

	public static void main(String[] args) throws Exception { 
		//================= create Broker (= JMS provider) tcp://localhost:1000
		BrokerService broker = new BrokerService();

		TransportConnector connector = new TransportConnector();
		// http://activemq.apache.org/tcp-transport-reference.html
		/**
		 *  tcp://hostname:port?key=value
		 *  vd:  tcp://localhost:61616?transport.threadName&transport.trace=false&transport.soTimeout=60000
		 */
		
		connector.setUri(new URI("tcp://localhost:1000"));
		
		broker.addConnector(connector);
		broker.start();

		//==================create consumer: tcp://localhost:1000 =========
		thread(new ConsumerAsync(), false); //server
		Thread.sleep(1000);

		//==================create Producer: tcp://localhost:1000 =========
		thread(new Producer(), false); //client blocking wait for server created
		thread(new Producer(), false); //client blocking wait for server created
		thread(new Producer(), false); //client 
	}

	public static void thread(Runnable runnable, boolean daemon) {
		Thread brokerThread = new Thread(runnable);
		brokerThread.setDaemon(daemon);
		brokerThread.start();
	}


}
