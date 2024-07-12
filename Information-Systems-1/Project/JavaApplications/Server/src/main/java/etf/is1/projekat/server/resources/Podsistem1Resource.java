/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.is1.projekat.server.resources;

import etf.is1.projekat.entities.Korisnik;
import etf.is1.projekat.responses.DataResponse;
import etf.is1.projekat.responses.JMSResponse;
import etf.is1.projekat.responses.OKResponse;
import etf.is1.projekat.entities.Mesto;
import etf.is1.projekat.server.requests.DohvatiKorisnikeRequest;
import etf.is1.projekat.server.requests.DohvatiMestaRequest;
import etf.is1.projekat.server.requests.KreirajKorisnikaRequest;
import etf.is1.projekat.server.requests.KreirajMestoRequest;
import etf.is1.projekat.server.requests.PromeniEmailRequest;
import etf.is1.projekat.server.requests.PromeniMestoRequest;
import etf.is1.projekat.server.requests.ServerRequest;
import etf.is1.projekat.responses.ErrorResponse;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author ninaj
 */
@Path("podsistem1")
public class Podsistem1Resource {

    @Resource(lookup = "jms/__defaultConnectionFactory")
    private ConnectionFactory factory;

    @Resource(lookup = "s1Queue")
    private Queue s1Queue;

