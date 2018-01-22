package hung.com.activmq.broker.examVM2;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * Producer quan hệ với Broker. Nó ko liên quan gì tới consumer
 *
 */
public class Producer implements Runnable {
    public void run() {
        try {
            //================================  ==========
        	//  vm://localhost: là giao thức trong nội bộ Java Virtual Machine
        	//  tcp://host:port
        	//  ssl://host:port
        	//  http://activemq.apache.org/uri-protocols.html
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:82");

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


