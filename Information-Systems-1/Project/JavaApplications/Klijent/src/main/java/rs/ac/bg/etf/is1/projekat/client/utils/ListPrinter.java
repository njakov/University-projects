/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.ac.bg.etf.is1.projekat.client.utils;

/**
 *
 * @author ninaj
 */
import etf.is1.projekat.entities.Gledanje;
import etf.is1.projekat.entities.Kategorija;
import etf.is1.projekat.entities.Korisnik;
import etf.is1.projekat.entities.Mesto;
import etf.is1.projekat.entities.Ocena;
import etf.is1.projekat.entities.Paket;
import etf.is1.projekat.entities.Pretplata;
import etf.is1.projekat.entities.Video;
import java.util.List;

public class ListPrinter {

    // Method to format a list of Mesto objects
    public static String formatMestoList(List<Mesto> mesta) {
        if (mesta == null || mesta.isEmpty()) {
            return "Nema trazenih mesta.";
        }
        StringBuilder sb = new StringBuilder();
        mesta.forEach(mesto -> {
            sb.append("ID: ").append(mesto.getSifM())
                    .append(", Naziv: ").append(mesto.getNaziv())
                    .append("\n");
        });
        return sb.toString();
    }

    // Method to format a list of Korisnik objects
    public static String formatKorisnikList(List<Korisnik> korisnici) {
        if (korisnici == null || korisnici.isEmpty()) {
            return "Nema trazenih korisnika.";
        }
        StringBuilder sb = new StringBuilder();
        korisnici.forEach(korisnik -> {
            sb.append("ID: ").append(korisnik.getSifK())
                    .append(", Ime: ").append(korisnik.getIme())
                    .append(", Email: ").append(korisnik.getEmail())
                    .append(", Godiste: ").append(korisnik.getGodiste())
                    .append(", Pol: ").append(korisnik.getPol())
                    .append(", Mesto: ").append(korisnik.getSifM() != null ? korisnik.getSifM().getNaziv() : "N/A")
                    .append("\n");
        });
        return sb.toString();
    }

    // Method to format a list of Kategorija objects
    public static String formatKategorijaList(List<Kategorija> kategorije) {
        if (kategorije == null || kategorije.isEmpty()) {
            return "Nema trazenih kategorija.";
        }
        StringBuilder sb = new StringBuilder();
        kategorije.forEach(kategorija -> {
            sb.append("ID: ").append(kategorija.getSifKat())
                    .append(", Naziv: ").append(kategorija.getNaziv())
                    .append("\n");
        });
        return sb.toString();
    }

    // Method to format a list of Video objects
    public static String formatVideoList(List<Video> videi) {
        if (videi == null || videi.isEmpty()) {
            return "Nema trazenih videa.";
        }
        StringBuilder sb = new StringBuilder();
        videi.forEach(video -> {
            sb.append("ID: ").append(video.getSifV())
                    .append(", Naziv: ").append(video.getNaziv())
                    .append(", Trajanje: ").append(video.getTrajanje())
                    .append(", SifK: ").append(video.getSifK() != null ? video.getSifK().getSifK() : "N/A")
                    .append(", DatumVreme: ").append(video.getDatumVreme())
                    .append("\n");
        });
        return sb.toString();
    }

    // Method to format a list of Paket objects
    public static String formatPaketList(List<Paket> paketi) {
        if (paketi == null || paketi.isEmpty()) {
            return "Nema trazenih paketa.";
        }
        StringBuilder sb = new StringBuilder();
        paketi.forEach(paket -> {
            sb.append("ID: ").append(paket.getSifPak())
                    .append(", Naziv: ").append(paket.getNaziv())
                    .append(", Cena: ").append(paket.getCena())
                    .append("\n");
        });
        return sb.toString();
    }

    // Method to format a list of Pretplata objects
    public static String formatPretplataList(List<Pretplata> pretplate) {
        if (pretplate == null || pretplate.isEmpty()) {
            return "Nema trazenih pretplata.";
        }
        StringBuilder sb = new StringBuilder();
        pretplate.forEach(pretplata -> {
            sb.append("ID: ").append(pretplata.getSifPre())
                    .append(", Korisnik: ").append(pretplata.getSifK().getSifK())
                    .append(", Paket: ").append(pretplata.getSifPak().getNaziv())
                    .append(", DatumVreme: ").append(pretplata.getDatumVreme())
                    .append(", Cena: ").append(pretplata.getCena())
                    .append("\n");
        });
        return sb.toString();
    }

    // Method to format a list of Gledanje objects
    public static String formatGledanjeList(List<Gledanje> gledanja) {
        if (gledanja == null || gledanja.isEmpty()) {
            return "Nema trazenih gledanja.";
        }
        StringBuilder sb = new StringBuilder();
        gledanja.forEach(g -> {
            sb.append("ID: ").append(g.getSifG())
                    .append(", ID Korisnik: ").append(g.getSifK().getSifK())
                    .append(", Video: ").append(g.getSifV().getSifV())
                    .append(", DatumVreme: ").append(g.getDatumVreme())
                    .append(", Zapoceto: ").append(g.getZapoceto())
                    .append(", Odgledano: ").append(g.getOdgledano())
                    .append("\n");
        });
        return sb.toString();
    }

    // Method to format a list of Ocena objects
    public static String formatOcenaList(List<Ocena> ocene) {
        if (ocene == null || ocene.isEmpty()) {
            return "Nema trazenih ocena.";
        }
        StringBuilder sb = new StringBuilder();
        ocene.forEach(o -> {
            sb.append("ID: ").append(o.getOcenaPK())
                    .append(", Ocena: ").append(o.getOcena())
                    .append(", DatumVreme: ").append(o.getDatumVreme())
                    .append(", Korisnik: ").append(o.getKorisnik().getSifK())
                    .append(", Video: ").append(o.getVideo().getSifV())
                    .append("\n");
        });
        return sb.toString();
    }

}
