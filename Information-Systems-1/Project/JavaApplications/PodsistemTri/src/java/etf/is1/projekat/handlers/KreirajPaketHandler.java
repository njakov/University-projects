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
import etf.is1.projekat.server.requests.KreirajPaketRequest;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 *
 * @author ninaj
 */
public class KreirajPaketHandler extends RequestHandler {

    public KreirajPaketHandler(EntityManager em) {
        super(em);
    }

    @Override
    public JMSResponse handle(ServerRequest req) {
        KreirajPaketRequest paketRequest = (KreirajPaketRequest) req;

        String nazivPaketa = paketRequest.getNaziv();
        int cenaPaketa = paketRequest.getCena();

        System.out.println("Request: " + req.getRequest() + " Naziv paketa: " + nazivPaketa + ", Cena: " + cenaPaketa);

        try {
            // Check if a package with the same name already exists
            List<Paket> existingPaketi = em.createNamedQuery("Paket.findByNaziv", Paket.class)
                    .setParameter("naziv", nazivPaketa)
                    .getResultList();
            if (!existingPaketi.isEmpty()) {
                String errorMessage = "Postoji paket sa tim nazivom.";
                System.out.println(errorMessage);
                return new ErrorResponse(req, errorMessage);
            }

            // Create and persist the new package
            Paket newPaket = new Paket();
            newPaket.setNaziv(nazivPaketa);
            newPaket.setCena(cenaPaketa);

            EntityTransaction transaction = em.getTransaction();
            try {
                transaction.begin();
                em.persist(newPaket);
                transaction.commit();
                System.out.println("Paket uspešno kreiran.");
                return new OKResponse(req);
            } catch (Exception e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                String errorMessage = "Došlo je do greške pri kreiranju paketa: " + e.getMessage();
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
