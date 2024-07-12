package rs.ac.bg.etf.is1.projekat.client.utils;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ninaj
 */
public enum MenuItem {
    IZLAZ(0),
    KREIRANJE_MESTA(1),
    KREIRANJE_KORISNIKA(2),
    PROMENA_EMAIL(3),
    PROMENA_MESTA(4),
    KREIRANJE_KATEGORIJE(5),
    KREIRANJE_VIDEA(6),
    PROMENA_NAZIVA_VIDEA(7),
    DODAVANJE_KATEGORIJE(8),
    KREIRANJE_PAKETA(9),
    PROMENA_CENE_PAKETA(10),
    KREIRANJE_PRETPLATE(11),
    KREIRANJE_GLEDANJA(12),
    KREIRANJE_OCENE(13),
    PROMENA_OCENE(14),
    BRISANJE_OCENE(15),
    BRISANJE_VIDEA(16),
    DOHVATANJE_SVIH_MESTA(17),
    DOHVATANJE_SVIH_KORISNIKA(18),
    DOHVATANJE_SVIH_KATEGORIJA(19),
    DOHVATANJE_SVIH_VIDEO_SNIMAKA(20),
    DOHVATANJE_KATEGORIJA_ZA_VIDEO(21),
    DOHVATANJE_SVIH_PAKETA(22),
    DOHVATANJE_PRETPLATA_ZA_KORISNIKA(23),
    DOHVATANJE_GLEDANJA_ZA_VIDEO(24),
    DOHVATANJE_OCENA_ZA_VIDEO(25);

    private final int value;

    MenuItem(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static MenuItem fromValue(int value) {
        for (MenuItem item : MenuItem.values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return null; // Handle unknown values gracefully if needed
    }
}
