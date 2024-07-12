/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.is1.projekat.handlers;

import etf.is1.projekat.entities.Kategorija;
import etf.is1.projekat.server.requests.KreirajKategorijuRequest;
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
public class KreirajKategorijuHandler extends RequestHandler {

    public KreirajKategorijuHandler(EntityManager em) {
        super(em);
    }

    @Override
    public JMSResponse handle(ServerRequest req) {

        KreirajKategorijuRequest kategorija = (KreirajKategorijuRequest) req;
        System.out.println("Handling request: " + req.getRequest() + " Naziv: " + kategorija.getNaziv());

        // Check if a kategorija with the same naziv already exists
        List<Kategorija> existingKategorije = em.createNamedQuery("Kategorija.findByNaziv", Kategorija.class)
                                                .setParameter("naziv", kategorija.getNaziv())
                                                .getResultList();
        if (!existingKategorije.isEmpty()) {
            String errorMessage = "Postoji kategorija sa tim nazivom.";
            System.out.println(errorMessage);
            return new ErrorResponse(req, errorMessage);
        }

        // Create and persist the new Kategorija
        Kategorija newKategorija = new Kategorija();
        newKategorija.setNaziv(kategorija.getNaziv());

        try {
            em.getTransaction().begin();
            em.persist(newKategorija);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            String errorMessage = "Transaction failed: " + e.getMessage();
            System.out.println(errorMessage);
            return new ErrorResponse(req, errorMessage);
        }

        return new OKResponse(req);
    }

}
