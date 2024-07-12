
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.is1.projekat.subsystem1;

import etf.is1.projekat.handlers.DohvatanjeSvihKorisnikaHandler;
import etf.is1.projekat.handlers.DohvatanjeSvihMestaHandler;
import etf.is1.projekat.handlers.KreirajKorisnikaHandler;
import etf.is1.projekat.handlers.KreirajMestoHandler;
import etf.is1.projekat.handlers.PromeniEmailHandler;
import etf.is1.projekat.handlers.PromeniMestoHandler;
import etf.is1.projekat.handlers.RequestHandler;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import etf.is1.projekat.server.requests.ServerRequest;
import etf.is1.projekat.responses.JMSResponse;

/**
 *
 * @author ninaj
 */
public class Subsystem1 {

    @Resource(lookup = "jms/__defaultConnectionFactory")
    private static ConnectionFactory connectionFactory;

    @Resource(lookup = "serverQueue")
    private static Queue serverQueue;

    @Resource(lookup = "s1Queue")
    private static Queue queue1;

    private static final Map<ServerRequest.Request, RequestHandler> handlers = assignHandlers();

    private static Map<ServerRequest.Request, RequestHandler> assignHandlers() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("podsistem1PU");
        EntityManager em = emf.createEntityManager();
        Map<ServerRequest.Request, RequestHandler> map = new HashMap<>();

        map.put(ServerRequest.Request.KREIRANJE_MESTA, new KreirajMestoHandler(em));
        map.put(ServerRequest.Request.KREIRANJE_KORISNIKA, new KreirajKorisnikaHandler(em));
        map.put(ServerRequest.Request.PROMENA_EMAIL, new PromeniEmailHandler(em));
        map.put(ServerRequest.Request.PROMENA_MESTA, new PromeniMestoHandler(em));
        map.put(ServerRequest.Request.DOHVATANJE_SVIH_MESTA, new DohvatanjeSvihMestaHandler(em));
        map.put(ServerRequest.Request.DOHVATANJE_SVIH_KORISNIKA, new DohvatanjeSvihKorisnikaHandler(em));

        return map;
    }

    public static void main(String[] args) {
        try {
            // Create a JMS context, producer, and consumer
            JMSContext context = connectionFactory.createContext();

            JMSProducer producer = context.createProducer();
            JMSConsumer consumer = context.createConsumer(queue1);
            Message msg;

            // Discard any existing messages in the queue before starting
            while ((msg = consumer.receiveNoWait()) != null) {
                System.out.println("Discarding message: " + msg);
            }
            System.out.println("Subsystem 1 started.");

            while (true) {
                try {
                    // Receive a JMS message
                    msg = consumer.receive();
                    
                    // Validate reply destination
                    Destination replyTo = msg.getJMSReplyTo();
                    if (replyTo == null || !(replyTo instanceof Queue)) {
                        System.err.println("Invalid reply destination: " + msg);
                        continue;
                    }

                    // Ensure the message is an ObjectMessage
                    if (!(msg instanceof ObjectMessage)) {
                        System.err.println("Received message is not an ObjectMessage: " + msg);
                        continue;
                    }
                    // Extract the ServerRequest object
                    Serializable obj = ((ObjectMessage) msg).getObject();
                    if (!(obj instanceof ServerRequest)) {
                        System.err.println("Received object is not a ServerRequest: " + obj);
                        continue;
                    }
                    ServerRequest req = (ServerRequest) obj;

                    // Find the appropriate handler for the request
                    RequestHandler handler = handlers.get(req.getRequest());
                    if (handler == null) {
                        System.err.println("No handler found for request: " + req);
                        continue;
                    }
                    // Process the request
                    System.out.println("Handling request " + req);
                    JMSResponse response = handler.handle(req);

                    //Send response
                    ObjectMessage responseMsg = context.createObjectMessage(response);
                    responseMsg.setJMSCorrelationID(msg.getJMSCorrelationID());
                    responseMsg.setJMSReplyTo(replyTo);

                    System.out.println("Sending response " + response);
                    producer.send(replyTo, responseMsg);

                } catch (JMSException ex) {
                    Logger.getLogger(Subsystem1.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        } catch (Exception e) {
            System.err.println("Error initializing subsystem: " + e.getMessage());
        }
    }
    
}
