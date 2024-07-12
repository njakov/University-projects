/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.is1.projekat.subsystem3;

import etf.is1.projekat.handlers.BrisanjeOceneHandler;
import etf.is1.projekat.handlers.RequestHandler;
import etf.is1.projekat.handlers.DohvatanjeOcenaZaSnimakHandler;
import etf.is1.projekat.handlers.DohvatanjeSvihGledanjaHandler;
import etf.is1.projekat.handlers.KreirajOcenuZaSnimakHandler;
import etf.is1.projekat.handlers.DohvatanjeSvihPaketaHandler;
import etf.is1.projekat.handlers.DohvatanjeSvihPretplataHandler;
import etf.is1.projekat.handlers.KreirajGledanjeHandler;
import etf.is1.projekat.handlers.KreirajPretplatuHandler;
import etf.is1.projekat.handlers.KreirajPaketHandler;
import etf.is1.projekat.handlers.PromeniCenuPaketaHandler;
import etf.is1.projekat.handlers.PromeniOcenuZaSnimakHandler;
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
public class Subsystem3 {

    @Resource(lookup = "jms/__defaultConnectionFactory")
    private static ConnectionFactory connectionFactory;

    @Resource(lookup = "serverQueue")
    private static Queue serverQueue;

    @Resource(lookup = "s3Queue")
    private static Queue queue3;

    private static final Map<ServerRequest.Request, RequestHandler> handlers = assignHandlers();

    private static Map<ServerRequest.Request, RequestHandler> assignHandlers() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("podsistem3PU");
        EntityManager em = emf.createEntityManager();

        Map<ServerRequest.Request, RequestHandler> map = new HashMap<>();

        map.put(ServerRequest.Request.KREIRANJE_PAKETA, new KreirajPaketHandler(em));
        map.put(ServerRequest.Request.PROMENA_CENE_PAKETA, new PromeniCenuPaketaHandler(em));
        map.put(ServerRequest.Request.KREIRANJE_PRETPLATE, new KreirajPretplatuHandler(em));
        map.put(ServerRequest.Request.KREIRANJE_GLEDANJA, new KreirajGledanjeHandler(em));
        map.put(ServerRequest.Request.KREIRANJE_OCENE, new KreirajOcenuZaSnimakHandler(em));
        map.put(ServerRequest.Request.PROMENA_OCENE, new PromeniOcenuZaSnimakHandler(em));
        map.put(ServerRequest.Request.BRISANJE_OCENE, new BrisanjeOceneHandler(em));
        map.put(ServerRequest.Request.DOHVATANJE_SVIH_PAKETA, new DohvatanjeSvihPaketaHandler(em));
        map.put(ServerRequest.Request.DOHVATANJE_PRETPLATA_ZA_KORISNIKA, new DohvatanjeSvihPretplataHandler(em));
        map.put(ServerRequest.Request.DOHVATANJE_GLEDANJA_ZA_VIDEO, new DohvatanjeSvihGledanjaHandler(em));
        map.put(ServerRequest.Request.DOHVATANJE_OCENA_ZA_VIDEO, new DohvatanjeOcenaZaSnimakHandler(em));
        return map;
    }

    public static void main(String[] args) {
        try {
            // Create a JMS context, producer, and consumer
            JMSContext context = connectionFactory.createContext();
            JMSProducer producer = context.createProducer();
            JMSConsumer consumer = context.createConsumer(queue3);
            Message msg;

            // Discard any existing messages in the queue before starting
            while ((msg = consumer.receiveNoWait()) != null) {
                System.out.println("Discarding message: " + msg);
            }

            System.out.println("Subsystem 3 started.");

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
                    Logger.getLogger(Subsystem3.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        } catch (Exception e) {
            System.err.println("Error initializing subsystem: " + e.getMessage());
        }
    }

}
