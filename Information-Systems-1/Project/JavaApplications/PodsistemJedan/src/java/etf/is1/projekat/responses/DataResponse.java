/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.is1.projekat.responses;

import etf.is1.projekat.server.requests.ServerRequest;

/**
 *
 * @author ninaj
 */
public class DataResponse<T> extends OKResponse {

    private final T data;
    private static final long serialVersionUID = 1L;

    public DataResponse(ServerRequest req, T data) {
        super(req);
        this.data = data;
    }

    public T getData() {
        return data;
    }
}
