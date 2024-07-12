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
public class KreirajPretplatuRequest extends ServerRequest {

    private static final long serialVersionUID = 1L;  // Or any unique long value

    private final int sifK;
    private final int sifPak;

    public KreirajPretplatuRequest(int sifK, int sifPak) {
        this.sifK = sifK;
        this.sifPak = sifPak;
    }

    public int getSifK() {
        return sifK;
    }

    public int getSifPak() {
        return sifPak;
    }

    @Override
    public Odrediste getOdrediste() {
        return ServerRequest.Odrediste.PODSISTEM3;
    }

    @Override
    public Request getRequest() {
        return ServerRequest.Request.KREIRANJE_PRETPLATE;
    }

}
