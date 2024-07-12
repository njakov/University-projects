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
public class Pretplata implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer sifPre;

    private Date datumVreme;

    private int cena;

    private Korisnik sifK;

    private Paket sifPak;

    public Pretplata() {
    }

    public Pretplata(Integer sifPre) {
        this.sifPre = sifPre;
    }

    public Pretplata(Integer sifPre, Date datumVreme, int cena) {
        this.sifPre = sifPre;
        this.datumVreme = datumVreme;
        this.cena = cena;
    }

    public Integer getSifPre() {
        return sifPre;
    }

    public void setSifPre(Integer sifPre) {
        this.sifPre = sifPre;
    }

    public Date getDatumVreme() {
        return datumVreme;
    }

    public void setDatumVreme(Date datumVreme) {
        this.datumVreme = datumVreme;
    }

    public int getCena() {
        return cena;
    }

    public void setCena(int cena) {
        this.cena = cena;
    }

    public Korisnik getSifK() {
        return sifK;
    }

    public void setSifK(Korisnik sifK) {
        this.sifK = sifK;
    }

    public Paket getSifPak() {
        return sifPak;
    }

    public void setSifPak(Paket sifPak) {
        this.sifPak = sifPak;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (sifPre != null ? sifPre.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Pretplata)) {
            return false;
        }
        Pretplata other = (Pretplata) object;
        if ((this.sifPre == null && other.sifPre != null) || (this.sifPre != null && !this.sifPre.equals(other.sifPre))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "etf.is1.projekat.entities.Pretplata[ sifPre=" + sifPre + " ]";
    }

}
