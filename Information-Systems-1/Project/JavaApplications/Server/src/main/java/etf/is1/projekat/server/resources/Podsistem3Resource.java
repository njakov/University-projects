/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.is1.projekat.server.resources;

import etf.is1.projekat.entities.subsystem3.Gledanje;
import etf.is1.projekat.entities.subsystem3.Ocena;
import etf.is1.projekat.entities.subsystem3.Paket;
import etf.is1.projekat.entities.subsystem3.Pretplata;
import etf.is1.projekat.responses.DataResponse;
import etf.is1.projekat.responses.ErrorResponse;
import etf.is1.projekat.responses.JMSResponse;
import etf.is1.projekat.responses.OKResponse;
import etf.is1.projekat.server.requests.DohvatiGledanjaVideaRequest;
import etf.is1.projekat.server.requests.DohvatiOceneVideaRequest;
import etf.is1.projekat.server.requests.DohvatiPaketeRequest;
import etf.is1.projekat.server.requests.DohvatiPretplateKorisnikaRequest;
import etf.is1.projekat.server.requests.KreirajGledanjeRequest;
import etf.is1.projekat.server.requests.KreirajOcenuRequest;
import etf.is1.projekat.server.requests.KreirajPaketRequest;
import etf.is1.projekat.server.requests.KreirajPretplatuRequest;
import etf.is1.projekat.server.requests.ObrisiOcenuRequest;
import etf.is1.projekat.server.requests.PromeniCenuPaketaRequest;
import etf.is1.projekat.server.requests.PromeniOcenuSnimkaRequest;
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
@Path("podsistem3")
public class Podsistem3Resource {

    @Resource(lookup = "jms/__defaultConnectionFactory")
    private ConnectionFactory factory;

    @Resource(lookup = "s3Queue")
    private Queue s3Queue;

