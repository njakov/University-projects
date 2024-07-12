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
public class KreirajGledanje extends RetrofitCall{

    @Override
    public String name() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Call<String> execute(Scanner scanner, ApiService service) {
        System.out.println("Unesite ID korisnika: ");
        int sifK = Integer.parseInt(scanner.nextLine());
        System.out.println("Unesite ID videa: ");
        int sifV = Integer.parseInt(scanner.nextLine());
        System.out.println("Unesite minut od koga zapocinje gledanje: ");
        int zapoceto = Integer.parseInt(scanner.nextLine());
        System.out.println("Unesite minut gde se zavrsilo gledanje: ");
        int odgledano = Integer.parseInt(scanner.nextLine());
        
        
        return service.createGledanje(sifK, sifV, zapoceto, odgledano);  //To change body of generated methods, choose Tools | Templates.
    }
    
}
