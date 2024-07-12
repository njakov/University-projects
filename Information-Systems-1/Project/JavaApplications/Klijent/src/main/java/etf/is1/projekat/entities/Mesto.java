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
public class Mesto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer sifM;

    private String naziv;

    private List<Korisnik> korisnikList;

    public Mesto() {
    }

    public Mesto(Integer sifM) {
        this.sifM = sifM;
    }

    public Mesto(Integer sifM, String naziv) {
        this.sifM = sifM;
        this.naziv = naziv;
    }

    public Integer getSifM() {
        return sifM;
    }

    public void setSifM(Integer sifM) {
        this.sifM = sifM;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    @JsonbTransient
    @XmlTransient
    public List<Korisnik> getKorisnikList() {
        return korisnikList;
    }

    public void setKorisnikList(List<Korisnik> korisnikList) {
        this.korisnikList = korisnikList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (sifM != null ? sifM.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Mesto)) {
            return false;
        }
        Mesto other = (Mesto) object;
        if ((this.sifM == null && other.sifM != null) || (this.sifM != null && !this.sifM.equals(other.sifM))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Mesto{"
                + "ID=" + sifM
                + ", Naziv='" + naziv + '}';
    }

}
