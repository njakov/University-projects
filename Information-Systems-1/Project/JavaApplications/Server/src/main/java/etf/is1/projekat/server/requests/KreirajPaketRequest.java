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
public class KreirajPaketRequest extends ServerRequest {

    public KreirajPaketRequest(String naziv, int cena) {
        this.naziv = naziv;
        this.cena = cena;
    }

    public String getNaziv() {
        return naziv;
    }

    public int getCena() {
        return cena;
    }

    private final String naziv;

    private final int cena;

    @Override
    public Odrediste getOdrediste() {
        return ServerRequest.Odrediste.PODSISTEM3;
    }

    @Override
    public Request getRequest() {
        return ServerRequest.Request.KREIRANJE_PAKETA;
    }

}
