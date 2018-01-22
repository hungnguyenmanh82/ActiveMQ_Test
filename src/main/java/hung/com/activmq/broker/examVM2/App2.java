package hung.com.activmq.broker.examVM2;

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
 
/**
 * vm://localhost: là giao thức trong nội bộ Java Virtual Machine
 * đóng vai trò là Broker luôn
 */
public class App2 {
 
    public static void main(String[] args) throws Exception {
        thread(new Producer(), false); //client blocking wait for server created
        thread(new Producer(), false); //client blocking wait for server created
        Thread.sleep(1000);
        thread(new ConsumerAsync(), false); //server
        thread(new Producer(), false); //client 
        

    }
 
    public static void thread(Runnable runnable, boolean daemon) {
        Thread brokerThread = new Thread(runnable);
        brokerThread.setDaemon(daemon);
        brokerThread.start();
    }


}
