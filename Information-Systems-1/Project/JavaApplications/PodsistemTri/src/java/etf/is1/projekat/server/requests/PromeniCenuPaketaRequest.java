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
public class PromeniCenuPaketaRequest extends ServerRequest{

    
    private final int sifPak;
    private final int cena;

    public PromeniCenuPaketaRequest(int sifPak, int cena) {
        this.sifPak = sifPak;
        this.cena = cena;
    }

    public int getSifPak() {
        return sifPak;
    }

    public int getCena() {
        return cena;
    }

   

    @Override
    public Odrediste getOdrediste() {
        return ServerRequest.Odrediste.PODSISTEM3; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Request getRequest() {
        return ServerRequest.Request.PROMENA_CENE_PAKETA;
    }
    
}
