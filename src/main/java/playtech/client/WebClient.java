package playtech.client;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import playtech.service.WalletStatus;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class WebClient implements Runnable {
    private static int transaction = 0;
    private  String baseURL = "";
    private  String methodName = "";
    public WebClient(String baseURL, String method)
    {
        this.baseURL = baseURL;
        this.methodName = method;
    }
    @Override
    public  void run() {
        ClientConfig clientConfig = new DefaultClientConfig();
        //clientConfig.getClasses().add(com.sun.jersey.impl.provider.entity.JSONRootElementProvider.class);
        com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create(clientConfig);
        try {
            Thread.sleep((int) (Math.random() * 1000));

        } catch (InterruptedException ie) {
            System.out.println(ie.getMessage());
        }

        WebResource resource = client.resource(getBaseURI());
        RandomUserGenerator user = new RandomUserGenerator();

        ClientResponse response = resource.path(methodName).
                path(user.getUserName()).
                path(getTransactionId()).
                path(user.getBalanceChange()).
                accept(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class);

        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus());
        }

        WalletStatus output = response.getEntity(WalletStatus.class);

        System.out.println("Output from Server .... \n");
        if (output.errorCode == 0) {
            System.out.println("Transaction: " + output.transactionId);
            System.out.println("Balance: " + output.balanceAfterChange);
        } else {
            System.out.println("Error " + output.errorCode);
        }
    }

    private  URI getBaseURI() {
        return UriBuilder.fromUri(baseURL).build();
    }

    private  String getTransactionId() {
        return String.valueOf(transaction++);
    }
}
