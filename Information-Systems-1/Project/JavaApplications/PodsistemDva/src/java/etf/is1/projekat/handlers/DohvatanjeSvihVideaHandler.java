/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.is1.projekat.handlers;

import etf.is1.projekat.entities.Video;
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
public class DohvatanjeSvihVideaHandler extends RequestHandler {

    public DohvatanjeSvihVideaHandler(EntityManager em) {
        super(em);
    }

    @Override
    public JMSResponse handle(ServerRequest req) {
        System.out.println("Handling request: " + req.getRequest());

        // Fetch all videos
        List<Video> snimci;
        try {
            snimci = em.createNamedQuery("Video.findAll", Video.class).getResultList();
            System.out.println("Svi video snimci: " + snimci);
        } catch (Exception e) {
            String errorMessage = "Failed to fetch videos: " + e.getMessage();
            System.out.println(errorMessage);
            return new ErrorResponse(req, errorMessage);
        }

        // Create and return response
        return new DataResponse<>(req, snimci);
    }

}
