/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.is1.projekat.handlers;

import etf.is1.projekat.entities.Korisnik;
import etf.is1.projekat.entities.Mesto;
import etf.is1.projekat.server.requests.PromeniMestoRequest;
import etf.is1.projekat.server.requests.ServerRequest;
import etf.is1.projekat.responses.ErrorResponse;
import etf.is1.projekat.responses.JMSResponse;
import etf.is1.projekat.responses.OKResponse;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author ninaj
 */
public class PromeniMestoHandler extends RequestHandler {

    public PromeniMestoHandler(EntityManager em) {
        super(em);
    }

    @Override
    public JMSResponse handle(ServerRequest req) {
        PromeniMestoRequest promena = (PromeniMestoRequest) req;
        System.out.println("Request: " + req.getRequest() + " Korisnik: " + promena.getSifK() + ", naziv: " + promena.getNaziv());

        // Fetch korisnik by ID
        Korisnik korisnik = em.find(Korisnik.class, promena.getSifK());
        if (korisnik == null) {
            String errorMessage = "Ne postoji korisnik sa tim ID-om.";
            System.out.println(errorMessage);
            return new ErrorResponse(req, errorMessage);
        }

        // Fetch mesta by naziv
        List<Mesto> mesta = em.createNamedQuery("Mesto.findByNaziv", Mesto.class)
                .setParameter("naziv", promena.getNaziv())
                .getResultList();
        if (mesta.isEmpty()) {
            String errorMessage = "Ne postoji mesto sa tim nazivom.";
            return new ErrorResponse(req, errorMessage);
        }

        // Update korisnik
        korisnik.setSifM(mesta.get(0));

        // Transaction to update the korisnik entity
        try {
            this.em.getTransaction().begin();
            this.em.merge(korisnik);
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
