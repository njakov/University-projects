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
public class ErrorResponse extends JMSResponse {

    private final String reason;

    private static final long serialVersionUID = 1L;

    public ErrorResponse(ServerRequest req, String reason) {
        super(req);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public boolean isSuccessful() {
        return false;
    }
}
