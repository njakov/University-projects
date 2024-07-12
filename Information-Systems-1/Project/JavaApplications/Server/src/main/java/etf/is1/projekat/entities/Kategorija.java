/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.is1.projekat.entities;

import java.io.Serializable;
import java.util.List;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author ninaj
 */
@Entity
@Table(name = "kategorija")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Kategorija.findAll", query = "SELECT k FROM Kategorija k"),
    @NamedQuery(name = "Kategorija.findBySifKat", query = "SELECT k FROM Kategorija k WHERE k.sifKat = :sifKat"),
    @NamedQuery(name = "Kategorija.findByNaziv", query = "SELECT k FROM Kategorija k WHERE k.naziv = :naziv")})
public class Kategorija implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "SifKat")
    private Integer sifKat;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "Naziv")
    private String naziv;
    @JoinTable(name = "pripada", joinColumns = {
        @JoinColumn(name = "SifKat", referencedColumnName = "SifKat")}, inverseJoinColumns = {
        @JoinColumn(name = "SifV", referencedColumnName = "SifV")})
    @ManyToMany
    private List<Video> videoList;

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

    @JsonbTransient
    @XmlTransient
    public List<Video> getVideoList() {
        return videoList;
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
