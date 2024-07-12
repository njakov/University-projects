/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.is1.projekat.handlers;

import etf.is1.projekat.entities.Mesto;
import etf.is1.projekat.server.requests.KreirajMestoRequest;
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
public class KreirajMestoHandler extends RequestHandler {

    public KreirajMestoHandler(EntityManager em) {
        super(em);
    }

    @Override
    public JMSResponse handle(ServerRequest req) {

        KreirajMestoRequest mesto = (KreirajMestoRequest) req;
        System.out.println("Request: " + req.getRequest() + " Naziv: " + mesto.getNaziv());

        // Check if a mesto with the given naziv already exists
        List<Mesto> existing = em.createNamedQuery("Mesto.findByNaziv", Mesto.class)
                                .setParameter("naziv", mesto.getNaziv())
                                .getResultList();
        if (!existing.isEmpty()) {
            String errorMessage = "Postoji mesto sa tim nazivom.";
            System.out.println(errorMessage);
            return new ErrorResponse(req, errorMessage);
        }

        // Create and persist new Mesto
        Mesto newMesto = new Mesto();
        newMesto.setNaziv(mesto.getNaziv());

        try {
            this.em.getTransaction().begin();
            this.em.persist(newMesto);
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
