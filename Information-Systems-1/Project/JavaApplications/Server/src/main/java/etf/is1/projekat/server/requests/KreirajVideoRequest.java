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
public class KreirajVideoRequest extends ServerRequest {

    private final String naziv;

    private final int trajanje;

    private final int sifK;

    public KreirajVideoRequest(String naziv, int trajanje, int sifK) {
        this.naziv = naziv;
        this.trajanje = trajanje;
        this.sifK = sifK;
    }

    public String getNaziv() {
        return naziv;
    }

    public int getTrajanje() {
        return trajanje;
    }

    public int getSifK() {
        return sifK;
    }

    @Override
    public Odrediste getOdrediste() {
        return ServerRequest.Odrediste.PODSISTEM2;
    }

    @Override
    public Request getRequest() {
        return ServerRequest.Request.KREIRANJE_VIDEA;
    }

}
