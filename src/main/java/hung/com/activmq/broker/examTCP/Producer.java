package hung.com.activmq.broker.examTCP;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * Consumer = receiver: bên nhận event.
 * producer = sender: bên gửi
 * asynchronous event on the only thread by ActiveMQ
 * 
 * @Runnable:  dùng để chạy function này trên worker thread thôi
 */
public class Producer implements Runnable {
    public void run() {
        try {
            //================================  ==========
        	// vm://localhost: là giao thức trong nội bộ Java Virtual Machine
        	//  tcp://host:port
        	//  ssl://host:port
        	//  http://activemq.apache.org/uri-protocols.html
        	// tcp://localhost:1000
    		// http://activemq.apache.org/tcp-transport-reference.html
    		/**
    		 *  tcp://hostname:port?key=value
    		 *  vd:  tcp://localhost:61616?transport.threadName&transport.trace=false&transport.soTimeout=60000
    		 */
        	// User/pass ko có ở thiết lập Broker server
        	// bản chất nó là ID để đăng ký nhận message từ Consumer(Subscriber)
        	// Consumer và Producer phải chung user/pass thì mới gửi nhận message cho nhau đc.
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:1000");
            connectionFactory.setUserName("admin");
            connectionFactory.setPassword("admin");
            // Create a Connection
            Connection connection = connectionFactory.createConnection();
            //synchronous (blocking) here until Consumer create connection
            connection.start();

            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            //QueueName giống như ID để giao tiếp giữa Consumer và Producer thì phải.
			//Queue này ở Producer để send Message tới Broker (ko phải ở broker).
         // Create the destination (Topic or Queue)
            Destination destination = session.createQueue("TEST.FOO");
//			session.createTopic(topicName)
            
            //========================================================================end
            // Create a MessageProducer from the Session to the Topic or Queue
            MessageProducer producer = session.createProducer(destination);
            /**
             *     NON_PERSISTENT = 1;
				   PERSISTENT = 2;
             */
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            // Create a messages
            String text = "Hello world! From: " + Thread.currentThread().getName() + " : " + this.hashCode();
            TextMessage message = session.createTextMessage(text);
//            message.setJMSMessageID(id);
//            message.setJMSPriority(priority);

            // Tell the producer to send the message
            System.out.println("<= Sent message: "+ message.hashCode() + " : " + Thread.currentThread().getName());
            producer.send(message);
            System.out.println("<= finished sending : " + Thread.currentThread().getName());
            
            //test: repeat send message
            Thread.sleep(100);
            producer.send(message);
            Thread.sleep(100);
            producer.send(message);

            // Clean up
            session.close();
            connection.close();
        }
        catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }
}


