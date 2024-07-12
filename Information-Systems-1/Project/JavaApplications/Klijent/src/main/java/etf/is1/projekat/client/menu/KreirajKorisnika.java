/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.is1.projekat.client.menu;

import java.util.Scanner;
import retrofit2.Call;
import rs.ac.bg.etf.is1.projekat.client.services.ApiService;

/**
 *
 * @author ninaj
 */
public class KreirajKorisnika extends RetrofitCall {

    @Override
    public String name() {
        return "Kreiranje novog korisnika";
    }

    @Override
    public Call<String> execute(Scanner scanner, ApiService service) {
        System.out.println("Unesite ime korisnika: ");
        String ime = scanner.nextLine();
        System.out.println("Unesite email korisnika: ");
        String email = scanner.nextLine();
        System.out.println("Unesite godinu rodjenja: ");
        int godiste = Integer.parseInt(scanner.nextLine());
        System.out.println("Unesite pol (M/F): ");
        char pol = scanner.nextLine().charAt(0);
        System.out.println("Unesite naziv mesta: ");
        String mesto = scanner.nextLine();

        return service.createKorisnik(ime, email, godiste, pol, mesto);
    }

}
