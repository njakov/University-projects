/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.is1.projekat.handlers;

import etf.is1.projekat.entities.Korisnik;
import etf.is1.projekat.entities.Pripada;
import etf.is1.projekat.entities.Video;
import etf.is1.projekat.responses.ErrorResponse;
import etf.is1.projekat.responses.JMSResponse;
import etf.is1.projekat.responses.OKResponse;
import etf.is1.projekat.server.requests.ObrisiVideoRequest;
import etf.is1.projekat.server.requests.ServerRequest;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 *
 * @author ninaj
 */
public class BrisanjeVideaHandler extends RequestHandler {

    public BrisanjeVideaHandler(EntityManager em) {
        super(em);
    }

    @Override
    public JMSResponse handle(ServerRequest req) {
        ObrisiVideoRequest temp = (ObrisiVideoRequest) req;
        int sifV = temp.getSifV();
        int sifK = temp.getSifK();
        System.out.println("Handling request: " + req.getRequest() + " Video ID: " + sifV + ", Korisnik ID: " + sifK);

        EntityTransaction transaction = null;
        try {
            transaction = em.getTransaction();
            transaction.begin();

            // Find the video
            Video video = em.find(Video.class, sifV);
            if (video == null) {
                String errorMessage = "Ne postoji video sa tim ID-om.";
                System.out.println(errorMessage);
                return new ErrorResponse(req, errorMessage);
            }

            // Find the korisnik
            Korisnik korisnik = em.find(Korisnik.class, sifK);
            if (korisnik == null) {
                String errorMessage = "Ne postoji korisnik sa tim ID-om.";
                System.out.println(errorMessage);
                return new ErrorResponse(req, errorMessage);
            }

            // Remove all Pripada relationships associated with the video
            Pripada pripada = null;
            video.getPripadaList().forEach(p -> {
                em.remove(p);
            });

            // Remove the video itself
            em.remove(video);

            // Commit the transaction
            transaction.commit();
            return new OKResponse(req);

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            String errorMessage = "Došlo je do greške pri obradi zahteva: " + e.getMessage();
            System.out.println(errorMessage);
            return new ErrorResponse(req, errorMessage);
        }
    }

}
