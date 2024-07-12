/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.is1.projekat.handlers;

import etf.is1.projekat.entities.subsystem3.Paket;
import etf.is1.projekat.responses.DataResponse;
import etf.is1.projekat.responses.ErrorResponse;
import etf.is1.projekat.server.requests.ServerRequest;
import etf.is1.projekat.responses.JMSResponse;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author ninaj
 */
public class DohvatanjeSvihPaketaHandler extends RequestHandler {

    public DohvatanjeSvihPaketaHandler(EntityManager em) {
        super(em);
    }

    @Override
    public JMSResponse handle(ServerRequest req) {
        try {
            // Fetch all packages from the database
            List<Paket> paketi = em.createNamedQuery("Paket.findAll", Paket.class).getResultList();

            // Log the fetched packages
            System.out.println("Svi paketi: " + paketi);

            // Create a response with the fetched packages
            DataResponse<List<Paket>> response = new DataResponse<>(req, paketi);
            return response;
        } catch (Exception e) {
            // Log the error and return an error response
            String errorMessage = "Došlo je do greške pri obradi zahteva: " + e.getMessage();
            System.out.println(errorMessage);
            return new ErrorResponse(req, errorMessage);
        }
    }

}
