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
public class DodavanjeKategorije extends RetrofitCall {

    @Override
    public String name() {
        return "Dodaj kategoriju za video sa zadatim ID-ijem.";
    }

    @Override
    public Call<String> execute(Scanner scanner, ApiService service) {
        System.out.println("Unesite ID video snimka: ");
        int sifV = Integer.parseInt(scanner.nextLine());
        System.out.println("Unesite ID kategorije: ");
        int sifKat = Integer.parseInt(scanner.nextLine());

        return service.dodajKategoriju(sifV, sifKat);
    }

}
