/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.is1.projekat.entities.subsystem3;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author ninaj
 */
@Embeddable
public class OcenaPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "SifK")
    private int sifK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "SifV")
    private int sifV;

    public OcenaPK() {
    }

    public OcenaPK(int sifK, int sifV) {
        this.sifK = sifK;
        this.sifV = sifV;
    }

    public int getSifK() {
        return sifK;
    }

    public void setSifK(int sifK) {
        this.sifK = sifK;
    }

    public int getSifV() {
        return sifV;
    }

    public void setSifV(int sifV) {
        this.sifV = sifV;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) sifK;
        hash += (int) sifV;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OcenaPK)) {
            return false;
        }
        OcenaPK other = (OcenaPK) object;
        if (this.sifK != other.sifK) {
            return false;
        }
        if (this.sifV != other.sifV) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "etf.is1.projekat.entities.OcenaPK[ sifK=" + sifK + ", sifV=" + sifV + " ]";
    }
    
}
