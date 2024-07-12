/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.is1.projekat.handlers;

import etf.is1.projekat.entities.subsystem3.Ocena;
import etf.is1.projekat.entities.subsystem3.Video;
import etf.is1.projekat.responses.DataResponse;
import etf.is1.projekat.responses.ErrorResponse;
import etf.is1.projekat.responses.JMSResponse;
import etf.is1.projekat.server.requests.DohvatiOceneVideaRequest;
import etf.is1.projekat.server.requests.ServerRequest;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

/**
 *
 * @author ninaj
 */
public class DohvatanjeOcenaZaSnimakHandler extends RequestHandler {

    public DohvatanjeOcenaZaSnimakHandler(EntityManager em) {
        super(em);
    }

    @Override
    public JMSResponse handle(ServerRequest req) {
        DohvatiOceneVideaRequest temp = (DohvatiOceneVideaRequest) req;
        int sifV = temp.getSifV();

        System.out.println("Request: " + req.getRequest() + " Video: " + sifV);
        
        try {
            // Check if the video exists in the database
            Video video = em.find(Video.class, sifV);
            if (video == null) {
                System.out.println("Ne postoji video sa tim ID-om.");
                return new ErrorResponse(req, "Ne postoji video sa tim ID-om.");
            }

            // Use a typed query to fetch ratings (Ocena) for the given video ID
            TypedQuery<Ocena> query = em.createNamedQuery("Ocena.findBySifV", Ocena.class);
            query.setParameter("sifV", sifV);
            List<Ocena> ocene = query.getResultList();

            // Create a DataResponse containing the list of ratings (Ocena)
            DataResponse<List<Ocena>> response = new DataResponse<>(req, ocene);
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return new ErrorResponse(req, "Došlo je do greške pri obradi zahteva.");
        }
    }


}
