/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.ac.bg.etf.is1.projekat.client;

/**
 *
 * @author ninaj
 */
import etf.is1.projekat.client.menu.KreirajKategoriju;
import etf.is1.projekat.client.menu.PromeniMesto;
import etf.is1.projekat.client.menu.PromeniEmail;
import etf.is1.projekat.client.menu.DohvatanjeSvihMesta;
import etf.is1.projekat.client.menu.KreirajKorisnika;
import etf.is1.projekat.client.menu.KreirajMesto;
import etf.is1.projekat.client.menu.DohvatanjeSvihKorisnika;
import etf.is1.projekat.client.menu.BrisanjeOceneZaVideo;
import etf.is1.projekat.client.menu.BrisanjeVideoSnimka;
import etf.is1.projekat.client.menu.DodavanjeKategorije;
import etf.is1.projekat.client.menu.DohvatanjeGledanjaZaVideo;
import etf.is1.projekat.client.menu.DohvatanjeKategorijaZaVideo;
import etf.is1.projekat.client.menu.DohvatanjeOcenaZaVideo;
import etf.is1.projekat.client.menu.DohvatanjePretplataZaKorisnika;
import etf.is1.projekat.client.menu.DohvatanjeSvihKategorija;
import etf.is1.projekat.client.menu.DohvatanjeSvihPaketa;
import etf.is1.projekat.client.menu.DohvatanjeSvihVidea;
import etf.is1.projekat.client.menu.KreirajGledanje;
import etf.is1.projekat.client.menu.KreirajOcenuZaVideo;
import etf.is1.projekat.client.menu.KreirajPaket;
import etf.is1.projekat.client.menu.KreirajPretplatu;
import etf.is1.projekat.client.menu.KreirajVideo;
import etf.is1.projekat.client.menu.PromenaCene;
import etf.is1.projekat.client.menu.PromeniNazivVidea;
import etf.is1.projekat.client.menu.PromeniOcenuZaVideo;
import etf.is1.projekat.entities.Gledanje;
import etf.is1.projekat.entities.Kategorija;
import etf.is1.projekat.entities.Korisnik;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import okhttp3.OkHttpClient;

import retrofit2.Response;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import etf.is1.projekat.entities.Mesto;
import etf.is1.projekat.entities.Ocena;
import etf.is1.projekat.entities.Paket;
import etf.is1.projekat.entities.Pretplata;
import etf.is1.projekat.entities.Video;
import rs.ac.bg.etf.is1.projekat.client.utils.*;
import rs.ac.bg.etf.is1.projekat.client.services.ApiService;

public class Client {

    private static Retrofit retrofit;
    private static ApiService service;

