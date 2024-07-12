/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.is1.projekat.client.menu;

import etf.is1.projekat.entities.Gledanje;
import java.util.List;
import java.util.Scanner;
import retrofit2.Call;
import rs.ac.bg.etf.is1.projekat.client.services.ApiService;

/**
 *
 * @author ninaj
 */
public class DohvatanjeGledanjaZaVideo extends RetrofitCall {

    @Override
    public String name() {
        return "Dohvatanje liste svih gledanja za video sa zadatim ID-ijem.";
    }

    @Override
    public Call<List<Gledanje>> execute(Scanner scanner, ApiService service) {
        System.out.println("Unesite ID videa: ");
        int sifV = Integer.parseInt(scanner.nextLine());

        return service.getGledanja(sifV);
    }

}
