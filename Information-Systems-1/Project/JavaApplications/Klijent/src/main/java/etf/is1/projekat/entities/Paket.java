/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.is1.projekat.entities;

import java.io.Serializable;
import java.util.List;
import javax.json.bind.annotation.JsonbTransient;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author ninaj
 */
public class Paket implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer sifPak;

    private String naziv;

    private int cena;

    private List<Pretplata> pretplataList;

    public Paket() {
    }

    public Paket(Integer sifPak) {
        this.sifPak = sifPak;
    }

    public Paket(Integer sifPak, int cena) {
        this.sifPak = sifPak;
        this.cena = cena;
    }

    public Integer getSifPak() {
        return sifPak;
    }

    public void setSifPak(Integer sifPak) {
        this.sifPak = sifPak;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public int getCena() {
        return cena;
    }

    public void setCena(int cena) {
        this.cena = cena;
    }

    @JsonbTransient
    @XmlTransient
    public List<Pretplata> getPretplataList() {
        return pretplataList;
    }

    public void setPretplataList(List<Pretplata> pretplataList) {
        this.pretplataList = pretplataList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (sifPak != null ? sifPak.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Paket)) {
            return false;
        }
        Paket other = (Paket) object;
        if ((this.sifPak == null && other.sifPak != null) || (this.sifPak != null && !this.sifPak.equals(other.sifPak))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "etf.is1.projekat.entities.Paket[ sifPak=" + sifPak + " ]";
    }

}
