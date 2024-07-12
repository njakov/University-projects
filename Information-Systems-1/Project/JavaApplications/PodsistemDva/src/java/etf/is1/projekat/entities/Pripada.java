/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.is1.projekat.entities;

import java.io.Serializable;
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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ninaj
 */
@Entity
@Table(name = "pripada")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Pripada.findAll", query = "SELECT p FROM Pripada p"),
    @NamedQuery(name = "Pripada.findBySifP", query = "SELECT p FROM Pripada p WHERE p.sifP = :sifP")})
public class Pripada implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "SifP")
    private Integer sifP;
    @JoinColumn(name = "SifKat_id", referencedColumnName = "SifKat")
    @ManyToOne(optional = false)
    private Kategorija sifKatid;
    @JoinColumn(name = "SifV_id", referencedColumnName = "SifV")
    @ManyToOne(optional = false)
    private Video sifVid;

    public Pripada() {
    }

    public Pripada(Integer sifP) {
        this.sifP = sifP;
    }

    public Integer getSifP() {
        return sifP;
    }

    public void setSifP(Integer sifP) {
        this.sifP = sifP;
    }

    public Kategorija getSifKatid() {
        return sifKatid;
    }

    public void setSifKatid(Kategorija sifKatid) {
        this.sifKatid = sifKatid;
    }

    public Video getSifVid() {
        return sifVid;
    }

    public void setSifVid(Video sifVid) {
        this.sifVid = sifVid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (sifP != null ? sifP.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Pripada)) {
            return false;
        }
        Pripada other = (Pripada) object;
        if ((this.sifP == null && other.sifP != null) || (this.sifP != null && !this.sifP.equals(other.sifP))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "etf.is1.projekat.entities.Pripada[ sifP=" + sifP + " ]";
    }

}
