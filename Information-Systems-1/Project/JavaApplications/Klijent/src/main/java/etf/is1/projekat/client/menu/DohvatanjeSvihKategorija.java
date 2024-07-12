/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.is1.projekat.client.menu;

import etf.is1.projekat.entities.Kategorija;
import java.util.List;
import java.util.Scanner;
import retrofit2.Call;
import rs.ac.bg.etf.is1.projekat.client.services.ApiService;

/**
 *
 * @author ninaj
 */
public class DohvatanjeSvihKategorija extends RetrofitCall {

    @Override
    public String name() {
        return "Dohvati sve kategorije video snimaka.";
    }

    @Override
    public Call<List<Kategorija>> execute(Scanner scanner, ApiService service) {
        return service.getKategorije();
    }

}
