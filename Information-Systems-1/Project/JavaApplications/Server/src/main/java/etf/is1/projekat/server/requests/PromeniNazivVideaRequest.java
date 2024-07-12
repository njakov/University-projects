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
public class PromeniNazivVideaRequest extends ServerRequest {

    private final int sifV;
    private final String naziv;

    public int getSifV() {
        return sifV;
    }

    public String getNaziv() {
        return naziv;
    }

    public PromeniNazivVideaRequest(int sifV, String naziv) {
        this.sifV = sifV;
        this.naziv = naziv;
    }

    @Override
    public Odrediste getOdrediste() {
        return ServerRequest.Odrediste.PODSISTEM2;
    }

    @Override
    public Request getRequest() {
        return ServerRequest.Request.PROMENA_NAZIVA_VIDEA;
    }

}
