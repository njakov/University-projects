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
public class OKResponse extends JMSResponse {

    private static final long serialVersionUID = 1L;

    public OKResponse(ServerRequest req) {
        super(req);
    }

    @Override
    public boolean isSuccessful() {
        return true;
    }

}
