package hung.com.activmq.broker.examVM;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * Consumer = receiver: bên nhận event.
 * producer = sender: bên gửi
 * Có 2 cơ chế nhận là synchronous và asynchronous
 * 
 * @Runnable:  dùng để chạy function này trên worker thread thôi
 */
public class ConsumerSynchronous implements Runnable, ExceptionListener {
    public void run() {
        try {
        	 //================================  ==========
            // vm://localhost: là giao thức trong nội bộ Java Virtual Machine
        	//khởi tạo Broker đây là địa chỉ của server ko phải client
        	//  tcp://host:port
        	//  ssl://host:port
        	//  http://activemq.apache.org/uri-protocols.html
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");

            // Create a Connection
            Connection connection = connectionFactory.createConnection();
            connection.start();

            connection.setExceptionListener(this);

            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination destination = session.createQueue("TEST.FOO");

            //========================================================================end
            // Create a MessageConsumer from the Session to the Topic or Queue
            MessageConsumer consumer = session.createConsumer(destination);

            // synchronous waiting until receiving message from Producer
            Message message = consumer.receive(1000); //timeout = 1000ms

            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                String text = textMessage.getText();
                System.out.println("Received: " + text);
            } else {
                System.out.println("Received: " + message);
            }

            consumer.close();
            session.close();
            connection.close();
        } catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }
    
    public synchronized void onException(JMSException ex) {
        System.out.println("JMS Exception occured.  Shutting down client.");
    }
}