/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.is1.projekat.handlers;

import etf.is1.projekat.entities.Kategorija;
import etf.is1.projekat.entities.Pripada;
import etf.is1.projekat.entities.Video;
import etf.is1.projekat.responses.ErrorResponse;
import etf.is1.projekat.server.requests.ServerRequest;
import etf.is1.projekat.responses.JMSResponse;
import etf.is1.projekat.responses.OKResponse;
import etf.is1.projekat.server.requests.DodajKategorijuZaVideoRequest;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 *
 * @author ninaj
 */
public class DodavanjeKategorijeHandler extends RequestHandler {

    public DodavanjeKategorijeHandler(EntityManager em) {
        super(em);
    }

    @Override
    public JMSResponse handle(ServerRequest req) {
        DodajKategorijuZaVideoRequest promena = (DodajKategorijuZaVideoRequest) req;
        int novaSifKat = promena.getSifKat();
        int sifV = promena.getSifV();
        System.out.println("Handling request: " + req.getRequest() + " Video ID: " + sifV + ", Kategorija ID: " + novaSifKat);

        EntityTransaction transaction = null;
        try {
            transaction = em.getTransaction();
            transaction.begin();

            // Pronadji video
            Video video = em.find(Video.class, sifV);
            if (video == null) {
                String errorMessage = "Ne postoji video sa tim ID-om.";
                System.out.println(errorMessage);
                return new ErrorResponse(req, errorMessage);
            }

            // Pronadji kategoriju
            Kategorija kategorija = em.find(Kategorija.class, novaSifKat);
            if (kategorija == null) {
                String errorMessage = "Ne postoji kategorija sa tim ID-om.";
                System.out.println(errorMessage);
                return new ErrorResponse(req, errorMessage);
            }

            // Provera da li video vec ima zadatu kategoriju
            boolean alreadyExists = video.getPripadaList().stream()
                    .anyMatch(p -> p.getSifKatid().getSifKat() == novaSifKat);
            if (alreadyExists) {
                String errorMessage = "Snimak sa tim ID-jem već ima tu kategoriju.";
                System.out.println(errorMessage);
                return new ErrorResponse(req, errorMessage);
            }

            // Kreiranje nove veze izmedju videa i kategorije
            Pripada pripada = new Pripada();
            pripada.setSifKatid(kategorija);
            pripada.setSifVid(video);

            // Persist the new relationship
            em.persist(pripada);

            // Commit the transaction
            transaction.commit();
            return new OKResponse(req);

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            String errorMessage = "Došlo je do greške pri obradi zahteva: " + e.getMessage();
            System.out.println(errorMessage);
            return new ErrorResponse(req, errorMessage);
        }
    }

}
