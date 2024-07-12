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
public class PromeniMestoRequest extends ServerRequest {

    private final int sifK;
    private final String naziv;

    public PromeniMestoRequest(int sifK, String naziv) {
        this.sifK = sifK;
        this.naziv = naziv;
    }

    public int getSifK() {
        return sifK;
    }

    public String getNaziv() {
        return naziv;
    }

    @Override
    public Odrediste getOdrediste() {
        return ServerRequest.Odrediste.PODSISTEM1;
    }

    @Override
    public Request getRequest() {
        return ServerRequest.Request.PROMENA_MESTA;
    }
}
