package entitydisambiguation;

import java.io.IOException;

/**
 * Created by gadelrab on 10/27/16.
 */
public interface DocumentAnnotator {


    public void annotate(Document document) throws IOException;
}
