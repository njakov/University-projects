/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.is1.projekat.handlers;

import javax.persistence.EntityManager;
import etf.is1.projekat.server.requests.ServerRequest;
import etf.is1.projekat.responses.JMSResponse;

/**
 *
 * @author ninaj
 */
public abstract class RequestHandler {

    protected final EntityManager em;

    public RequestHandler(EntityManager em) {
        this.em = em;
    }

    abstract public JMSResponse handle(ServerRequest req);

}
