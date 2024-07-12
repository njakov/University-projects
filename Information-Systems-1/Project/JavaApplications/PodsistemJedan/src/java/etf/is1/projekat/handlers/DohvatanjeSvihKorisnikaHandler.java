/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.is1.projekat.handlers;

import etf.is1.projekat.entities.Korisnik;
import etf.is1.projekat.server.requests.ServerRequest;
import etf.is1.projekat.responses.DataResponse;
import etf.is1.projekat.responses.ErrorResponse;
import etf.is1.projekat.responses.JMSResponse;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author ninaj
 */
public class DohvatanjeSvihKorisnikaHandler extends RequestHandler {

    public DohvatanjeSvihKorisnikaHandler(EntityManager em) {
        super(em);
    }

    @Override
    public JMSResponse handle(ServerRequest req) {
        System.out.println("Handling request: " + req.getRequest());

        // Fetch all korisnici
        List<Korisnik> korisnici;
        try {
            korisnici = this.em.createNamedQuery("Korisnik.findAll", Korisnik.class).getResultList();
            System.out.println("Svi korisnici: " + korisnici);
        } catch (Exception e) {
            String errorMessage = "Failed to fetch korisnici: " + e.getMessage();
            System.out.println(errorMessage);
            return new ErrorResponse(req, errorMessage);
        }

        // Create and return response
        return new DataResponse<>(req, korisnici);
    }
}
