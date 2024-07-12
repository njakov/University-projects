/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.is1.projekat.handlers;

import etf.is1.projekat.entities.Korisnik;
import etf.is1.projekat.server.requests.PromeniEmailRequest;
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
public class PromeniEmailHandler extends RequestHandler {

    public PromeniEmailHandler(EntityManager em) {
        super(em);
    }

    @Override
    public JMSResponse handle(ServerRequest req) {
        
        PromeniEmailRequest promena = (PromeniEmailRequest) req;
        System.out.println("Request: " + req.getRequest() + " Korisnik: " + promena.getSifK() + ", mail: " + promena.getEmail());

        // Fetch korisnik by ID
        Korisnik korisnik = em.find(Korisnik.class, promena.getSifK());
        if (korisnik == null) {
            String errorMessage = "Ne postoji korisnik sa tim ID-om.";
            System.out.println(errorMessage);
            return new ErrorResponse(req, errorMessage);
        }

        // Check if email is already used
        List<Korisnik> korisnici = em.createNamedQuery("Korisnik.findByEmail", Korisnik.class)
                .setParameter("email", promena.getEmail())
                .getResultList();
        if (!korisnici.isEmpty()) {
            String errorMessage = "Postoji korisnik sa tim email-om.";
            System.out.println(errorMessage);
            return new ErrorResponse(req, errorMessage);
        }

        // Update the email
        korisnik.setEmail(promena.getEmail());

        try {
            this.em.getTransaction().begin();
            this.em.merge(korisnik); // merge the updated entity
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
