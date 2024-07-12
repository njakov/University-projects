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
public class DohvatiOceneVideaRequest extends ServerRequest {

    private final int sifV;

    public int getSifV() {
        return sifV;
    }

    public DohvatiOceneVideaRequest(int sifV) {
        this.sifV = sifV;
    }

    @Override
    public Odrediste getOdrediste() {
        return ServerRequest.Odrediste.PODSISTEM3;
    }

    @Override
    public Request getRequest() {
        return ServerRequest.Request.DOHVATANJE_OCENA_ZA_VIDEO;
    }
}
