package de.mpii.factspotting.text;

import de.mpii.datastructures.BinaryFact;
import de.mpii.datastructures.Document;
import de.mpii.datastructures.Fact;
import de.mpii.factspotting.IFactSpotter;
import de.mpii.factspotting.ISpottedEvidence;
import de.mpii.factspotting.config.Configuration;
import de.mpii.factspotting.text.verbalization.IFactVerbalizer;
import de.mpii.factspotting.text.verbalization.IParaphrase;
import de.mpii.factspotting.text.verbalization.VerbalizerFactory;
import de.mpii.factspotting.text.retrievers.BingRetriever;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class BingFactSpotter implements IFactSpotter<Fact> {


    BingRetriever bingRetriever;
    IFactVerbalizer<Fact> verbalizer;

    public BingFactSpotter() {
        this(Configuration.getInstance().getEvidencePerFactSize(),VerbalizerFactory.getInstance());


    }

    public BingFactSpotter(int evidencePerFactSize, IFactVerbalizer verbalizer) {
        this.bingRetriever=BingRetriever.getInstance();
        this.verbalizer=verbalizer;
    }

    @Override
    public ISpottedEvidence spot(Fact fact) {
        List<IParaphrase> verbalizations=verbalizer.getVerbalizations(fact);

        List<String> stringsList = verbalizations.stream().sorted().map(v -> v.getSearchableString()).collect(Collectors.toList());

        try {
            List<Document> docs = bingRetriever.searchParaphrases(stringsList);
            System.out.println("docs "+docs.size() );
            System.out.println(docs);
            return new TextEvidence(docs);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public ISpottedEvidence spotFullDocuments(Fact fact) {
        List<IParaphrase> verbalizations=verbalizer.getVerbalizations(fact);

        List<String> stringsList = verbalizations.stream().sorted().map(v -> v.getSearchableString()).collect(Collectors.toList());

        try {
            List<Document> docs = bingRetriever.searchParaphrasesFullDocument(stringsList);
            System.out.println("docs "+docs.size() );
            System.out.println(docs);
            return new TextEvidence(fact,docs);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }





    public static void main(String[] args) {
//        BinaryFact f=new BinaryFact("Albert_Einstein","wasBornIn","Ulm");
//        BinaryFact f=new BinaryFact("Albert_Einstein","wasBornIn","");

        Fact f=new BinaryFact("Albert_Einstein","wasBornIn","ULM");
        System.out.println(Configuration.getInstance());

        BingFactSpotter fs=new BingFactSpotter();
//        ElasticSearchFactSpotter fs=new ElasticSearchFactSpotter("wiki_sent",fields,5,VerbalizerFactory.getInstance());

        ISpottedEvidence ev = fs.spot(f);
        System.out.println(ev.size());
        System.out.println(ev);
        System.out.println(ev.getEntities());
    }
}
