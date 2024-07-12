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
import etf.is1.projekat.server.requests.ObrisiOcenuRequest;
import etf.is1.projekat.server.requests.ServerRequest;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 *
 * @author ninaj
 */
public class BrisanjeOceneHandler extends RequestHandler {

    public BrisanjeOceneHandler(EntityManager em) {
        super(em);
    }

    @Override
    public JMSResponse handle(ServerRequest req) {
        ObrisiOcenuRequest temp = (ObrisiOcenuRequest) req;
        int sifV = temp.getSifV();
        int sifK = temp.getSifK();

        System.out.println("Request: " + req.getRequest() + " Video ID: " + sifV + ", Korisnik ID: " + sifK);

        Video video = em.find(Video.class, sifV);
        if (video == null) {
            System.out.println("Ne postoji video sa tim ID-om.");
            return new ErrorResponse(req, "Ne postoji video sa tim ID-om.");
        }

        Korisnik korisnik = em.find(Korisnik.class, sifK);
        if (korisnik == null) {
            System.out.println("Ne postoji korisnik sa tim ID-om.");
            return new ErrorResponse(req, "Ne postoji korisnik sa tim ID-om.");
        }
        // Use try-finally for proper resource management
        EntityTransaction transaction = null;
        try {

            for (Ocena o : video.getOcenaList()) {
                if (o.getKorisnik().getSifK().equals(sifK)) {
                    this.em.getTransaction().begin();
                    this.em.remove(o);
                    this.em.getTransaction().commit();

                }
            }

            return new OKResponse(req);

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            return new ErrorResponse(req, "Došlo je do greške pri obradi zahteva.");
        }
    }
}
