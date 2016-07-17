package org.github.sdarioo.jaxrs.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.UriBuilder;

/**
 * Hello world!
 *
 */
public class App 
{
	private static final String BASE_URL = "http://localhost:8080/webapi";
	
    public static void main( String[] args )
    {
        Client client = ClientBuilder.newClient();
        try {
        	UriBuilder builder = UriBuilder.fromUri(BASE_URL);
        	//builder.path("myresource");
        	
        	String entity = client.target(builder).path("myresource").request().get(String.class);
        	        	
        	System.out.println("GET: " + entity);
        } finally {
        	client.close();
        }
    }
}