    public static void main(String[] args) throws IOException {
        // Initialize Retrofit
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:8080/Server/resources/podsistem1/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        service = retrofit.create(ApiService.class);
        Scanner scanner = new Scanner(System.in);
        int choice;

        while (true) {
            displayMenu();
            choice = Integer.parseInt(scanner.nextLine());
            MenuItem selectedItem = MenuItem.fromValue(choice);

            switch (selectedItem) {
                case KREIRANJE_MESTA:
                    try {
                    Response<String> response = new KreirajMesto()
                            .execute(scanner, service)
                            .execute();
                    // Check the response status code
                    ResponseHandler.handleResponse(response);
                } catch (IOException e) {
                    // Handle network or other I/O errors
                    System.out.println("Network error: " + e.getMessage());
                }
                break;

                case KREIRANJE_KORISNIKA:
                    try {
                    Response<String> response = new KreirajKorisnika()
                            .execute(scanner, service)
                            .execute();
                    // Check the response status code
                    ResponseHandler.handleResponse(response);
                } catch (IOException e) {
                    // Handle network or other I/O errors
                    System.out.println("Network error: " + e.getMessage());
                }
                break;

                case PROMENA_EMAIL:
                    try {
                    Response<String> response = new PromeniEmail()
                            .execute(scanner, service)
                            .execute();

                    // Check the response status code
                    ResponseHandler.handleResponse(response);
                } catch (IOException e) {
                    // Handle network or other I/O errors
                    System.out.println("Network error: " + e.getMessage());
                }
                break;

                case PROMENA_MESTA:
                    try {
                    Response<String> response = new PromeniMesto()
                            .execute(scanner, service)
                            .execute();

                    // Check the response status code
                    ResponseHandler.handleResponse(response);
                } catch (IOException e) {
                    // Handle network or other I/O errors
                    System.out.println("Network error: " + e.getMessage());
                }
                break;

                case KREIRANJE_KATEGORIJE:
                    try {
                    Response<String> response = new KreirajKategoriju()
                            .execute(scanner, service)
                            .execute();
                    // Check the response status code
                    ResponseHandler.handleResponse(response);
                } catch (IOException e) {
                    // Handle network or other I/O errors
                    System.out.println("Network error: " + e.getMessage());
                }
                break;

                case KREIRANJE_VIDEA:
                    try {
                    Response<String> response = new KreirajVideo()
                            .execute(scanner, service)
                            .execute();

                    ResponseHandler.handleResponse(response);
                } catch (IOException e) {
                    // Handle network or other I/O errors
                    System.out.println("Network error: " + e.getMessage());
                }
                break;

                case PROMENA_NAZIVA_VIDEA:
                    try {
                    Response<String> response = new PromeniNazivVidea()
                            .execute(scanner, service)
                            .execute();

                    // Check the response status code
                    ResponseHandler.handleResponse(response);
                } catch (IOException e) {
                    // Handle network or other I/O errors
                    System.out.println("Network error: " + e.getMessage());
                }
                break;

                case DODAVANJE_KATEGORIJE:
                    try {
                    Response<String> response = new DodavanjeKategorije()
                            .execute(scanner, service)
                            .execute();

                    // Check the response status code
                    ResponseHandler.handleResponse(response);
                } catch (IOException e) {
                    // Handle network or other I/O errors
                    System.out.println("Network error: " + e.getMessage());
                }
                break;

                case KREIRANJE_PAKETA:
                    try {
                    Response<String> response = new KreirajPaket()
                            .execute(scanner, service)
                            .execute();

                    // Check the response status code
                    ResponseHandler.handleResponse(response);
                } catch (IOException e) {
                    // Handle network or other I/O errors
                    System.out.println("Network error: " + e.getMessage());
                }
                break;

                case PROMENA_CENE_PAKETA:
                    try {
                    Response<String> response = new PromenaCene()
                            .execute(scanner, service)
                            .execute();

                    // Check the response status code
                    ResponseHandler.handleResponse(response);
                } catch (IOException e) {
                    // Handle network or other I/O errors
                    System.out.println("Network error: " + e.getMessage());
                }
                break;

                case KREIRANJE_PRETPLATE:
                    try {
                    Response<String> response = new KreirajPretplatu()
                            .execute(scanner, service)
                            .execute();

                    // Check the response status code
                    ResponseHandler.handleResponse(response);
                } catch (IOException e) {
                    // Handle network or other I/O errors
                    System.out.println("Network error: " + e.getMessage());
                }
                break;

                case KREIRANJE_GLEDANJA:
                    try {
                    Response<String> response = new KreirajGledanje()
                            .execute(scanner, service)
                            .execute();

                    // Check the response status code
                    ResponseHandler.handleResponse(response);
                } catch (IOException e) {
                    // Handle network or other I/O errors
                    System.out.println("Network error: " + e.getMessage());
                }
                break;

                case KREIRANJE_OCENE:
                    try {
                    Response<String> response = new KreirajOcenuZaVideo()
                            .execute(scanner, service)
                            .execute();

                    // Check the response status code
                    ResponseHandler.handleResponse(response);
                } catch (IOException e) {
                    // Handle network or other I/O errors
                    System.out.println("Network error: " + e.getMessage());
                }
                break;

                case PROMENA_OCENE:
                    try {
                    Response<String> response = new PromeniOcenuZaVideo()
                            .execute(scanner, service)
                            .execute();

                    // Check the response status code
                    ResponseHandler.handleResponse(response);
                } catch (IOException e) {
                    // Handle network or other I/O errors
                    System.out.println("Network error: " + e.getMessage());
                }
                break;

                case BRISANJE_OCENE:
                    try {
                    Response<String> response = new BrisanjeOceneZaVideo()
                            .execute(scanner, service)
                            .execute();

                    // Check the response status code
                    ResponseHandler.handleResponse(response);
                } catch (IOException e) {
                    // Handle network or other I/O errors
                    System.out.println("Network error: " + e.getMessage());
                }
                break;

                case BRISANJE_VIDEA:
                    try {
                    Response<String> response = new BrisanjeVideoSnimka()
                            .execute(scanner, service)
                            .execute();

                    // Check the response status code
                    ResponseHandler.handleResponse(response);
                } catch (IOException e) {
                    // Handle network or other I/O errors
                    System.out.println("Network error: " + e.getMessage());
                }
                break;

                case DOHVATANJE_SVIH_MESTA:
                    try {
                    Response<List<Mesto>> response = new DohvatanjeSvihMesta()
                            .execute(scanner, service)
                            .execute();

                    if (response.isSuccessful() && response.code() == 200) {
                        // Handle the 200 OK response
                        String formatted = ListPrinter.formatMestoList(response.body());
                        System.out.println("Sva mesta: ");
                        System.out.println(formatted);
                    } else {
                        ResponseHandler.handleErrorResponse(response);
                    }
                } catch (IOException e) {
                    // Handle network or other I/O errors
                    System.out.println("Network error: " + e.getMessage());
                }
                break;

                case DOHVATANJE_SVIH_KORISNIKA:
                    try {
                    Response<List<Korisnik>> response = new DohvatanjeSvihKorisnika()
                            .execute(scanner, service)
                            .execute();

                    if (response.isSuccessful() && response.code() == 200) {
                        // Handle the 200 OK response
                        String formatted = ListPrinter.formatKorisnikList(response.body());
                        System.out.println("Svi korisnici: ");
                        System.out.println(formatted);
                    } else {
                        ResponseHandler.handleErrorResponse(response);
                    }
                } catch (IOException e) {
                    // Handle network or other I/O errors
                    System.out.println("Network error: " + e.getMessage());
                }
                break;

                case DOHVATANJE_SVIH_KATEGORIJA:
                    try {
                    Response<List<Kategorija>> response = new DohvatanjeSvihKategorija()
                            .execute(scanner, service)
                            .execute();

                    if (response.isSuccessful() && response.code() == 200) {
                        // Handle the 200 OK response
                        String formatted = ListPrinter.formatKategorijaList(response.body());
                        System.out.println("Sve kategorije: ");
                        System.out.println(formatted);
                    } else {
                        ResponseHandler.handleErrorResponse(response);
                    }
                } catch (IOException e) {
                    // Handle network or other I/O errors
                    System.out.println("Network error: " + e.getMessage());
                }
                break;

                case DOHVATANJE_SVIH_VIDEO_SNIMAKA:
                    try {
                    Response<List<Video>> response = new DohvatanjeSvihVidea()
                            .execute(scanner, service)
                            .execute();

                    if (response.isSuccessful() && response.code() == 200) {
                        // Handle the 200 OK response
                        String formatted = ListPrinter.formatVideoList(response.body());
                        System.out.println("Svi video snimci: ");
                        System.out.println(formatted);
                    } else {
                        ResponseHandler.handleErrorResponse(response);
                    }
                } catch (IOException e) {
                    // Handle network or other I/O errors
                    System.out.println("Network error: " + e.getMessage());
                }
                break;

                case DOHVATANJE_KATEGORIJA_ZA_VIDEO:
                    try {
                    Response<List<Kategorija>> response = new DohvatanjeKategorijaZaVideo()
                            .execute(scanner, service)
                            .execute();

                    if (response.isSuccessful() && response.code() == 200) {
                        // Handle the 200 OK response
                        String formatted = ListPrinter.formatKategorijaList(response.body());
                        System.out.println("Sve kategorije izabranog videa: ");
                        System.out.println(formatted);
                    } else {
                        ResponseHandler.handleErrorResponse(response);
                    }
                } catch (IOException e) {
                    // Handle network or other I/O errors
                    System.out.println("Network error: " + e.getMessage());
                }
                break;

                case DOHVATANJE_SVIH_PAKETA:
                    try {
                    Response<List<Paket>> response = new DohvatanjeSvihPaketa()
                            .execute(scanner, service)
                            .execute();

                    if (response.isSuccessful() && response.code() == 200) {
                        // Handle the 200 OK response
                        String formatted = ListPrinter.formatPaketList(response.body());
                        System.out.println("Svi paketi: ");
                        System.out.println(formatted);
                    } else {
                        ResponseHandler.handleErrorResponse(response);
                    }
                } catch (IOException e) {
                    // Handle network or other I/O errors
                    System.out.println("Network error: " + e.getMessage());
                }
                break;

                case DOHVATANJE_PRETPLATA_ZA_KORISNIKA:
                    try {
                    Response<List<Pretplata>> response = new DohvatanjePretplataZaKorisnika()
                            .execute(scanner, service)
                            .execute();

                    if (response.isSuccessful() && response.code() == 200) {
                        // Handle the 200 OK response
                        String formatted = ListPrinter.formatPretplataList(response.body());
                        System.out.println("Sve pretplate izabranog videa: ");
                        System.out.println(formatted);
                    } else {
                        ResponseHandler.handleErrorResponse(response);
                    }
                } catch (IOException e) {
                    // Handle network or other I/O errors
                    System.out.println("Network error: " + e.getMessage());
                }
                break;

                case DOHVATANJE_GLEDANJA_ZA_VIDEO:
                     try {
                    Response<List<Gledanje>> response = new DohvatanjeGledanjaZaVideo()
                            .execute(scanner, service)
                            .execute();

                    if (response.isSuccessful() && response.code() == 200) {
                        // Handle the 200 OK response
                        String formatted = ListPrinter.formatGledanjeList(response.body());
                        System.out.println("Sva gledanja izabranog videa: ");
                        System.out.println(formatted);
                    } else {
                        ResponseHandler.handleErrorResponse(response);
                    }
                } catch (IOException e) {
                    // Handle network or other I/O errors
                    System.out.println("Network error: " + e.getMessage());
                }
                break;

                case DOHVATANJE_OCENA_ZA_VIDEO:
                    try {
                    Response<List<Ocena>> response = new DohvatanjeOcenaZaVideo()
                            .execute(scanner, service)
                            .execute();

                    if (response.isSuccessful() && response.code() == 200) {
                        // Handle the 200 OK response
                        String formatted = ListPrinter.formatOcenaList(response.body());
                        System.out.println("Sve ocene izabranog videa: ");
                        System.out.println(formatted);
                    } else {
                        ResponseHandler.handleErrorResponse(response);
                    }
                } catch (IOException e) {
                    // Handle network or other I/O errors
                    System.out.println("Network error: " + e.getMessage());
                }
                break;

                default:
                    System.out.println("Izlazak iz programa");
                    scanner.close(); // Closing the scanner when exiting the program
                    return;
            }
        }
    }

