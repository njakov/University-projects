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
public class ObrisiVideoRequest extends ServerRequest {

    private final int sifV;
    private final int sifK;

    public int getSifV() {
        return sifV;
    }

    public int getSifK() {
        return sifK;
    }

    public ObrisiVideoRequest(int sifV, int sifK) {
        this.sifV = sifV;
        this.sifK = sifK;
    }

    @Override
    public Odrediste getOdrediste() {
        return ServerRequest.Odrediste.PODSISTEM2;
    }

    @Override
    public Request getRequest() {
        return ServerRequest.Request.BRISANJE_VIDEA;
    }

}
