package de.mpii.factspotting.text;

import java.util.Collection;

/**
 * Created by gadelrab on 3/17/17.
 */
public interface IDictionary<T extends IParaphrase> {


    /**
     * get all possible paraphrases for the key
     * @param key
     * @return
     */
    public Collection<T> getParaphrases(String key);


    /**
     * Get top-k paraphrases for the key
     * @param key
     * @param topk
     * @return
     */
    public Collection<T> getParaphrases(String key, int topk);
}
