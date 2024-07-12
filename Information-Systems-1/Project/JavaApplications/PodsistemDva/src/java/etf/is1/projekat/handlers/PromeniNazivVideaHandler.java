/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.is1.projekat.handlers;

import etf.is1.projekat.entities.Video;
import etf.is1.projekat.responses.ErrorResponse;
import etf.is1.projekat.server.requests.ServerRequest;
import etf.is1.projekat.responses.JMSResponse;
import etf.is1.projekat.responses.OKResponse;
import etf.is1.projekat.server.requests.PromeniNazivVideaRequest;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author ninaj
 */
public class PromeniNazivVideaHandler extends RequestHandler {

    public PromeniNazivVideaHandler(EntityManager em) {
        super(em);
    }

    @Override
    public JMSResponse handle(ServerRequest req) {
        PromeniNazivVideaRequest promena = (PromeniNazivVideaRequest) req;
        System.out.println("Request: " + req.getRequest() + " Video ID: " + promena.getSifV() + ", Naziv: " + promena.getNaziv());

        // Fetch the video by ID
        Video video = em.find(Video.class, promena.getSifV());
        if (video == null) {
            String errorMessage = "Ne postoji video sa zadatim ID-om.";
            System.out.println(errorMessage);
            return new ErrorResponse(req, errorMessage);
        }

        // Check if a video with the new naziv already exists
        List<Video> existingVideos = em.createNamedQuery("Video.findByNaziv", Video.class)
                                    .setParameter("naziv", promena.getNaziv())
                                    .getResultList();
        if (!existingVideos.isEmpty()) {
            String errorMessage = "Postoji video sa istim nazivom.";
            System.out.println(errorMessage);
            return new ErrorResponse(req, errorMessage);
        }

        // Update the naziv of the video
        video.setNaziv(promena.getNaziv());

        // Persist the changes
        try {
            this.em.getTransaction().begin();
            this.em.merge(video); // merge the updated entity
            this.em.getTransaction().commit();
        } catch (Exception e) {
            this.em.getTransaction().rollback();
            String errorMessage = "Transaction failed: " + e.getMessage();
            System.out.println(errorMessage);
            return new ErrorResponse(req, errorMessage);
        }

        return new OKResponse(req);
    }

}
