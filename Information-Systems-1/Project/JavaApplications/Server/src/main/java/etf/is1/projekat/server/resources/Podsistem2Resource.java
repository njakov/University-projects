/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.is1.projekat.server.resources;

import etf.is1.projekat.entities.Kategorija;
import etf.is1.projekat.entities.Video;
import etf.is1.projekat.responses.DataResponse;
import etf.is1.projekat.responses.ErrorResponse;
import etf.is1.projekat.responses.JMSResponse;
import etf.is1.projekat.responses.OKResponse;
import etf.is1.projekat.server.requests.DodajKategorijuZaVideoRequest;
import etf.is1.projekat.server.requests.DohvatiKategorijeRequest;
import etf.is1.projekat.server.requests.DohvatiVideeRequest;
import etf.is1.projekat.server.requests.DohvatiKategorijeZaVideoRequest;
import etf.is1.projekat.server.requests.KreirajKategorijuRequest;
import etf.is1.projekat.server.requests.KreirajVideoRequest;
import etf.is1.projekat.server.requests.ObrisiVideoRequest;
import etf.is1.projekat.server.requests.PromeniNazivVideaRequest;
import etf.is1.projekat.server.requests.ServerRequest;
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
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author ninaj
 */
@Path("podsistem2")
public class Podsistem2Resource {

    @Resource(lookup = "jms/__defaultConnectionFactory")
    private ConnectionFactory factory;

    @Resource(lookup = "s2Queue")
    private Queue s2Queue;

