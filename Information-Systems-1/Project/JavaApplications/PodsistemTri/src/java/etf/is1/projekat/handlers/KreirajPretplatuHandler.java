/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.is1.projekat.handlers;

import etf.is1.projekat.entities.subsystem3.Korisnik;
import etf.is1.projekat.entities.subsystem3.Paket;
import etf.is1.projekat.entities.subsystem3.Pretplata;
import etf.is1.projekat.responses.ErrorResponse;
import etf.is1.projekat.server.requests.ServerRequest;
import etf.is1.projekat.responses.JMSResponse;
import etf.is1.projekat.responses.OKResponse;
import etf.is1.projekat.server.requests.KreirajPretplatuRequest;
import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 *
 * @author ninaj
 */
public class KreirajPretplatuHandler extends RequestHandler {

    public KreirajPretplatuHandler(EntityManager em) {
        super(em);
    }

    @Override
    public JMSResponse handle(ServerRequest req) {
        KreirajPretplatuRequest pretplata = (KreirajPretplatuRequest) req;
        int sifK = pretplata.getSifK();
        int sifPak = pretplata.getSifPak();

        System.out.println("Request: " + req.getRequest() + " Korisnik: " + sifK + ", Pretplata: " + sifPak);

        try {
            // Validate korisnik
            Korisnik korisnik = em.find(Korisnik.class, sifK);
            if (korisnik == null) {
                String errorMessage = "Ne postoji korisnik sa tim ID-om.";
                System.out.println(errorMessage);
                return new ErrorResponse(req, errorMessage);
            }

            // Validate paket
            Paket paket = em.find(Paket.class, sifPak);
            if (paket == null) {
                String errorMessage = "Ne postoji paket sa tim ID-om.";
                System.out.println(errorMessage);
                return new ErrorResponse(req, errorMessage);
            }

            // Check if the subscription already exists
            for (Pretplata p : korisnik.getPretplataList()) {
                if (p.getSifPak().getSifPak().equals(sifPak)) {
                    String errorMessage = "Korisnik sa tim ID-jem vec ima tu pretplatu.";
                    System.out.println(errorMessage);
                    return new ErrorResponse(req, errorMessage);
                }
            }

            // Create new subscription
            Pretplata p = new Pretplata();
            p.setSifK(korisnik);
            p.setSifPak(paket);
            p.setCena(paket.getCena());
            p.setDatumVreme(new Date());

            // Start transaction
            EntityTransaction transaction = em.getTransaction();
            try {
                transaction.begin();
                em.persist(p);
                transaction.commit();
                System.out.println("Pretplata uspešno kreirana.");
                return new OKResponse(req);
            } catch (Exception e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                String errorMessage = "Došlo je do greške pri kreiranju pretplate: " + e.getMessage();
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
