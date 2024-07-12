/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.is1.projekat.server.requests;

import java.io.Serializable;
import java.util.UUID;

/**
 *
 * @author ninaj
 */
public abstract class ServerRequest implements Serializable {

    public enum Request {
        KREIRANJE_MESTA,
        KREIRANJE_KORISNIKA,
        PROMENA_EMAIL,
        PROMENA_MESTA,
        KREIRANJE_KATEGORIJE,
        KREIRANJE_VIDEA,
        PROMENA_NAZIVA_VIDEA,
        DODAVANJE_KATEGORIJE,
        KREIRANJE_PAKETA,
        PROMENA_CENE_PAKETA,
        KREIRANJE_PRETPLATE,
        KREIRANJE_GLEDANJA,
        KREIRANJE_OCENE,
        PROMENA_OCENE,
        BRISANJE_OCENE,
        BRISANJE_VIDEA,
        DOHVATANJE_SVIH_MESTA,
        DOHVATANJE_SVIH_KORISNIKA,
        DOHVATANJE_SVIH_KATEGORIJA,
        DOHVATANJE_SVIH_VIDEO_SNIMAKA,
        DOHVATANJE_KATEGORIJA_ZA_VIDEO,
        DOHVATANJE_SVIH_PAKETA,
        DOHVATANJE_PRETPLATA_ZA_KORISNIKA,
        DOHVATANJE_GLEDANJA_ZA_VIDEO,
        DOHVATANJE_OCENA_ZA_VIDEO;
    };

    public enum Odrediste {
        PODSISTEM1,
        PODSISTEM2,
        PODSISTEM3
    };

    protected final UUID uuid;

    public ServerRequest() {
        this.uuid = UUID.randomUUID();
    }

    public String getId() {
        return uuid.toString();
    }

    abstract public Odrediste getOdrediste();

    abstract public Request getRequest();

    @Override
    public String toString() {
        return "ServerRequest{"
                + "name=" + getRequest()
                + ", id=" + uuid
                + ", destination=" + getOdrediste()
                + '}';
    }

}
