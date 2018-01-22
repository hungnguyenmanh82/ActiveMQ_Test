package hung.com.activmq.broker.examTCP;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

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
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:1000");
            connectionFactory.setUserName("admin");
            connectionFactory.setPassword("admin");
            // Create a Connection
            Connection connection = connectionFactory.createConnection();
            //synchronous (blocking) here until Consumer create connection
            connection.start();

            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination destination = session.createQueue("TEST.FOO");
            
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


