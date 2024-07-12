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
public class PromeniOcenuSnimkaRequest extends ServerRequest {

    private final int sifK;
    private final int sifV;
    private final int ocena;

    public PromeniOcenuSnimkaRequest(int sifK, int sifV, int ocena) {
        this.sifK = sifK;
        this.sifV = sifV;
        this.ocena = ocena;
    }

    public int getSifK() {
        return sifK;
    }

    public int getSifV() {
        return sifV;
    }

    public int getOcena() {
        return ocena;
    }

    @Override
    public Odrediste getOdrediste() {
        return ServerRequest.Odrediste.PODSISTEM3;
    }

    @Override
    public Request getRequest() {
        return ServerRequest.Request.PROMENA_OCENE;
    }
}
