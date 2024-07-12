/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.is1.projekat.handlers;

import etf.is1.projekat.entities.subsystem3.Gledanje;
import etf.is1.projekat.entities.subsystem3.Video;
import etf.is1.projekat.responses.DataResponse;
import etf.is1.projekat.responses.ErrorResponse;
import etf.is1.projekat.responses.JMSResponse;
import etf.is1.projekat.server.requests.DohvatiGledanjaVideaRequest;
import etf.is1.projekat.server.requests.ServerRequest;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

/**
 *
 * @author ninaj
 */
public class DohvatanjeSvihGledanjaHandler extends RequestHandler {

    public DohvatanjeSvihGledanjaHandler(EntityManager em) {
        super(em);
    }

    @Override
    public JMSResponse handle(ServerRequest req) {
        DohvatiGledanjaVideaRequest request = (DohvatiGledanjaVideaRequest) req;
        int sifV = request.getSifV();

        System.out.println("Request: " + req.getRequest() + " Video: " + sifV);

        try {
            // Find the video by its ID
            Video video = em.find(Video.class, sifV);

            if (video == null) {
                System.out.println("Ne postoji video sa tim ID-om.");
                return new ErrorResponse(req, "Ne postoji video sa tim ID-om.");
            }

            // Construct the query to fetch video views
            String queryString = "SELECT g FROM Gledanje g WHERE g.sifV.sifV = :sifV";

            // Create a typed query for Gledanje entities
            TypedQuery<Gledanje> typedQuery = em.createQuery(queryString, Gledanje.class);
            typedQuery.setParameter("sifV", sifV);

            // Execute the query to get the list of Gledanje entities
            List<Gledanje> gledanja = typedQuery.getResultList();

            // Log the fetched views
            System.out.println("Sva gledanja videa sa ID: " + sifV + ": " + gledanja.size());

            // Create a DataResponse containing the list of Gledanje
            DataResponse<List<Gledanje>> response = new DataResponse<>(req, gledanja);
            return response;
        } catch (Exception e) {
            // Log any exceptions and return an error response
            e.printStackTrace();
            return new ErrorResponse(req, "Došlo je do greške pri obradi zahteva.");
        }
    }

}
