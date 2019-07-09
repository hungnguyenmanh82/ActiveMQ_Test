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
 *  Broker là TCP server chứa các message dạng queue.
 *   Broker thường cài đặt bằng commandline là Java app và config bằng file.
 *
 */
public class App1_Brocker {

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
		// Consumer(or Subscriber) là TCP client wait event từ Brocker để nhận Message
		thread(new ConsumerAsync_runable(), false); //server
		Thread.sleep(1000);

		//==================create Producer: tcp://localhost:1000 =========
		// Producer (or Publisher) là TCP client  để send message tới Subscriber qua Brocker
		thread(new Producer_runable(), false); //client blocking wait for server created
		thread(new Producer_runable(), false); //client blocking wait for server created
		thread(new Producer_runable(), false); //client 
	}

	public static void thread(Runnable runnable, boolean daemon) {
		Thread brokerThread = new Thread(runnable);
		brokerThread.setDaemon(daemon);
		brokerThread.start();
	}


}
