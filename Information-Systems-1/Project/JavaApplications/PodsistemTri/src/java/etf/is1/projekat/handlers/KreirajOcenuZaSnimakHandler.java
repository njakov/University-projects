/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.is1.projekat.handlers;

import etf.is1.projekat.entities.subsystem3.Korisnik;
import etf.is1.projekat.entities.subsystem3.Ocena;
import etf.is1.projekat.entities.subsystem3.OcenaPK;
import etf.is1.projekat.entities.subsystem3.Video;
import etf.is1.projekat.responses.ErrorResponse;
import etf.is1.projekat.responses.JMSResponse;
import etf.is1.projekat.responses.OKResponse;
import etf.is1.projekat.server.requests.KreirajOcenuRequest;
import etf.is1.projekat.server.requests.ServerRequest;
import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 *
 * @author ninaj
 */
public class KreirajOcenuZaSnimakHandler extends RequestHandler {

    public KreirajOcenuZaSnimakHandler(EntityManager em) {
        super(em);
    }

    @Override
    public JMSResponse handle(ServerRequest req) {
        KreirajOcenuRequest ocenaRequest = (KreirajOcenuRequest) req;

        int sifK = ocenaRequest.getSifK();
        int sifV = ocenaRequest.getSifV();
        int ocenaValue = ocenaRequest.getOcena();

        System.out.println("Request: " + req.getRequest() + " Korisnik ID: " + sifK + ", Video ID: " + sifV + ", Ocena: " + ocenaValue);

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

            // Check if the user has already rated this video
            for (Ocena o : video.getOcenaList()) {
                if (o.getKorisnik().getSifK().equals(sifK)) {
                    String errorMessage = "Korisnik sa tim ID-jem je vec ocenio taj video.";
                    System.out.println(errorMessage);
                    return new ErrorResponse(req, errorMessage);
                }
            }

            // Create a new rating
            Ocena novaOcena = new Ocena();
            OcenaPK ocenaPK = new OcenaPK(sifK, sifV);
            novaOcena.setKorisnik(korisnik);
            novaOcena.setVideo(video);
            novaOcena.setOcena(ocenaValue);
            novaOcena.setDatumVreme(new Date());
            novaOcena.setOcenaPK(ocenaPK);

            // Start transaction, persist the new rating, and commit the transaction
            EntityTransaction transaction = em.getTransaction();
            try {
                transaction.begin();
                em.persist(novaOcena);
                transaction.commit();
                System.out.println("Ocena uspešno kreirana.");
                return new OKResponse(req);
            } catch (Exception e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                String errorMessage = "Došlo je do greške pri kreiranju ocene: " + e.getMessage();
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
