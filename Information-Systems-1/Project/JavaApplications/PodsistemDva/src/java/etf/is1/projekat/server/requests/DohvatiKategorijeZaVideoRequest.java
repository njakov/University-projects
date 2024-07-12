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
public class DohvatiKategorijeZaVideoRequest extends ServerRequest {

    private final int sifV;

    public DohvatiKategorijeZaVideoRequest(int sifV) {
        this.sifV = sifV;
    }

    public int getSifV() {
        return sifV;
    }

    @Override
    public Odrediste getOdrediste() {
        return ServerRequest.Odrediste.PODSISTEM2;
    }

    @Override
    public Request getRequest() {
        return ServerRequest.Request.DOHVATANJE_KATEGORIJA_ZA_VIDEO;
    }

}
