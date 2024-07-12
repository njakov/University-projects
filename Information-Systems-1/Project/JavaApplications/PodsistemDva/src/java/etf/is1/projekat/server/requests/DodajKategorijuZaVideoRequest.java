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
public class DodajKategorijuZaVideoRequest extends ServerRequest {

    private final int sifV;
    private final int sifKat;

    public int getSifV() {
        return sifV;
    }

    public int getSifKat() {
        return sifKat;
    }

    public DodajKategorijuZaVideoRequest(int sifV, int sifKat) {
        this.sifV = sifV;
        this.sifKat = sifKat;
    }

    @Override
    public Odrediste getOdrediste() {
        return ServerRequest.Odrediste.PODSISTEM2;
    }

    @Override
    public Request getRequest() {
        return ServerRequest.Request.DODAVANJE_KATEGORIJE;
    }

}
