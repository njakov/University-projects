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
public class PromeniOcenuZaVideo extends RetrofitCall {

    @Override
    public String name() {
        return "Promeni ocenu za video sa zadatim ID-ijem.";
    }

    @Override
    public Call<String> execute(Scanner scanner, ApiService service) {
        System.out.println("Unesite ID korisnika: ");
        int sifK = Integer.parseInt(scanner.nextLine());
        System.out.println("Unesite ID videa: ");
        int sifV = Integer.parseInt(scanner.nextLine());
        System.out.println("Unesite novu ocenu (1-5): ");
        int ocena = Integer.parseInt(scanner.nextLine());

        return service.changeOcena(sifK, sifV, ocena);
    }

}
