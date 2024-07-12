/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.ac.bg.etf.is1.projekat.client.services;

/**
 *
 * @author ninaj
 */
import etf.is1.projekat.entities.Gledanje;
import etf.is1.projekat.entities.Kategorija;
import etf.is1.projekat.entities.Korisnik;
import etf.is1.projekat.entities.Mesto;
import etf.is1.projekat.entities.Ocena;
import etf.is1.projekat.entities.Paket;
import etf.is1.projekat.entities.Pretplata;
import etf.is1.projekat.entities.Video;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    //Podsistem 1
    @GET("/Server/resources/podsistem1/getMesta")
    public Call<List<Mesto>> getMesta();

    @GET("/Server/resources/podsistem1/getKorisnici")
    public Call<List<Korisnik>> getKorisnici();

    @FormUrlEncoded
    @POST("/Server/resources/podsistem1/createMesto")
    public Call<String> createMesto(@Field("Naziv") String naziv);

    @FormUrlEncoded
    @POST("/Server/resources/podsistem1/createKorisnik")
    public Call<String> createKorisnik(@Field("ime") String ime, @Field("email") String naziv,
            @Field("godiste") int godiste, @Field("pol") Character pol,
            @Field("mesto") String mesto);

    @FormUrlEncoded
    @POST("/Server/resources/podsistem1/changeEmail")
    public Call<String> changeEmail(@Field("sifK") int sifK, @Field("email") String email);

    @FormUrlEncoded
    @POST("/Server/resources/podsistem1/changeMesto")
    public Call<String> changeMesto(@Field("sifK") int sifK, @Field("naziv") String naziv);

    //Podsistem 2
    @GET("/Server/resources/podsistem2/getKategorije")
    public Call<List<Kategorija>> getKategorije();

    @GET("/Server/resources/podsistem2/getVidei")
    public Call<List<Video>> getVidei();

    @GET("/Server/resources/podsistem2/getKategorijeVidea/{sifV}")
    public Call<List<Kategorija>> getKategorijeVidea(@Path("sifV") int sifV);

    @FormUrlEncoded
    @POST("/Server/resources/podsistem2/createKategorija")
    public Call<String> createKategorija(@Field("Naziv") String naziv);

    @FormUrlEncoded
    @POST("/Server/resources/podsistem2/createVideo")
    public Call<String> createVideo(@Field("Naziv") String naziv, @Field("Trajanje") int trajanje, @Field("SifK") int sifK);

    @FormUrlEncoded
    @POST("/Server/resources/podsistem2/changeNazivVidea")
    public Call<String> changeNazivVidea(@Field("sifV") int sifV, @Field("naziv") String naziv);

    @FormUrlEncoded
    @POST("/Server/resources/podsistem2/dodajKategoriju")
    public Call<String> dodajKategoriju(@Field("sifV") int sifV, @Field("sifKat") int sifKat);

    @DELETE("/Server/resources/podsistem2/deleteVideo/{sifV}/{sifK}")
    public Call<String> deleteVideo(@Path("sifV") int sifV, @Path("sifK") int sifK);

    //Podsistem 3
    @GET("/Server/resources/podsistem3/getPaketi")
    public Call<List<Paket>> getPaketi();

    @GET("/Server/resources/podsistem3/getPretplateKorisnika/{sifK}")
    public Call<List<Pretplata>> getPretplateKorisnika(@Path("sifK") int sifK);

    @GET("/Server/resources/podsistem3/getGledanja/{sifV}")
    public Call<List<Gledanje>> getGledanja(@Path("sifV") int sifV);

    @GET("/Server/resources/podsistem3/getOcene/{sifV}")
    public Call<List<Ocena>> getOcene(@Path("sifV") int sifV);

    @FormUrlEncoded
    @POST("/Server/resources/podsistem3/createPaket")
    public Call<String> createPaket(@Field("naziv") String naziv, @Field("cena") int cena);

    @FormUrlEncoded
    @POST("/Server/resources/podsistem3/changeCena")
    public Call<String> changeCena(@Field("sifPak") int sifPak, @Field("cena") int cena);

    @FormUrlEncoded
    @POST("/Server/resources/podsistem3/createPretplata")
    public Call<String> createPretplata(@Field("sifK") int sifK, @Field("sifPak") int sifPak);

    @FormUrlEncoded
    @POST("/Server/resources/podsistem3/createOcena")
    public Call<String> createOcena(@Field("sifK") int sifK, @Field("sifV") int sifV, @Field("ocena") int ocena);

    @FormUrlEncoded
    @POST("/Server/resources/podsistem3/changeOcena")
    public Call<String> changeOcena(@Field("sifK") int sifK, @Field("sifV") int sifV, @Field("ocena") int ocena);

    @FormUrlEncoded
    @POST("/Server/resources/podsistem3/createGledanje")
    public Call<String> createGledanje(@Field("sifK") int sifK, @Field("sifV") int sifV, @Field("zapoceto") int zapoceto, @Field("odgledano") int odgledano);

    @DELETE("/Server/resources/podsistem3/deleteOcena/{sifV}/{sifK}")
    public Call<String> deleteOcena(@Path("sifV") int sifV, @Path("sifK") int sifK);

}
