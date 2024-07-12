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
public class Kategorija implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer sifKat;

    private String naziv;

    private List<Video> videoList;

    @JsonbTransient
    @XmlTransient
    public List<Video> getVideoList() {
        return videoList;
    }

    public Kategorija() {
    }

    public Kategorija(Integer sifKat) {
        this.sifKat = sifKat;
    }

    public Kategorija(Integer sifKat, String naziv) {
        this.sifKat = sifKat;
        this.naziv = naziv;
    }

    public Integer getSifKat() {
        return sifKat;
    }

    public void setSifKat(Integer sifKat) {
        this.sifKat = sifKat;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public void setVideoList(List<Video> videoList) {
        this.videoList = videoList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (sifKat != null ? sifKat.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Kategorija)) {
            return false;
        }
        Kategorija other = (Kategorija) object;
        if ((this.sifKat == null && other.sifKat != null) || (this.sifKat != null && !this.sifKat.equals(other.sifKat))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "etf.is1.projekat.entities.Kategorija[ sifKat=" + sifKat + " ]";
    }

}
