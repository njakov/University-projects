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
public class KreirajPaket extends RetrofitCall {

    @Override
    public String name() {
        return "Kreiranje novog paketa.";
    }

    @Override
    public Call<String> execute(Scanner scanner, ApiService service) {
        System.out.println("Unesite naziv paketa: ");
        String naziv = scanner.nextLine();
        System.out.println("Unesite cenu paketa: ");
        int cena = Integer.parseInt(scanner.nextLine());

        return service.createPaket(naziv, cena);
    }

}