    @Resource(lookup = "serverQueue")
    private Queue serverQueue;

    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Path("createPaket")
    public Response createPaket(
            @FormParam("naziv") String naziv,
            @FormParam("cena") int cena) {

        try {
            // Create JMS context and producer
            JMSContext context = factory.createContext();
            JMSProducer producer = context.createProducer();

            // Create request object
            ServerRequest req = new KreirajPaketRequest(naziv, cena);
            ObjectMessage objMsg = context.createObjectMessage(req);
            objMsg.setJMSReplyTo(serverQueue);
            objMsg.setJMSCorrelationID(String.valueOf(req.getId()));

            System.out.println("Server sending " + req);
            producer.send(s3Queue, objMsg);

            JMSConsumer consumer = context.createConsumer(serverQueue, "JMSCorrelationID = '" + req.getId() + "'");
            Message reply = consumer.receive(5000);

            if (!(reply instanceof ObjectMessage)) {
                System.err.println("Received message is not an ObjectMessage: " + reply);
            }
            ObjectMessage objResponse = (ObjectMessage) reply;

            Serializable obj = (objResponse).getObject();

            if (!(obj instanceof JMSResponse)) {
                System.err.println("Response is not a JMSResponse: " + obj);
            }

            if (obj instanceof ErrorResponse) {
                ErrorResponse error = (ErrorResponse) obj;
                System.out.println(error.getReason());
                return Response.status(Response.Status.BAD_REQUEST).entity(error.getReason()).build();
            }

            // Handle OKResponse
            if (obj instanceof OKResponse) {
                OKResponse ok = (OKResponse) obj;
                System.out.println("Server received response: " + ok);
                return Response.status(Response.Status.OK).entity(ok.toString()).build();
            }

            // Unexpected response type
            System.err.println("Received unexpected response type: " + obj);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Received unexpected response type from server").build();

        } catch (JMSException ex) {
            Logger.getLogger(Podsistem3Resource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to process request due to JMS exception").build();
        }

    }

    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Path("changeCena")
    public Response changeCena(
            @FormParam("sifPak") int sifPak,
            @FormParam("cena") int cena) {

        try {
            // Create JMS context and producer
            JMSContext context = factory.createContext();
            JMSProducer producer = context.createProducer();

            // Create request object
            ServerRequest req = new PromeniCenuPaketaRequest(sifPak, cena);
            ObjectMessage objMsg = context.createObjectMessage(req);
            objMsg.setJMSReplyTo(serverQueue);
            objMsg.setJMSCorrelationID(String.valueOf(req.getId()));

            // Log the outgoing request
            System.out.println("Server sending " + req);
            producer.send(s3Queue, objMsg);

            // Create consumer to receive response
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
            Logger.getLogger(Podsistem3Resource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to process request due to JMS exception").build();
        }
    }

    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Path("createPretplata")
    public Response createPretplata(
            @FormParam("sifK") int sifK,
            @FormParam("sifPak") int sifPak) {

        try {
            // Create JMS context and producer
            JMSContext context = factory.createContext();
            JMSProducer producer = context.createProducer();

            // Create a request object
            ServerRequest req = new KreirajPretplatuRequest(sifK, sifPak);
            ObjectMessage objMsg = context.createObjectMessage(req);
            objMsg.setJMSReplyTo(serverQueue);
            objMsg.setJMSCorrelationID(String.valueOf(req.getId()));

            // Log the outgoing request
            System.out.println("Server sending " + req);
            producer.send(s3Queue, objMsg);

            // Create a consumer to receive the response
            JMSConsumer consumer = context.createConsumer(serverQueue, "JMSCorrelationID = '" + req.getId() + "'");
            Message reply = consumer.receive(5000);

            if (!(reply instanceof ObjectMessage)) {
                System.err.println("Received message is not an ObjectMessage: " + reply);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Received unexpected message type from server").build();
            }

            ObjectMessage objResponse = (ObjectMessage) reply;
            Serializable obj = objResponse.getObject();

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
            Logger.getLogger(Podsistem3Resource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to process request due to JMS exception").build();
        }
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("getPaketi")
    public Response getPaketi() {
        try {
            // Create JMS context and producer
            JMSContext context = factory.createContext();
            JMSProducer producer = context.createProducer();

            // Create a request object
            ServerRequest req = new DohvatiPaketeRequest();
            ObjectMessage objMsg = context.createObjectMessage(req);
            objMsg.setJMSReplyTo(serverQueue);
            objMsg.setJMSCorrelationID(String.valueOf(req.getId()));

            // Log the outgoing request
            System.out.println("Server sending " + req);
            producer.send(s3Queue, objMsg);

            // Create a consumer to receive the response
            JMSConsumer consumer = context.createConsumer(serverQueue, "JMSCorrelationID = '" + req.getId() + "'");
            Message reply = consumer.receive(5000);

            if (!(reply instanceof ObjectMessage)) {
                System.err.println("Received message is not an ObjectMessage: " + reply);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Received message is not an ObjectMessage.").build();
            }

            ObjectMessage objResponse = (ObjectMessage) reply;
            Serializable obj = objResponse.getObject();

            if (!(obj instanceof JMSResponse)) {
                System.err.println("Response is not a JMSResponse: " + obj);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Response is not a JMSResponse.").build();
            }

            // Handle ErrorResponse
            if (obj instanceof ErrorResponse) {
                ErrorResponse error = (ErrorResponse) obj;
                System.out.println(error.getReason());
                return Response.status(Response.Status.BAD_REQUEST).entity(error.getReason()).build();
            }

            // Handle DataResponse
            if (obj instanceof DataResponse) {
                DataResponse dataResponse = (DataResponse) obj;
                List<Paket> paketi = (List<Paket>) dataResponse.getData();
                System.out.println("Server received response: " + paketi);
                return Response.status(Response.Status.OK).entity(paketi).build();
            }

            // Unexpected response type
            System.err.println("Received unexpected response type: " + obj);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Received unexpected response type from server").build();

        } catch (JMSException ex) {
            Logger.getLogger(Podsistem3Resource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to process request due to JMS exception").build();
        }

    }

    @GET
    @Path("getPretplateKorisnika/{sifK}")
    public Response getPretplateKorisnika(@PathParam("sifK") int sifK) {
        try {
            // Create JMS context and producer
            JMSContext context = factory.createContext();
            JMSProducer producer = context.createProducer();

            // Create request object
            ServerRequest req = new DohvatiPretplateKorisnikaRequest(sifK);
            ObjectMessage objMsg = context.createObjectMessage(req);
            objMsg.setJMSReplyTo(serverQueue);
            objMsg.setJMSCorrelationID(String.valueOf(req.getId()));

            // Log the outgoing request
            System.out.println("Server sending " + req);
            producer.send(s3Queue, objMsg);

            // Create consumer to receive response
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

            // Handle ErrorResponse
            if (obj instanceof ErrorResponse) {
                ErrorResponse error = (ErrorResponse) obj;
                System.out.println("Error response: " + error.getReason());
                return Response.status(Response.Status.BAD_REQUEST).entity(error.getReason()).build();
            }

            // Handle DataResponse
            if (obj instanceof DataResponse) {
                DataResponse data = (DataResponse) obj;
                List<Pretplata> pretplata = (List<Pretplata>) data.getData();
                System.out.println("Server received: " + pretplata);
                return Response.status(Response.Status.OK).entity(pretplata).build();
            }

            // Unexpected response type
            System.err.println("Received unexpected response type: " + obj);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Received unexpected response type from server").build();

        } catch (JMSException ex) {
            Logger.getLogger(Podsistem3Resource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to process request due to JMS exception").build();
        }
    }

    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Path("createOcena")
    public Response createOcena(
            @FormParam("sifK") int sifK,
            @FormParam("sifV") int sifV,
            @FormParam("ocena") int ocena) {

        try {
            // Create JMS context and producer
            JMSContext context = factory.createContext();
            JMSProducer producer = context.createProducer();

            // Create server request object
            ServerRequest req = new KreirajOcenuRequest(sifK, sifV, ocena);
            ObjectMessage objMsg = context.createObjectMessage(req);
            objMsg.setJMSReplyTo(serverQueue);
            objMsg.setJMSCorrelationID(String.valueOf(req.getId()));

            // Log the outgoing request
            System.out.println("Server sending " + req);
            producer.send(s3Queue, objMsg);

            // Create consumer to receive response
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
            Logger.getLogger(Podsistem3Resource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to process request due to JMS exception").build();
        }
    }

    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Path("changeOcena")
    public Response changeOcena(
            @FormParam("sifK") int sifK,
            @FormParam("sifV") int sifV,
            @FormParam("ocena") int ocena) {

        try {
            // Create JMS context and producer
            JMSContext context = factory.createContext();
            JMSProducer producer = context.createProducer();

            // Create server request object
            ServerRequest req = new PromeniOcenuSnimkaRequest(sifK, sifV, ocena);
            ObjectMessage objMsg = context.createObjectMessage(req);
            objMsg.setJMSReplyTo(serverQueue);
            objMsg.setJMSCorrelationID(String.valueOf(req.getId()));

            // Log the outgoing request
            System.out.println("Server sending " + req);
            producer.send(s3Queue, objMsg);

            // Create consumer to receive response
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
            Logger.getLogger(Podsistem3Resource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to process request due to JMS exception").build();
        }
    }

    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Path("createGledanje")
    public Response createGledanje(
            @FormParam("sifK") int sifK,
            @FormParam("sifV") int sifV,
            @FormParam("zapoceto") int zapoceto,
            @FormParam("odgledano") int odgledano) {

        try {
            // Create JMS context and producer
            JMSContext context = factory.createContext();
            JMSProducer producer = context.createProducer();

            ServerRequest req = new KreirajGledanjeRequest(sifK, sifV, zapoceto, odgledano);
            ObjectMessage objMsg = context.createObjectMessage(req);
            objMsg.setJMSReplyTo(serverQueue);
            objMsg.setJMSCorrelationID(String.valueOf(req.getId()));

            // Log the outgoing request
            System.out.println("Server sending " + req);
            producer.send(s3Queue, objMsg);

            // Create consumer to receive response
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
            Logger.getLogger(Podsistem3Resource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to process request due to JMS exception").build();
        }

    }

    @Produces(MediaType.APPLICATION_JSON)
    @DELETE
    @Path("deleteOcena/{sifV}/{sifK}")
    public Response deleteOcena(
            @PathParam("sifV") int sifV,
            @PathParam("sifK") int sifK) {

        try {
            // Create JMS context and producer
            JMSContext context = factory.createContext();
            JMSProducer producer = context.createProducer();

            ServerRequest req = new ObrisiOcenuRequest(sifV, sifK);
            ObjectMessage objMsg = context.createObjectMessage(req);
            objMsg.setJMSReplyTo(serverQueue);
            objMsg.setJMSCorrelationID(String.valueOf(req.getId()));

            // Log the outgoing request
            System.out.println("Server sending " + req);
            producer.send(s3Queue, objMsg);

            // Create consumer to receive response
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
            Logger.getLogger(Podsistem3Resource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to process request due to JMS exception").build();
        }

    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("getGledanja/{sifV}")
    public Response getGledanja(@PathParam("sifV") int sifV) {
        try {
            JMSContext context = factory.createContext();
            JMSProducer producer = context.createProducer();

            ServerRequest req = new DohvatiGledanjaVideaRequest(sifV);
            ObjectMessage objMsg = context.createObjectMessage(req);
            objMsg.setJMSReplyTo(serverQueue);
            objMsg.setJMSCorrelationID(String.valueOf(req.getId()));

            // Log the outgoing request
            System.out.println("Server sending " + req);
            producer.send(s3Queue, objMsg);

            JMSConsumer consumer = context.createConsumer(serverQueue, "JMSCorrelationID = '" + req.getId() + "'");
            Message reply = consumer.receive(5000);

            if (!(reply instanceof ObjectMessage)) {
                System.err.println("Received message is not an ObjectMessage: " + reply);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Received unexpected message type from server").build();
            }

            ObjectMessage objResponse = (ObjectMessage) reply;
            Serializable obj = objResponse.getObject();

            if (!(obj instanceof JMSResponse)) {
                System.err.println("Response is not a JMSResponse: " + obj);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Received unexpected response type from server").build();
            }

            if (obj instanceof ErrorResponse) {
                ErrorResponse error = (ErrorResponse) obj;
                System.out.println("Error response: " + error.getReason());
                return Response.status(Response.Status.BAD_REQUEST).entity(error.getReason()).build();
            }

            if (!(obj instanceof DataResponse)) {
                System.err.println("Response is not a DataResponse: " + obj);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Received unexpected response type from server").build();
            }

            DataResponse dataResponse = (DataResponse) obj;
            List<Gledanje> gledanja = (List<Gledanje>) dataResponse.getData();
            System.out.println("Response: " + gledanja);
            return Response.status(Response.Status.OK).entity(gledanja).build();

        } catch (JMSException ex) {
            Logger.getLogger(Podsistem3Resource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to process request due to JMS exception").build();
        }
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("getOcene/{sifV}")
    public Response getOcene(@PathParam("sifV") int sifV) {
        try {
            // Create JMS context and producer
            JMSContext context = factory.createContext();
            JMSProducer producer = context.createProducer();

            // Create and send JMS request message
            ServerRequest req = new DohvatiOceneVideaRequest(sifV);
            ObjectMessage objMsg = context.createObjectMessage(req);
            objMsg.setJMSReplyTo(serverQueue);
            objMsg.setJMSCorrelationID(String.valueOf(req.getId()));

            // Log the outgoing request
            System.out.println("Server sending " + req);
            producer.send(s3Queue, objMsg);

            // Create JMS consumer to receive response
            JMSConsumer consumer = context.createConsumer(serverQueue, "JMSCorrelationID = '" + req.getId() + "'");
            Message reply = consumer.receive(5000);

            // Check if the received message is of the expected type
            if (!(reply instanceof ObjectMessage)) {
                System.err.println("Received message is not an ObjectMessage: " + reply);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Received unexpected response type from server").build();
            }

            // Extract response object and handle based on type
            ObjectMessage objResponse = (ObjectMessage) reply;
            Serializable obj = objResponse.getObject();

            if (!(obj instanceof JMSResponse)) {
                System.err.println("Response is not a JMSResponse: " + obj);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Received unexpected response type from server").build();
            }

            if (obj instanceof ErrorResponse) {
                ErrorResponse error = (ErrorResponse) obj;
                System.out.println("Error response: " + error.getReason());
                return Response.status(Response.Status.BAD_REQUEST).entity(error.getReason()).build();
            }

            // Check if the response is a DataResponse
            if (!(obj instanceof DataResponse)) {
                System.err.println("Response is not a DataResponse: " + obj);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Received unexpected response type from server").build();
            }

            // Process successful DataResponse
            System.out.println("Server received response: " + obj);
            DataResponse data = (DataResponse) obj;

            //System.out.println(obj.toString());
            List<Ocena> ocene = (List<Ocena>) data.getData();
            System.out.println("Ocene: " + ocene);
            return Response.status(Response.Status.OK).entity(ocene).build();

        } catch (JMSException ex) {
            Logger.getLogger(Podsistem3Resource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to process request due to JMS exception").build();
        }
    }

}
