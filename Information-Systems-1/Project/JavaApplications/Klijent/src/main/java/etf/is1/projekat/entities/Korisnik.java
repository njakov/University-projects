/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.is1.projekat.entities;

import java.io.Serializable;

/**
 *
 * @author ninaj
 */
public class Korisnik implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer sifK;

    private String ime;

    private String email;

    private int godiste;

    private Character pol;

    private Mesto sifM;

    public Korisnik() {
    }

    public Korisnik(Integer sifK) {
        this.sifK = sifK;
    }

    public Korisnik(Integer sifK, String ime, String email, int godiste, Character pol) {
        this.sifK = sifK;
        this.ime = ime;
        this.email = email;
        this.godiste = godiste;
        this.pol = pol;
    }

    public Integer getSifK() {
        return sifK;
    }

    public void setSifK(Integer sifK) {
        this.sifK = sifK;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getGodiste() {
        return godiste;
    }

    public void setGodiste(int godiste) {
        this.godiste = godiste;
    }

    public Character getPol() {
        return pol;
    }

    public void setPol(Character pol) {
        this.pol = pol;
    }

    public Mesto getSifM() {
        return sifM;
    }

    public void setSifM(Mesto sifM) {
        this.sifM = sifM;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (sifK != null ? sifK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Korisnik)) {
            return false;
        }
        Korisnik other = (Korisnik) object;
        if ((this.sifK == null && other.sifK != null) || (this.sifK != null && !this.sifK.equals(other.sifK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String mestoNaziv = (sifM != null) ? sifM.getNaziv() : "N/A";
        return "Korisnik{"
                + "ID=" + sifK
                + ", Ime='" + ime + '\''
                + ", Email='" + email + '\''
                + ", Godiste=" + godiste
                + ", Pol=" + pol
                + ", Mesto=" + mestoNaziv
                + '}';
    }

}