    @Resource(lookup = "serverQueue")
    private Queue serverQueue;

    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Path("createMesto")
    public Response createMesto(
            @FormParam("Naziv") String naziv) {

        try {
            // Create JMS context and producer
            JMSContext context = factory.createContext();
            JMSProducer producer = context.createProducer();

            // Create request object
            ServerRequest req = new KreirajMestoRequest(naziv);
            ObjectMessage objMsg = context.createObjectMessage(req);
            objMsg.setJMSReplyTo(serverQueue);
            objMsg.setJMSCorrelationID(String.valueOf(req.getId()));

            // Log the outgoing request
            System.out.println("Server sending " + req);
            producer.send(s1Queue, objMsg);

            JMSConsumer consumer = context.createConsumer(serverQueue, "JMSCorrelationID = '" + req.getId() + "'");
            Message reply = consumer.receive(5000);

            // Check if response is an ObjectMessage
            if (!(reply instanceof ObjectMessage)) {
                System.err.println("Received message is not an ObjectMessage: " + reply);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Received unexpected message type from server").build();
            }

            // Extract and handle the response object
            ObjectMessage objResponse = (ObjectMessage) reply;
            Serializable obj = objResponse.getObject();

            // Check if response is a JMSResponse
            if (!(obj instanceof JMSResponse)) {
                System.err.println("Response is not a JMSResponse: " + obj);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Received unexpected response type from server").build();
            }

            // Handle OKResponse
            if (obj instanceof OKResponse) {
                OKResponse ok = (OKResponse) obj;
                System.out.println("Server received response: " + ok);
                return Response.status(Response.Status.CREATED).entity(ok.toString()).build();
            }

            // Handle ErrorResponse
            if (obj instanceof ErrorResponse) {
                ErrorResponse error = (ErrorResponse) obj;
                System.out.println("Error response: " + error.getReason());
                return Response.status(Response.Status.BAD_REQUEST).entity(error.getReason()).build();
            }

            // Unexpected response type
            System.err.println("Received unexpected response type: " + obj);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Received unexpected response type from server").build();

        } catch (JMSException ex) {
            Logger.getLogger(Podsistem1Resource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to process request due to JMS exception").build();
        }
    }

    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Path("createKorisnik")
    public Response createKorisnik(
            @FormParam("ime") String ime,
            @FormParam("email") String email,
            @FormParam("godiste") int godiste,
            @FormParam("pol") Character pol,
            @FormParam("mesto") String mesto) {
        try {
            // Create JMS context and producer
            JMSContext context = factory.createContext();
            JMSProducer producer = context.createProducer();

            // Create request object
            ServerRequest req = new KreirajKorisnikaRequest(ime, email, godiste, pol, mesto);
            ObjectMessage objMsg = context.createObjectMessage(req);
            objMsg.setJMSReplyTo(serverQueue);
            objMsg.setJMSCorrelationID(String.valueOf(req.getId()));

            // Log the outgoing request
            System.out.println("Server sending " + req);
            producer.send(s1Queue, objMsg);

            JMSConsumer consumer = context.createConsumer(serverQueue, "JMSCorrelationID = '" + req.getId() + "'");
            Message reply = consumer.receive(5000);

            // Check if response is an ObjectMessage
            if (!(reply instanceof ObjectMessage)) {
                System.err.println("Received message is not an ObjectMessage: " + reply);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Received unexpected message type from server").build();
            }

            // Extract and handle the response object
            ObjectMessage objResponse = (ObjectMessage) reply;
            Serializable obj = objResponse.getObject();

            // Check if response is a JMSResponse
            if (!(obj instanceof JMSResponse)) {
                System.err.println("Response is not a JMSResponse: " + obj);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Received unexpected response type from server").build();
            }

            // Handle OKResponse
            if (obj instanceof OKResponse) {
                OKResponse ok = (OKResponse) obj;
                System.out.println("Server received response: " + ok);
                return Response.status(Response.Status.CREATED).entity(ok.toString()).build();
            }

            // Handle ErrorResponse
            if (obj instanceof ErrorResponse) {
                ErrorResponse error = (ErrorResponse) obj;
                System.out.println("Error response: " + error.getReason());
                return Response.status(Response.Status.BAD_REQUEST).entity(error.getReason()).build();
            }

            // Unexpected response type
            System.err.println("Received unexpected response type: " + obj);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Received unexpected response type from server").build();

        } catch (JMSException ex) {
            Logger.getLogger(Podsistem1Resource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to process request due to JMS exception").build();
        }
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("getMesta")
    public Response getMesta() {
        try {
            // Create JMS context and producer
            JMSContext context = factory.createContext();
            JMSProducer producer = context.createProducer();

            // Create request object
            ServerRequest req = new DohvatiMestaRequest();
            ObjectMessage objMsg = context.createObjectMessage(req);
            objMsg.setJMSReplyTo(serverQueue);
            objMsg.setJMSCorrelationID(String.valueOf(req.getId()));

            // Log the outgoing request
            System.out.println("Server sending " + req);
            producer.send(s1Queue, objMsg);

            JMSConsumer consumer = context.createConsumer(serverQueue, "JMSCorrelationID = '" + req.getId() + "'");
            Message reply = consumer.receive(5000);

            // Check if response is an ObjectMessage
            if (!(reply instanceof ObjectMessage)) {
                System.err.println("Received message is not an ObjectMessage: " + reply);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Received unexpected message type from server").build();
            }

            // Extract and handle the response object
            ObjectMessage objResponse = (ObjectMessage) reply;
            Serializable obj = objResponse.getObject();

            // Check if response is a JMSResponse
            if (!(obj instanceof JMSResponse)) {
                System.err.println("Response is not a JMSResponse: " + obj);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Received unexpected response type from server").build();
            }

            // Handle DataResponse
            if (obj instanceof DataResponse) {
                DataResponse dataResponse = (DataResponse) obj;
                List<Mesto> mesta = (List<Mesto>) dataResponse.getData();
                System.out.println("Server received response: " + mesta);
                return Response.status(Response.Status.OK).entity(mesta).build();
            }

            // Handle ErrorResponse
            if (obj instanceof ErrorResponse) {
                ErrorResponse error = (ErrorResponse) obj;
                System.out.println(error.getReason());
                return Response.status(Response.Status.BAD_REQUEST).entity(error.getReason()).build();
            }

            // Unexpected response type
            System.err.println("Received unexpected response type: " + obj);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Received unexpected response type from server").build();

        } catch (JMSException ex) {
            Logger.getLogger(Podsistem1Resource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to process request due to JMS exception").build();
        }

    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("getKorisnici")
    public Response getKorisnici() {
        try {
            // Create JMS context and producer
            JMSContext context = factory.createContext();
            JMSProducer producer = context.createProducer();

            // Create request object
            ServerRequest req = new DohvatiKorisnikeRequest();
            ObjectMessage objMsg = context.createObjectMessage(req);
            objMsg.setJMSReplyTo(serverQueue);
            objMsg.setJMSCorrelationID(String.valueOf(req.getId()));

            // Log the outgoing request
            System.out.println("Server sending " + req);
            producer.send(s1Queue, objMsg);

            JMSConsumer consumer = context.createConsumer(serverQueue, "JMSCorrelationID = '" + req.getId() + "'");
            Message reply = consumer.receive(5000);

            // Check if response is an ObjectMessage
            if (!(reply instanceof ObjectMessage)) {
                System.err.println("Received message is not an ObjectMessage: " + reply);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Received unexpected message type from server").build();
            }

            // Extract and handle the response object
            ObjectMessage objResponse = (ObjectMessage) reply;
            Serializable obj = objResponse.getObject();

            // Check if response is a JMSResponse
            if (!(obj instanceof JMSResponse)) {
                System.err.println("Response is not a JMSResponse: " + obj);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Received unexpected response type from server").build();
            }

            // Handle DataResponse
            if (obj instanceof DataResponse) {
                DataResponse dataResponse = (DataResponse) obj;
                List<Korisnik> korisnici = (List<Korisnik>) dataResponse.getData();
                System.out.println("Server received response: " + korisnici);
                return Response.status(Response.Status.OK).entity(korisnici).build();
            }

            // Handle ErrorResponse
            if (obj instanceof ErrorResponse) {
                ErrorResponse error = (ErrorResponse) obj;
                System.out.println(error.getReason());
                return Response.status(Response.Status.BAD_REQUEST).entity(error.getReason()).build();
            }

            // Unexpected response type
            System.err.println("Received unexpected response type: " + obj);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Received unexpected response type from server").build();

        } catch (JMSException ex) {
            Logger.getLogger(Podsistem1Resource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to process request due to JMS exception").build();
        }

    }

    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Path("changeEmail")
    public Response changeEmail(
            @FormParam("sifK") int sifK,
            @FormParam("email") String email) {
        try {
            // Create JMS context and producer
            JMSContext context = factory.createContext();
            JMSProducer producer = context.createProducer();

            // Create request object
            ServerRequest req = new PromeniEmailRequest(sifK, email);
            ObjectMessage objMsg = context.createObjectMessage(req);
            objMsg.setJMSReplyTo(serverQueue);
            objMsg.setJMSCorrelationID(String.valueOf(req.getId()));

            // Log the outgoing request
            System.out.println("Server sending " + req);
            producer.send(s1Queue, objMsg);

            JMSConsumer consumer = context.createConsumer(serverQueue, "JMSCorrelationID = '" + req.getId() + "'");
            Message reply = consumer.receive(5000);

            // Check if response is an ObjectMessage
            if (!(reply instanceof ObjectMessage)) {
                System.err.println("Received message is not an ObjectMessage: " + reply);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Received unexpected message type from server").build();
            }

            // Extract and handle the response object
            ObjectMessage objResponse = (ObjectMessage) reply;
            Serializable obj = objResponse.getObject();

            // Check if response is a JMSResponse
            if (!(obj instanceof JMSResponse)) {
                System.err.println("Response is not a JMSResponse: " + obj);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Received unexpected response type from server").build();
            }

            // Handle OKResponse
            if (obj instanceof OKResponse) {
                OKResponse ok = (OKResponse) obj;
                System.out.println("Server received response: " + ok);
                return Response.status(Response.Status.OK).entity(ok.toString()).build();
            }

            // Handle ErrorResponse
            if (obj instanceof ErrorResponse) {
                ErrorResponse error = (ErrorResponse) obj;
                System.out.println("Error response: " + error.getReason());
                return Response.status(Response.Status.BAD_REQUEST).entity(error.getReason()).build();
            }

            // Unexpected response type
            System.err.println("Received unexpected response type: " + obj);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Received unexpected response type from server").build();

        } catch (JMSException ex) {
            Logger.getLogger(Podsistem1Resource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to process request due to JMS exception").build();
        }
    }

    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Path("changeMesto")
    public Response changeMesto(
            @FormParam("sifK") int sifK,
            @FormParam("naziv") String naziv) {
        try {
            // Create JMS context and producer
            JMSContext context = factory.createContext();
            JMSProducer producer = context.createProducer();

            // Create request object
            ServerRequest req = new PromeniMestoRequest(sifK, naziv);
            ObjectMessage objMsg = context.createObjectMessage(req);
            objMsg.setJMSReplyTo(serverQueue);
            objMsg.setJMSCorrelationID(String.valueOf(req.getId()));

            // Log the outgoing request
            System.out.println("Server sending " + req);
            producer.send(s1Queue, objMsg);

            JMSConsumer consumer = context.createConsumer(serverQueue, "JMSCorrelationID = '" + req.getId() + "'");
            Message reply = consumer.receive(5000);

            // Check if response is an ObjectMessage
            if (!(reply instanceof ObjectMessage)) {
                System.err.println("Received message is not an ObjectMessage: " + reply);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Received unexpected message type from server").build();
            }

            // Extract and handle the response object
            ObjectMessage objResponse = (ObjectMessage) reply;
            Serializable obj = objResponse.getObject();

            // Check if response is a JMSResponse
            if (!(obj instanceof JMSResponse)) {
                System.err.println("Response is not a JMSResponse: " + obj);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Received unexpected response type from server").build();
            }

            // Handle OKResponse
            if (obj instanceof OKResponse) {
                OKResponse ok = (OKResponse) obj;
                System.out.println("Server received response: " + ok);
                return Response.status(Response.Status.OK).entity(ok.toString()).build();
            }

            // Handle ErrorResponse
            if (obj instanceof ErrorResponse) {
                ErrorResponse error = (ErrorResponse) obj;
                System.out.println("Error response: " + error.getReason());
                return Response.status(Response.Status.BAD_REQUEST).entity(error.getReason()).build();
            }

            // Unexpected response type
            System.err.println("Received unexpected response type: " + obj);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Received unexpected response type from server").build();

        } catch (JMSException ex) {
            Logger.getLogger(Podsistem1Resource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to process request due to JMS exception").build();
        }
    }

}