    private static void displayMenu() {
        System.out.println("--------------------------------------------"); // Separator line
        System.out.println("Odaberite opciju 0-25:");
        System.out.println("0. Izlaz");
        System.out.println("1. Kreiranje grada");
        System.out.println("2. Kreiranje korisnika");
        System.out.println("3. Promena email adrese za korisnika");
        System.out.println("4. Promena mesta za korisnika");
        System.out.println("5. Kreiranje kategorije");
        System.out.println("6. Kreiranje video snimka");
        System.out.println("7. Promena naziva video snimka");
        System.out.println("8. Dodavanje kategorije video snimku");
        System.out.println("9. Kreiranje paketa");
        System.out.println("10. Promena mesecne cene za paket");
        System.out.println("11. Kreiranje pretplate korisnika na paket");
        System.out.println("12. Kreiranje gledanja video snimka od strane korisnika");
        System.out.println("13. Kreiranje ocene korisnika za video snimak");
        System.out.println("14. Menjanje ocene korisnika za video snimak");
        System.out.println("15. Brisanje ocene korisnika za video snimak");
        System.out.println("16. Brisanje video snimka od strane korisnika koji ga je kreirao");
        System.out.println("17. Dohvatanje svih mesta");
        System.out.println("18. Dohvatanje svih korisnika");
        System.out.println("19. Dohvatanje svih kategorija");
        System.out.println("20. Dohvatanje svih video snimaka");
        System.out.println("21. Dohvatanje kategorija za odredjeni video snimak");
        System.out.println("22. Dohvatanje svih paketa");
        System.out.println("23. Dohvatanje svih pretplata za korisnika");
        System.out.println("24. Dohvatanje svih gledanja za video snimak");
        System.out.println("25. Dohvatanje svih ocena za video snimak");
        System.out.println("--------------------------------------------"); // Separator line

    }

}
