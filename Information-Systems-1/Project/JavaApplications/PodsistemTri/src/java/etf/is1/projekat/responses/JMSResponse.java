/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.is1.projekat.responses;

import etf.is1.projekat.server.requests.ServerRequest;
import java.io.Serializable;
import java.util.UUID;

/**
 *
 * @author ninaj
 */
public abstract class JMSResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    protected final ServerRequest req;

    public JMSResponse(ServerRequest req) {
        this.req = req;
    }

    public String getID() {
        return req.getId();
    }

    abstract public boolean isSuccessful();

    @Override
    public String toString() {
        return "Response[id=" + getID() + ", type=" + req.getId() + ", from=" + req.getOdrediste() + "]";
    }

}
