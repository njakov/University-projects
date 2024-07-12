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
@Table(name = "gledanje")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Gledanje.findAll", query = "SELECT g FROM Gledanje g"),
    @NamedQuery(name = "Gledanje.findBySifG", query = "SELECT g FROM Gledanje g WHERE g.sifG = :sifG"),
    @NamedQuery(name = "Gledanje.findByDatumVreme", query = "SELECT g FROM Gledanje g WHERE g.datumVreme = :datumVreme"),
    @NamedQuery(name = "Gledanje.findByZapoceto", query = "SELECT g FROM Gledanje g WHERE g.zapoceto = :zapoceto"),
    @NamedQuery(name = "Gledanje.findByOdgledano", query = "SELECT g FROM Gledanje g WHERE g.odgledano = :odgledano")})
public class Gledanje implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "SifG")
    private Integer sifG;
    @Basic(optional = false)
    @NotNull
    @Column(name = "DatumVreme")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datumVreme;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Zapoceto")
    private int zapoceto;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Odgledano")
    private int odgledano;
    @JoinColumn(name = "SifK", referencedColumnName = "SifK")
    @ManyToOne(optional = false)
    private Korisnik sifK;
    @JoinColumn(name = "SifV", referencedColumnName = "SifV")
    @ManyToOne(optional = false)
    private Video sifV;

    public Gledanje() {
    }

    public Gledanje(Integer sifG) {
        this.sifG = sifG;
    }

    public Gledanje(Integer sifG, Date datumVreme, int zapoceto, int odgledano) {
        this.sifG = sifG;
        this.datumVreme = datumVreme;
        this.zapoceto = zapoceto;
        this.odgledano = odgledano;
    }

    public Integer getSifG() {
        return sifG;
    }

    public void setSifG(Integer sifG) {
        this.sifG = sifG;
    }

    public Date getDatumVreme() {
        return datumVreme;
    }

    public void setDatumVreme(Date datumVreme) {
        this.datumVreme = datumVreme;
    }

    public int getZapoceto() {
        return zapoceto;
    }

    public void setZapoceto(int zapoceto) {
        this.zapoceto = zapoceto;
    }

    public int getOdgledano() {
        return odgledano;
    }

    public void setOdgledano(int odgledano) {
        this.odgledano = odgledano;
    }

    public Korisnik getSifK() {
        return sifK;
    }

    public void setSifK(Korisnik sifK) {
        this.sifK = sifK;
    }

    public Video getSifV() {
        return sifV;
    }

    public void setSifV(Video sifV) {
        this.sifV = sifV;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (sifG != null ? sifG.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Gledanje)) {
            return false;
        }
        Gledanje other = (Gledanje) object;
        if ((this.sifG == null && other.sifG != null) || (this.sifG != null && !this.sifG.equals(other.sifG))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "etf.is1.projekat.entities.Gledanje[ sifG=" + sifG + " ]";
    }

}
