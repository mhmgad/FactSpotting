package entitydisambiguation;


import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import com.ambiverse.api.AmbiverseApiClient;
import com.ambiverse.api.Credential;
import com.ambiverse.api.model.AnalyzeInput;
import com.ambiverse.api.model.AnalyzeOutput;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientCredentialsTokenRequest;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;


/**
 * Created by gadelrab on 10/26/16.
 */
public class AmbiverseDocumentAnnotator implements DocumentAnnotator {



    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport httpTransport = new NetHttpTransport();

    private AmbiverseApiClient client = null;

    /**
     * Creates and initializes a new instance of {@link AmbiverseApiClient}.
     *
     * @return Initialized instance of {@link AmbiverseApiClient}
     * @throws IOException
     */
    private void initialize() throws IOException {
  /*
  * Authenticates your client application against the Ambiverse API endpoint via the OAuth 2
  * protocol. Your client credentials are read from client_secrets.json on your classpath and
  * exchanged for an API access token, which is stored within the API client throughout your
  * session.
  */
        Credential credential = AmbiverseApiClient.authorize(httpTransport, JSON_FACTORY);

        // Instantiate a new API client.
        this.client = new AmbiverseApiClient(httpTransport, JSON_FACTORY, credential);
        System.out.println(this.client);
    }


    @Override
    public AnnotatedDocument annotate(Document d) throws IOException {

        // Prepare the analysis input, an English text fragment in this case.
        AnalyzeInput input = new AnalyzeInput()
                .withLanguage("en")     // Optional. If not set, language detection happens automatically.
                .withText("When [[Who]] played Tommy in Columbus, Pete was at his best.");

        AnalyzeOutput output = client.entityLinking().analyze().process(input).execute();
        System.out.println(output.getMatches());

        return null;
    }

    public static void main(String[] args) throws IOException {
        AmbiverseDocumentAnnotator helloApi = new AmbiverseDocumentAnnotator();
        helloApi.initialize();
        helloApi.annotate(null);


    }

}
