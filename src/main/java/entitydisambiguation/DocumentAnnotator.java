package entitydisambiguation;

import java.io.IOException;

/**
 * Created by gadelrab on 10/27/16.
 */
public interface DocumentAnnotator {


    public AnnotatedDocument annotate(Document d) throws IOException;
}
