/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.is1.projekat.handlers;

import etf.is1.projekat.entities.subsystem3.Korisnik;
import etf.is1.projekat.entities.subsystem3.Ocena;
import etf.is1.projekat.entities.subsystem3.Video;
import etf.is1.projekat.responses.ErrorResponse;
import etf.is1.projekat.responses.JMSResponse;
import etf.is1.projekat.responses.OKResponse;
import etf.is1.projekat.server.requests.PromeniOcenuSnimkaRequest;
import etf.is1.projekat.server.requests.ServerRequest;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 *
 * @author ninaj
 */
public class PromeniOcenuZaSnimakHandler extends RequestHandler {

    public PromeniOcenuZaSnimakHandler(EntityManager em) {
        super(em);
    }

    @Override
    public JMSResponse handle(ServerRequest req) {
        PromeniOcenuSnimkaRequest temp = (PromeniOcenuSnimkaRequest) req;
        int sifK = temp.getSifK();
        int sifV = temp.getSifV();
        int ocena = temp.getOcena();

        System.out.println("Request: " + req.getRequest() + " Video ID: " + sifV + ", Korisnik ID: " + sifK + ", Ocena: " + ocena);

        try {
            // Check if the video exists
            Video video = em.find(Video.class, sifV);
            if (video == null) {
                String errorMessage = "Ne postoji video sa tim ID-om.";
                System.out.println(errorMessage);
                return new ErrorResponse(req, errorMessage);
            }

            // Check if the korisnik exists
            Korisnik korisnik = em.find(Korisnik.class, sifK);
            if (korisnik == null) {
                String errorMessage = "Ne postoji korisnik sa tim ID-om.";
                System.out.println(errorMessage);
                return new ErrorResponse(req, errorMessage);
            }

            // Check if the korisnik has already rated the video
            boolean found = false;
            for (Ocena o : video.getOcenaList()) {
                if (o.getKorisnik().getSifK().equals(sifK)) {
                    // Start transaction
                    EntityTransaction transaction = em.getTransaction();
                    try {
                        transaction.begin();
                        o.setOcena(ocena);
                        em.merge(o);
                        transaction.commit();
                    } catch (Exception e) {
                        if (transaction.isActive()) {
                            transaction.rollback();
                        }
                        String errorMessage = "Došlo je do greške pri obradi zahteva: " + e.getMessage();
                        System.out.println(errorMessage);
                        return new ErrorResponse(req, errorMessage);
                    }
                    found = true;
                    break;
                }
            }

            if (!found) {
                String errorMessage = "Ocena nije pronađena za korisnika sa tim ID-om.";
                System.out.println(errorMessage);
                return new ErrorResponse(req, errorMessage);
            }

            return new OKResponse(req);

        } catch (Exception e) {
            String errorMessage = "Došlo je do greške pri obradi zahteva: " + e.getMessage();
            System.out.println(errorMessage);
            return new ErrorResponse(req, errorMessage);
        }
    }

}
