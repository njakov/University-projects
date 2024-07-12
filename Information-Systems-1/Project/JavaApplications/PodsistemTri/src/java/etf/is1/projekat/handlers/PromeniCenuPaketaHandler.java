/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.is1.projekat.handlers;

import etf.is1.projekat.entities.subsystem3.Paket;
import etf.is1.projekat.responses.ErrorResponse;
import etf.is1.projekat.server.requests.ServerRequest;
import etf.is1.projekat.responses.JMSResponse;
import etf.is1.projekat.responses.OKResponse;
import etf.is1.projekat.server.requests.PromeniCenuPaketaRequest;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 *
 * @author ninaj
 */
public class PromeniCenuPaketaHandler extends RequestHandler {

    public PromeniCenuPaketaHandler(EntityManager em) {
        super(em);
    }

    @Override
    public JMSResponse handle(ServerRequest req) {
        PromeniCenuPaketaRequest promena = (PromeniCenuPaketaRequest) req;
        int sifPak = promena.getSifPak();
        int novaCena = promena.getCena();

        System.out.println("Request: " + req.getRequest() + " Paket: " + sifPak + ", Cena: " + novaCena);

        try {
            // Check if the package exists
            Paket paket = em.find(Paket.class, sifPak);
            if (paket == null) {
                String errorMessage = "Ne postoji paket sa tim ID-om.";
                System.out.println(errorMessage);
                return new ErrorResponse(req, errorMessage);
            }

            // Update the package price
            paket.setCena(novaCena);

            // Start transaction
            EntityTransaction transaction = em.getTransaction();
            try {
                transaction.begin();
                em.merge(paket); // Merge the updated entity
                transaction.commit();
                System.out.println("Cena paketa uspešno promenjena.");
                return new OKResponse(req);
            } catch (Exception e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                String errorMessage = "Došlo je do greške pri obradi zahteva: " + e.getMessage();
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
