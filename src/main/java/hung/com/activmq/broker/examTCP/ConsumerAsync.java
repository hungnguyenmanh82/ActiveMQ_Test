package hung.com.activmq.broker.examTCP;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * Consumer = receiver: bên nhận event.
 * producer = sender: bên gửi
 * asynchronous event on the only thread by ActiveMQ
 * 
 * @Runnable:  dùng để chạy function này trên worker thread thôi
 */
public class ConsumerAsync implements Runnable, ExceptionListener {

	private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	public void run() {
		System.out.println("Consumer: thread=" + Thread.currentThread().getId());
		try {
			//================================  ==========
			// vm://localhost: là giao thức trong nội bộ Java Virtual Machine
			//khởi tạo Broker đây là địa chỉ của server ko phải client
			//  tcp://host:port
			//  ssl://host:port
			// http://activemq.apache.org/uri-protocols.html
			// http://activemq.apache.org/tcp-transport-reference.html
			/**
			 *  tcp://hostname:port?key=value
			 *  vd:  tcp://localhost:61616?transport.threadName&transport.trace=false&transport.soTimeout=60000
			 */
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:1000");
			connectionFactory.setUserName("admin");
			connectionFactory.setPassword("admin");
			// Create a Connection
			Connection connection = connectionFactory.createConnection();
			connection.start();

			connection.setExceptionListener(this);  //server

			// Create a Session
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			// Create the destination (Topic or Queue)
			Destination destination = session.createQueue("TEST.FOO");

			//========================================================================end
			// Create a MessageConsumer from the Session to the Topic or Queue
			MessageConsumer consumer = session.createConsumer(destination);

			// Asynchronous runs on threadpool by ActiveMQ
			consumer.setMessageListener(new MessageListener() {
				//callback function runs on threadpool by ActiveMQ
				public void onMessage(Message message) {
					TextMessage textMessage = (TextMessage) message;
					String text;
					try {
						text = textMessage.getText();
						System.out.println("Received: onMessage(): thread=" + Thread.currentThread().getId() + 
								"***" + Thread.currentThread().getName());
						System.out.println("=> Received: " + text);
					} catch (JMSException e) {
						e.printStackTrace();
					}
				}
			});

			//Ko đc phép đóng các connect này
			/*            consumer.close();
            session.close();
            connection.close();*/
		} catch (Exception e) {
			System.out.println("Caught: " + e);
			e.printStackTrace();
		}
	}

	public synchronized void onException(JMSException ex) {
		System.out.println("JMS Exception occured.  Shutting down client.");
	}
}