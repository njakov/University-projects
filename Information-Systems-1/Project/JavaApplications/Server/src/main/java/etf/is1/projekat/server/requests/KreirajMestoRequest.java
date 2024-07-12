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
public class KreirajMestoRequest extends ServerRequest {

    private final String naziv;

    public KreirajMestoRequest(String naziv) {
        this.naziv = naziv;
    }

    public String getNaziv() {
        return naziv;
    }

    @Override
    public ServerRequest.Odrediste getOdrediste() {
        return ServerRequest.Odrediste.PODSISTEM1;
    }

    @Override
    public ServerRequest.Request getRequest() {
        return ServerRequest.Request.KREIRANJE_MESTA;
    }
}
