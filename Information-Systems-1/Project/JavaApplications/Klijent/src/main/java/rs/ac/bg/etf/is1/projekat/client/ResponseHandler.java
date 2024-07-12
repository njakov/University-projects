/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.ac.bg.etf.is1.projekat.client;

import java.io.IOException;
import retrofit2.Response;

/**
 *
 * @author ninaj
 */
public class ResponseHandler {

    public static <T> void handleResponse(Response<T> response) {
        if (response.isSuccessful()) {
            handleSuccessfulResponse(response);
        } else {
            handleErrorResponse(response);
        }
    }

    private static <T> void handleSuccessfulResponse(Response<T> response) {
        int statusCode = response.code();
        if (statusCode == 201) {
            System.out.println("Created: " + response.body());
        } else {
            System.out.println("Success: " + response.body());
        }
    }

    public static <T> void handleErrorResponse(Response<T> response) {
        System.err.println("Request failed with code: " + response.code());

        try {
            if (response.errorBody() != null) {
                String serverMsg = response.errorBody().string();
                System.err.println("Server message: " + serverMsg);
            } else {
                System.err.println("Error message: " + response.message());

            }
        } catch (IOException e) {
            System.err.println("Error reading error body: " + e.getMessage());
            e.printStackTrace();
        }

    }

}
