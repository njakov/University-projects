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
public class PromeniEmailRequest extends ServerRequest {

    private final int sifK;
    private final String email;

    public PromeniEmailRequest(int sifK, String email) {
        this.sifK = sifK;
        this.email = email;
    }

    public int getSifK() {
        return sifK;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public Odrediste getOdrediste() {
        return ServerRequest.Odrediste.PODSISTEM1;
    }

    @Override
    public Request getRequest() {
        return ServerRequest.Request.PROMENA_EMAIL;
    }

}
