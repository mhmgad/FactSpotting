package entitydisambiguation;


import java.io.IOException;

import com.ambiverse.api.AmbiverseApiClient;
import com.ambiverse.api.Credential;
import com.ambiverse.api.model.AnalyzeInput;
import com.ambiverse.api.model.AnalyzeOutput;
import com.ambiverse.api.model.Match;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import de.mpii.containers.AnnotatedDocument;
import de.mpii.containers.Mention;
import de.mpii.containers.Mentions;


public class AmbiverseDocumentAnnotator implements DocumentAnnotator {



    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport httpTransport = new NetHttpTransport();
    private static AmbiverseDocumentAnnotator documentAnnotator;

    private AmbiverseApiClient client = null;

    /**
     * Creates and initializes a new instance of {@link AmbiverseApiClient}.
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

    }


    @Override
    public Mentions annotate(AnnotatedDocument doc) throws IOException {

        // Prepare the analysis input, an English text fragment in this case.
        AnalyzeInput input = new AnalyzeInput()
                .withLanguage("en")     // Optional. If not set, language detection happens automatically.
                .withText(doc.getText());


        AnalyzeOutput output = client.entityLinking().analyze().process(input).execute();
//        System.out.println(output.getMatches());

        Mentions mentions=new Mentions();
        for (Match match:output.getMatches()       ) {
            Mention em=new Mention();
            em.setText(match.getText());                       // Span of text in the input that was linked.
            em.setCharOffset(match.getCharOffset());          // Character offset of the match in the original text.
            em.setCharLength(match.getCharLength());          // Character length of the match.
            em.setEntity(new de.mpii.containers.Entity(match.getEntity().getId(),match.getEntity().getScore()));         // ID of the linked entity, e.g. "YAGO3:<The_Who>".
            em.setConfidence(match.getEntity().getScore());    // Confidence score.
            mentions.add(em);
        }
        return mentions;
    }


    public static DocumentAnnotator getInstance() throws Exception{
        
        if(documentAnnotator==null) {
            documentAnnotator = new AmbiverseDocumentAnnotator();

            try {
                documentAnnotator.initialize();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return documentAnnotator;
    }

    public static void main(String[] args) throws IOException {
        AmbiverseDocumentAnnotator helloApi = new AmbiverseDocumentAnnotator();
        helloApi.initialize();
        String text="Albert Einstein (/ˈaɪnstaɪn/;[4] German: [ˈalbɛɐ̯t ˈaɪnʃtaɪn] ( listen); 14 March 1879 – 18 April 1955) was a German-born theoretical physicist. He developed the general theory of relativity, one of the two pillars of modern physics (alongside quantum mechanics).[1][5]:274 Einstein's work is also known for its influence on the philosophy of science.[6][7] Einstein is best known in popular culture for his mass–energy equivalence formula E = mc2 (which has been dubbed \"the world's most famous equation\").[8] He received the 1921 Nobel Prize in Physics for his \"services to theoretical physics\", in particular his discovery of the law of the photoelectric effect, a pivotal step in the evolution of quantum theory.[9]\n" +
                "Near the beginning of his career, Einstein thought that Newtonian mechanics was no longer enough to reconcile the laws of classical mechanics with the laws of the electromagnetic field. This led to the development of his special theory of relativity. He realized, however, that the principle of relativity could also be extended to gravitational fields, and with his subsequent theory of gravitation in 1916, he published a paper on general relativity. He continued to deal with problems of statistical mechanics and quantum theory, which led to his explanations of particle theory and the motion of molecules. He also investigated the thermal properties of light which laid the foundation of the photon theory of light. In 1917, Einstein applied the general theory of relativity to model the large-scale structure of the universe.[10][11]";

        AnnotatedDocument d=new AnnotatedDocument(text);

        helloApi.annotate(d);

        //System.out.println(d.getEntityMentions());
        d.printEntityMentions();


    }

}
