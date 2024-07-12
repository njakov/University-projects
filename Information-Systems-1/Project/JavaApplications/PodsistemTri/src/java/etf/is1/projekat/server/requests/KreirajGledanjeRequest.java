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
public class KreirajGledanjeRequest extends ServerRequest {

    private final int sifK;
    private final int sifV;

    public KreirajGledanjeRequest(int sifK, int sifV, int zapoceto, int odgledano) {
        this.sifK = sifK;
        this.sifV = sifV;
        this.zapoceto = zapoceto;
        this.odgledano = odgledano;
    }

    public int getSifK() {
        return sifK;
    }

    public int getSifV() {
        return sifV;
    }

    public int getZapoceto() {
        return zapoceto;
    }

    public int getOdgledano() {
        return odgledano;
    }

    private final int zapoceto;
    private final int odgledano;

    @Override
    public Odrediste getOdrediste() {
        return ServerRequest.Odrediste.PODSISTEM3;
    }

    @Override
    public Request getRequest() {
        return ServerRequest.Request.KREIRANJE_GLEDANJA;
    }
}
