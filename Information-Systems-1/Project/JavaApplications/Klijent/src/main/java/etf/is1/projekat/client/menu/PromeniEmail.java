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
public class PromeniEmail extends RetrofitCall {

    @Override
    public String name() {
        return "Promena email-a za korisnika sa zadatim ID-ijem.";
    }

    @Override
    public Call<String> execute(Scanner scanner, ApiService service) {
        System.out.println("Unesite ID korisnika: ");
        int sifK = Integer.parseInt(scanner.nextLine());
        System.out.println("Unesite novi email korisnika: ");
        String email = scanner.nextLine();
        
        return service.changeEmail(sifK, email);
    }
    
}
