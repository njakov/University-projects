/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.is1.projekat.client.menu;

import etf.is1.projekat.entities.Korisnik;
import java.util.List;
import java.util.Scanner;
import retrofit2.Call;
import rs.ac.bg.etf.is1.projekat.client.services.ApiService;

/**
 *
 * @author ninaj
 */
public class DohvatanjeSvihKorisnika extends RetrofitCall {

    @Override
    public String name() {
        return "Dohvati sve korisnike.";
    }

    @Override
    public Call<List<Korisnik>> execute(Scanner scanner, ApiService service) {
        return service.getKorisnici();
    }

}
