/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.is1.projekat.entities.subsystem3;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ninaj
 */
@Entity
@Table(name = "pretplata")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Pretplata.findAll", query = "SELECT p FROM Pretplata p"),
    @NamedQuery(name = "Pretplata.findBySifPre", query = "SELECT p FROM Pretplata p WHERE p.sifPre = :sifPre"),
    @NamedQuery(name = "Pretplata.findByDatumVreme", query = "SELECT p FROM Pretplata p WHERE p.datumVreme = :datumVreme"),
    @NamedQuery(name = "Pretplata.findByCena", query = "SELECT p FROM Pretplata p WHERE p.cena = :cena")})
public class Pretplata implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "SifPre")
    private Integer sifPre;
    @Basic(optional = false)
    @NotNull
    @Column(name = "DatumVreme")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datumVreme;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Cena")
    private int cena;
    @JoinColumn(name = "SifK", referencedColumnName = "SifK")
    @ManyToOne(optional = false)
    private Korisnik sifK;
    @JoinColumn(name = "SifPak", referencedColumnName = "SifPak")
    @ManyToOne(optional = false)
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
