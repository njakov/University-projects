/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.is1.projekat.server.requests;

/**
 *
 * @author ninaj
 */
public class DohvatiMestaRequest extends ServerRequest {

    @Override
    public Odrediste getOdrediste() {
        return ServerRequest.Odrediste.PODSISTEM1;
    }

    @Override
    public Request getRequest() {
        return ServerRequest.Request.DOHVATANJE_SVIH_MESTA;
    }

}
