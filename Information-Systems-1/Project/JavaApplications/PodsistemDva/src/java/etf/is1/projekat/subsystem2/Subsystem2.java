/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.is1.projekat.subsystem2;

import etf.is1.projekat.handlers.BrisanjeVideaHandler;
import etf.is1.projekat.handlers.RequestHandler;
import etf.is1.projekat.handlers.DodavanjeKategorijeHandler;
import etf.is1.projekat.handlers.DohvatanjeKategorijaZaVideoHandler;
import etf.is1.projekat.handlers.DohvatanjeSvihKategorijaHandler;
import etf.is1.projekat.handlers.DohvatanjeSvihVideaHandler;
import etf.is1.projekat.handlers.KreirajKategorijuHandler;
import etf.is1.projekat.handlers.KreirajVideoHandler;
import etf.is1.projekat.handlers.PromeniNazivVideaHandler;
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
public class Subsystem2 {

    @Resource(lookup = "jms/__defaultConnectionFactory")
    private static ConnectionFactory connectionFactory;

    @Resource(lookup = "serverQueue")
    private static Queue serverQueue;

    @Resource(lookup = "s2Queue")
    private static Queue queue2;

    private static final Map<ServerRequest.Request, RequestHandler> handlers = assignHandlers();

    private static Map<ServerRequest.Request, RequestHandler> assignHandlers() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("podsistem2PU");
        EntityManager em = emf.createEntityManager();

        Map<ServerRequest.Request, RequestHandler> map = new HashMap<>();

        map.put(ServerRequest.Request.KREIRANJE_KATEGORIJE, new KreirajKategorijuHandler(em));
        map.put(ServerRequest.Request.KREIRANJE_VIDEA, new KreirajVideoHandler(em));
        map.put(ServerRequest.Request.PROMENA_NAZIVA_VIDEA, new PromeniNazivVideaHandler(em));
        map.put(ServerRequest.Request.DODAVANJE_KATEGORIJE, new DodavanjeKategorijeHandler(em));
        map.put(ServerRequest.Request.DOHVATANJE_SVIH_KATEGORIJA, new DohvatanjeSvihKategorijaHandler(em));
        map.put(ServerRequest.Request.DOHVATANJE_SVIH_VIDEO_SNIMAKA, new DohvatanjeSvihVideaHandler(em));
        map.put(ServerRequest.Request.DOHVATANJE_KATEGORIJA_ZA_VIDEO, new DohvatanjeKategorijaZaVideoHandler(em));
        map.put(ServerRequest.Request.BRISANJE_VIDEA, new BrisanjeVideaHandler(em));

        return map;
    }

    public static void main(String[] args) {
        try {
            // Create a JMS context, producer, and consumer
            JMSContext context = connectionFactory.createContext();
            JMSProducer producer = context.createProducer();
            JMSConsumer consumer = context.createConsumer(queue2);
            Message msg;

            // Discard any existing messages in the queue before starting
            while ((msg = consumer.receiveNoWait()) != null) {
                System.out.println("Discarding message: " + msg);
            }

            System.out.println("Subsystem 2 started.");

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
                    
                    //Send the response
                    ObjectMessage responseMsg = context.createObjectMessage(response);
                    responseMsg.setJMSCorrelationID(msg.getJMSCorrelationID());
                    responseMsg.setJMSReplyTo(replyTo);

                    //TextMessage responseMsg = context.createTextMessage(response);
                    System.out.println("Sending response " + response);
                    producer.send(replyTo, responseMsg);

                } catch (JMSException ex) {
                    Logger.getLogger(Subsystem2.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        } catch (Exception e) {
            System.err.println("Error initializing subsystem: " + e.getMessage());
        }
    }

}
