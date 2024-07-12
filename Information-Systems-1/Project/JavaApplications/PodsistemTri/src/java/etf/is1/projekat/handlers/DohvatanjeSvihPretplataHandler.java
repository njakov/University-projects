/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.is1.projekat.handlers;

import etf.is1.projekat.entities.subsystem3.Korisnik;
import etf.is1.projekat.entities.subsystem3.Pretplata;
import etf.is1.projekat.responses.DataResponse;
import etf.is1.projekat.responses.ErrorResponse;
import etf.is1.projekat.server.requests.ServerRequest;
import etf.is1.projekat.responses.JMSResponse;
import etf.is1.projekat.server.requests.DohvatiPretplateKorisnikaRequest;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author ninaj
 */
public class DohvatanjeSvihPretplataHandler extends RequestHandler {

    public DohvatanjeSvihPretplataHandler(EntityManager em) {
        super(em);
    }

    @Override
    public JMSResponse handle(ServerRequest req) {
        DohvatiPretplateKorisnikaRequest request = (DohvatiPretplateKorisnikaRequest) req;
        int sifK = request.getSifK();

        System.out.println("Request: " + req.getRequest() + " Korisnik ID: " + sifK);

        try {
            // Find the user by ID
            Korisnik korisnik = em.find(Korisnik.class, sifK);
            if (korisnik == null) {
                String errorMessage = "Ne postoji korisnik sa tim ID-om.";
                System.out.println(errorMessage);
                return new ErrorResponse(req, errorMessage);
            }

            // Query to fetch subscriptions for the user
            String query = "SELECT p FROM Pretplata p WHERE p.sifK = :sifK";
            List<Pretplata> pretplate = em.createQuery(query, Pretplata.class)
                    .setParameter("sifK", korisnik)
                    .getResultList();

            // Check if the user has subscriptions
            if (pretplate != null && !pretplate.isEmpty()) {
                System.out.println("Pretplate korisnika: " + pretplate);
                DataResponse<List<Pretplata>> response = new DataResponse<>(req, pretplate);
                return response;
            } else {
                String errorMessage = "Nema pretplata za korisnika.";
                System.out.println(errorMessage);
                return new ErrorResponse(req, errorMessage);
            }
        } catch (Exception e) {
            String errorMessage = "Došlo je do greške pri obradi zahteva: " + e.getMessage();
            System.out.println(errorMessage);
            return new ErrorResponse(req, errorMessage);
        }
    }

}
