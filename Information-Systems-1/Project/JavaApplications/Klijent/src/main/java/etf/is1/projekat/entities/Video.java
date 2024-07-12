/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.is1.projekat.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.json.bind.annotation.JsonbTransient;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author ninaj
 */
public class Video implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer sifV;

    private String naziv;

    private int trajanje;

    private Date datumVreme;

    private List<Kategorija> kategorijaList;

    private Korisnik sifK;

    public Video() {
    }

    @JsonbTransient
    @XmlTransient
    public List<Kategorija> getKategorijaList() {
        return kategorijaList;
    }

    public Video(Integer sifV) {
        this.sifV = sifV;
    }

    public Video(Integer sifV, String naziv, int trajanje, Date datumVreme) {
        this.sifV = sifV;
        this.naziv = naziv;
        this.trajanje = trajanje;
        this.datumVreme = datumVreme;
    }

    public Integer getSifV() {
        return sifV;
    }

    public void setSifV(Integer sifV) {
        this.sifV = sifV;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public int getTrajanje() {
        return trajanje;
    }

    public void setTrajanje(int trajanje) {
        this.trajanje = trajanje;
    }

    public Date getDatumVreme() {
        return datumVreme;
    }

    public void setDatumVreme(Date datumVreme) {
        this.datumVreme = datumVreme;
    }

    public void setKategorijaList(List<Kategorija> kategorijaList) {
        this.kategorijaList = kategorijaList;
    }

    public Korisnik getSifK() {
        return sifK;
    }

    public void setSifK(Korisnik sifK) {
        this.sifK = sifK;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (sifV != null ? sifV.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Video)) {
            return false;
        }
        Video other = (Video) object;
        if ((this.sifV == null && other.sifV != null) || (this.sifV != null && !this.sifV.equals(other.sifV))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "etf.is1.projekat.entities.Video[ sifV=" + sifV + " ]";
    }

}
