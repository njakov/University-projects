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
public class KreirajKorisnikaRequest extends ServerRequest {

    private final String ime;
    private final String email;
    private final int godiste;
    private final Character pol;
    private final String mesto;

    public KreirajKorisnikaRequest(String ime, String email, int godiste, Character pol, String mesto) {
        this.ime = ime;
        this.email = email;
        this.godiste = godiste;
        this.pol = pol;
        this.mesto = mesto;
    }

    public String getIme() {
        return ime;
    }

    public String getEmail() {
        return email;
    }

    public int getGodiste() {
        return godiste;
    }

    public Character getPol() {
        return pol;
    }

    public String getMesto() {
        return mesto;
    }

    @Override
    public ServerRequest.Odrediste getOdrediste() {
        return ServerRequest.Odrediste.PODSISTEM1;
    }

    @Override
    public Request getRequest() {
        return ServerRequest.Request.KREIRANJE_KORISNIKA;
    }

}
