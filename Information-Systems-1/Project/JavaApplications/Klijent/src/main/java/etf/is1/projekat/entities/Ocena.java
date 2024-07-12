/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.is1.projekat.entities;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author ninaj
 */
public class Ocena implements Serializable {

    private static final long serialVersionUID = 1L;

    protected OcenaPK ocenaPK;

    private int ocena;

    private Date datumVreme;

    private Korisnik korisnik;

    private Video video;

    public Ocena() {
    }

    public Ocena(OcenaPK ocenaPK) {
        this.ocenaPK = ocenaPK;
    }

    public Ocena(OcenaPK ocenaPK, int ocena, Date datumVreme) {
        this.ocenaPK = ocenaPK;
        this.ocena = ocena;
        this.datumVreme = datumVreme;
    }

    public Ocena(int sifK, int sifV) {
        this.ocenaPK = new OcenaPK(sifK, sifV);
    }

    public OcenaPK getOcenaPK() {
        return ocenaPK;
    }

    public void setOcenaPK(OcenaPK ocenaPK) {
        this.ocenaPK = ocenaPK;
    }

    public int getOcena() {
        return ocena;
    }

    public void setOcena(int ocena) {
        this.ocena = ocena;
    }

    public Date getDatumVreme() {
        return datumVreme;
    }

    public void setDatumVreme(Date datumVreme) {
        this.datumVreme = datumVreme;
    }

    public Korisnik getKorisnik() {
        return korisnik;
    }

    public void setKorisnik(Korisnik korisnik) {
        this.korisnik = korisnik;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (ocenaPK != null ? ocenaPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Ocena)) {
            return false;
        }
        Ocena other = (Ocena) object;
        if ((this.ocenaPK == null && other.ocenaPK != null) || (this.ocenaPK != null && !this.ocenaPK.equals(other.ocenaPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "etf.is1.projekat.entities.Ocena[ ocenaPK=" + ocenaPK + " ]";
    }

}
