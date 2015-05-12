package amilcarmenjivar.decisionmaking;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Amilcar Menjivar on 02/05/2015.
 */
public class Pair {

    public final String elem1, elem2;

    public Pair(String elem1, String elem2) {
        this.elem1 = elem1;
        this.elem2 = elem2;
    }


    public String toString() {
        return elem1 + " vs " + elem2;
    }

    public int hashCode() {
        return elem1.hashCode() * 31 + elem2.hashCode();
    }

    public boolean equals(Object o) {
        if(o != null && o instanceof Pair) {
            Pair pair = (Pair) o;
            return elem1.equals(pair.elem1) && elem2.equals(pair.elem2);
        }
        return false;
    }

    public Pair reverse() {
        return new Pair(this.elem2, this.elem1);
    }

    public static boolean loading = false;

    public static List<Pair> makePairs(List<String> elements) {
        List<Pair> pairs = new ArrayList<Pair>();
        if(elements.size() <= 1) {
            return pairs; // Wrong state: need AT LEAST 2 to makes pairs.
        }

        for(int i=0; i<elements.size()-1; i++) {
            for(int e=i+1; e<elements.size(); e++) {
                Pair pair = new Pair(elements.get(i), elements.get(e));
                pairs.add(pair);
            }
        }
        return pairs;
    }

}
