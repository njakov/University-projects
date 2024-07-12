/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.is1.projekat.handlers;

import etf.is1.projekat.entities.Korisnik;
import etf.is1.projekat.entities.Mesto;
import etf.is1.projekat.server.requests.KreirajKorisnikaRequest;
import javax.persistence.EntityManager;
import etf.is1.projekat.server.requests.ServerRequest;
import etf.is1.projekat.responses.ErrorResponse;
import etf.is1.projekat.responses.JMSResponse;
import etf.is1.projekat.responses.OKResponse;
import java.util.List;

/**
 *
 * @author ninaj
 */
public class KreirajKorisnikaHandler extends RequestHandler {

    public KreirajKorisnikaHandler(EntityManager em) {
        super(em);
    }

    @Override
    public JMSResponse handle(ServerRequest req) {

        KreirajKorisnikaRequest korisnik = (KreirajKorisnikaRequest) req;
        System.out.println("Request: " + req.getRequest() + " Email: " + korisnik.getEmail() + ", Mesto: " + korisnik.getMesto());

        // Check if a korisnik with the given email already exists
        List<Korisnik> existing = em.createNamedQuery("Korisnik.findByEmail", Korisnik.class)
                .setParameter("email", korisnik.getEmail())
                .getResultList();
        if (!existing.isEmpty()) {
            String errorMessage = "Postoji korisnik sa tim email-om.";
            System.out.println(errorMessage);
            return new ErrorResponse(req, errorMessage);
        }

        // Check if the specified mesto exists
        List<Mesto> existingMesto = em.createNamedQuery("Mesto.findByNaziv", Mesto.class)
                .setParameter("naziv", korisnik.getMesto())
                .getResultList();
        if (existingMesto.size() != 1) {
            String errorMessage = "Ne postoji mesto sa tim nazivom. Kreirajte mesto.";
            System.out.println(errorMessage);
            return new ErrorResponse(req, errorMessage);
        }

        // Create and persist new Korisnik
        Korisnik newKorisnik = new Korisnik();
        newKorisnik.setIme(korisnik.getIme());
        newKorisnik.setEmail(korisnik.getEmail());
        newKorisnik.setGodiste(korisnik.getGodiste());
        newKorisnik.setPol(korisnik.getPol());
        newKorisnik.setSifM(existingMesto.get(0));

        try {
            this.em.getTransaction().begin();
            this.em.persist(newKorisnik);
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