    @Resource(lookup = "serverQueue")
    private Queue serverQueue;

    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Path("createKategorija")
    public Response createKategorija(
            @FormParam("Naziv") String naziv) {

        try {
            // Create JMS context and producer
            JMSContext context = factory.createContext();
            JMSProducer producer = context.createProducer();

            // Create request object
            ServerRequest req = new KreirajKategorijuRequest(naziv);
            ObjectMessage objMsg = context.createObjectMessage(req);
            objMsg.setJMSReplyTo(serverQueue);
            objMsg.setJMSCorrelationID(String.valueOf(req.getId()));

            // Log the outgoing request
            System.out.println("Server sending " + req);
            producer.send(s2Queue, objMsg);

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
            Logger.getLogger(Podsistem2Resource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to process request due to JMS exception").build();
        }

    }

    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Path("createVideo")
    public Response createVideo(
            @FormParam("Naziv") String naziv,
            @FormParam("Trajanje") int trajanje,
            @FormParam("SifK") int sifK) {

        try {
            JMSContext context = factory.createContext();
            JMSProducer producer = context.createProducer();

            ServerRequest req = new KreirajVideoRequest(naziv, trajanje, sifK);
            ObjectMessage objMsg = context.createObjectMessage(req);
            objMsg.setJMSReplyTo(serverQueue);
            objMsg.setJMSCorrelationID(String.valueOf(req.getId()));

            // Log the outgoing request
            System.out.println("Server sending " + req);
            producer.send(s2Queue, objMsg);

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
            Logger.getLogger(Podsistem2Resource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to process request due to JMS exception").build();
        }

    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("getKategorije")
    public Response getKategorije() {
        try {
            // Create JMS context and producer
            JMSContext context = factory.createContext();
            JMSProducer producer = context.createProducer();

            // Create request object
            ServerRequest req = new DohvatiKategorijeRequest();
            ObjectMessage objMsg = context.createObjectMessage(req);
            objMsg.setJMSReplyTo(serverQueue);
            objMsg.setJMSCorrelationID(String.valueOf(req.getId()));

            // Log the outgoing request
            System.out.println("Server sending " + req);
            producer.send(s2Queue, objMsg);

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
                List<Kategorija> kategorije = (List<Kategorija>) dataResponse.getData();
                System.out.println("Server received response: " + kategorije);
                return Response.status(Response.Status.OK).entity(kategorije).build();
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
            Logger.getLogger(Podsistem2Resource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to process request due to JMS exception").build();
        }

    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("getVidei")
    public Response getVidei() {
        try {
            // Create JMS context and producer
            JMSContext context = factory.createContext();
            JMSProducer producer = context.createProducer();

            // Create request object
            ServerRequest req = new DohvatiVideeRequest();
            ObjectMessage objMsg = context.createObjectMessage(req);
            objMsg.setJMSReplyTo(serverQueue);
            objMsg.setJMSCorrelationID(String.valueOf(req.getId()));

            // Log the outgoing request
            System.out.println("Server sending " + req);
            producer.send(s2Queue, objMsg);

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
                List<Video> videi = (List<Video>) dataResponse.getData();
                System.out.println("Server received response: " + videi);
                return Response.status(Response.Status.OK).entity(videi).build();
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
            Logger.getLogger(Podsistem2Resource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to process request due to JMS exception").build();
        }
    }

    @GET
    @Path("getKategorijeVidea/{sifV}")
    public Response getKategorijeVidea(@PathParam("sifV") int sifV) {
        try {
            // Create JMS context and producer
            JMSContext context = factory.createContext();
            JMSProducer producer = context.createProducer();

            // Create request object
            ServerRequest req = new DohvatiKategorijeZaVideoRequest(sifV);
            ObjectMessage objMsg = context.createObjectMessage(req);
            objMsg.setJMSReplyTo(serverQueue);
            objMsg.setJMSCorrelationID(String.valueOf(req.getId()));

            // Log the outgoing request
            System.out.println("Server sending " + req);
            producer.send(s2Queue, objMsg);

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
                List<Kategorija> kategorije = (List<Kategorija>) dataResponse.getData();
                System.out.println("Server received response: " + kategorije);
                return Response.status(Response.Status.OK).entity(kategorije).build();
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
            Logger.getLogger(Podsistem2Resource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to process request due to JMS exception").build();
        }
    }

    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Path("changeNazivVidea")
    public Response changeEmail(
            @FormParam("sifV") int sifV,
            @FormParam("naziv") String naziv) {
        try {
            // Create JMS context and producer
            JMSContext context = factory.createContext();
            JMSProducer producer = context.createProducer();

            // Create request object
            ServerRequest req = new PromeniNazivVideaRequest(sifV, naziv);
            ObjectMessage objMsg = context.createObjectMessage(req);
            objMsg.setJMSReplyTo(serverQueue);
            objMsg.setJMSCorrelationID(String.valueOf(req.getId()));

            // Log the outgoing request
            System.out.println("Server sending " + req);
            producer.send(s2Queue, objMsg);

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
            Logger.getLogger(Podsistem2Resource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to process request due to JMS exception").build();
        }

    }

    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Path("dodajKategoriju")
    public Response dodajKategoriju(
            @FormParam("sifV") int sifV,
            @FormParam("sifKat") int sifKat) {
        try {
            // Create JMS context and producer
            JMSContext context = factory.createContext();
            JMSProducer producer = context.createProducer();

            // Create request object
            ServerRequest req = new DodajKategorijuZaVideoRequest(sifV, sifKat);
            ObjectMessage objMsg = context.createObjectMessage(req);
            objMsg.setJMSReplyTo(serverQueue);
            objMsg.setJMSCorrelationID(String.valueOf(req.getId()));

            // Log the outgoing request
            System.out.println("Server sending " + req);
            producer.send(s2Queue, objMsg);

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
            Logger.getLogger(Podsistem2Resource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to process request due to JMS exception").build();
        }

    }

    @Produces(MediaType.APPLICATION_JSON)
    @DELETE
    @Path("deleteVideo/{sifV}/{sifK}")
    public Response deleteVideo(
            @PathParam("sifV") int sifV,
            @PathParam("sifK") int sifK) {

        try {
            // Create JMS context and producer
            JMSContext context = factory.createContext();
            JMSProducer producer = context.createProducer();

            // Create request object
            ServerRequest req = new ObrisiVideoRequest(sifV, sifK);
            ObjectMessage objMsg = context.createObjectMessage(req);
            objMsg.setJMSReplyTo(serverQueue);
            objMsg.setJMSCorrelationID(String.valueOf(req.getId()));

            // Log the outgoing request
            System.out.println("Server sending " + req);
            producer.send(s2Queue, objMsg);

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
            Logger.getLogger(Podsistem2Resource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to process request due to JMS exception").build();
        }
    }

}
