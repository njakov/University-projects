/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.is1.projekat.handlers;

import etf.is1.projekat.entities.subsystem3.Gledanje;
import etf.is1.projekat.entities.subsystem3.Korisnik;
import etf.is1.projekat.entities.subsystem3.Video;
import etf.is1.projekat.responses.ErrorResponse;
import etf.is1.projekat.responses.JMSResponse;
import etf.is1.projekat.responses.OKResponse;
import etf.is1.projekat.server.requests.KreirajGledanjeRequest;
import etf.is1.projekat.server.requests.ServerRequest;
import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 *
 * @author ninaj
 */
public class KreirajGledanjeHandler extends RequestHandler {

    public KreirajGledanjeHandler(EntityManager em) {
        super(em);
    }

    @Override
    public JMSResponse handle(ServerRequest req) {
        KreirajGledanjeRequest gledanjeRequest = (KreirajGledanjeRequest) req;

        int sifK = gledanjeRequest.getSifK();
        int sifV = gledanjeRequest.getSifV();
        int zapoceto = gledanjeRequest.getZapoceto();
        int odgledano = gledanjeRequest.getOdgledano();

        System.out.println("Request: " + req.getRequest() + " Korisnik ID: " + sifK + ", Video ID: " + sifV
                + ", Zapoceto: " + zapoceto + ", Odgledano: " + odgledano);

        try {
            // Find the video by ID
            Video video = em.find(Video.class, sifV);
            if (video == null) {
                String errorMessage = "Ne postoji video sa tim ID-om.";
                System.out.println(errorMessage);
                return new ErrorResponse(req, errorMessage);
            }

            // Find the user by ID
            Korisnik korisnik = em.find(Korisnik.class, sifK);
            if (korisnik == null) {
                String errorMessage = "Ne postoji korisnik sa tim ID-om.";
                System.out.println(errorMessage);
                return new ErrorResponse(req, errorMessage);
            }

            // Create a new viewing record
            Gledanje gledanje = new Gledanje();
            gledanje.setSifK(korisnik);
            gledanje.setSifV(video);
            gledanje.setDatumVreme(new Date());
            gledanje.setZapoceto(zapoceto);
            gledanje.setOdgledano(odgledano);

            // Start transaction, persist the new viewing record, and commit the transaction
            EntityTransaction transaction = em.getTransaction();
            try {
                transaction.begin();
                em.persist(gledanje);
                transaction.commit();
                System.out.println("Gledanje uspešno kreirano.");
                return new OKResponse(req);
            } catch (Exception e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                String errorMessage = "Došlo je do greške pri kreiranju gledanja: " + e.getMessage();
                System.out.println(errorMessage);
                return new ErrorResponse(req, errorMessage);
            }
        } catch (Exception e) {
            String errorMessage = "Došlo je do greške pri obradi zahteva: " + e.getMessage();
            System.out.println(errorMessage);
            return new ErrorResponse(req, errorMessage);
        }
    }

}
