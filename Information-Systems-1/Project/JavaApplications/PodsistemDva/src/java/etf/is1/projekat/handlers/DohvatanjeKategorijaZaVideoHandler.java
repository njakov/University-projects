/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.is1.projekat.handlers;

import etf.is1.projekat.entities.Kategorija;
import etf.is1.projekat.entities.Video;
import etf.is1.projekat.responses.DataResponse;
import etf.is1.projekat.responses.ErrorResponse;
import etf.is1.projekat.responses.JMSResponse;
import etf.is1.projekat.server.requests.DohvatiKategorijeZaVideoRequest;
import etf.is1.projekat.server.requests.ServerRequest;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author ninaj
 */
public class DohvatanjeKategorijaZaVideoHandler extends RequestHandler {

    public DohvatanjeKategorijaZaVideoHandler(EntityManager em) {
        super(em);
    }

    @Override
    public JMSResponse handle(ServerRequest req) {
        DohvatiKategorijeZaVideoRequest temp = (DohvatiKategorijeZaVideoRequest) req;
        System.out.println("Handling request: " + req.getRequest() + " Sifra videa: " + temp.getSifV());

        // Find the video by its ID
        Video video = em.find(Video.class, temp.getSifV());
        if (video == null) {
            String errorMessage = "Ne postoji video sa tim ID-om.";
            System.out.println(errorMessage);
            return new ErrorResponse(req, errorMessage);
        }

        // Query to fetch categories associated with the video
        String query = "SELECT k FROM Kategorija k LEFT OUTER JOIN k.pripadaList p WHERE p.sifVid = :sifVid";
        List<Kategorija> kategorije = em.createQuery(query, Kategorija.class)
                                        .setParameter("sifVid", video)
                                        .getResultList();

        // Check if categories exist and return appropriate response
        if (!kategorije.isEmpty()) {
            System.out.println("Kategorije izabranog videa: " + kategorije);
            return new DataResponse<>(req, kategorije);
        } else {
            String errorMessage = "Nema kategorija za taj snimak.";
            System.out.println(errorMessage);
            return new ErrorResponse(req, errorMessage);
        }
    }

}
