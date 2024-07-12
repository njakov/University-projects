/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.is1.projekat.handlers;

import etf.is1.projekat.entities.Korisnik;
import etf.is1.projekat.entities.Video;
import etf.is1.projekat.responses.ErrorResponse;
import etf.is1.projekat.server.requests.ServerRequest;
import etf.is1.projekat.responses.JMSResponse;
import etf.is1.projekat.responses.OKResponse;
import etf.is1.projekat.server.requests.KreirajVideoRequest;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author ninaj
 */
public class KreirajVideoHandler extends RequestHandler {

    public KreirajVideoHandler(EntityManager em) {
        super(em);
    }

    @Override
    public JMSResponse handle(ServerRequest req) {

        KreirajVideoRequest video = (KreirajVideoRequest) req;
        System.out.println("Handling request: " + req.getRequest() + " Naziv: " + video.getNaziv());

        // Check if a video with the same naziv already exists
        List<Video> existingVideos = em.createNamedQuery("Video.findByNaziv", Video.class)
                .setParameter("naziv", video.getNaziv())
                .getResultList();
        if (!existingVideos.isEmpty()) {
            String errorMessage = "Postoji video sa tim nazivom.";
            System.out.println(errorMessage);
            return new ErrorResponse(req, errorMessage);
        }

        // Fetch the korisnik by ID
        List<Korisnik> korisnici = em.createNamedQuery("Korisnik.findBySifK", Korisnik.class)
                .setParameter("sifK", video.getSifK())
                .getResultList();
        if (korisnici.size() != 1) {
            String errorMessage = "Ne postoji korisnik sa tim ID-ijem.";
            System.out.println(errorMessage);
            return new ErrorResponse(req, errorMessage);
        }

        // Create and persist the new Video
        Video newVideo = new Video();
        newVideo.setNaziv(video.getNaziv());
        newVideo.setTrajanje(video.getTrajanje());
        newVideo.setSifK(korisnici.get(0));
        newVideo.setDatumVreme(new Date());

        try {
            em.getTransaction().begin();
            em.persist(newVideo);
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
