package amilcarmenjivar.decisionmaking;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * Created by Amilcar Menjivar on 04/05/2015.
 */
public class Data {

    private final List<String> criteria;
    private final List<String> elements;
    private final List<String> judges;
    private final Map<String, Map<Pair, Map<String, Integer>>> data;

    private boolean hasChanged = true;
    private int[][][] lastRawData = null;

    private List<Pair> currentPairs;

    private static final int DEFAULT_VALUE = 1;

    public Data(List<String> criteria, List<String> elements, List<String> judges, int[][][] rawData) {
        this.criteria = criteria;
        this.elements = elements;
        this.judges = judges;
        this.data = decodeRawData(rawData);
    }

    public List<Pair> getPairs() {
        return currentPairs;
    }

    public void addCriteria(String criteria) {
        hasChanged = true;

        Map<Pair, Map<String, Integer>> pairsMap = new LinkedHashMap<Pair, Map<String, Integer>>();
        for(Pair pair : currentPairs) {
            Map<String, Integer> judgesMap = new LinkedHashMap<String, Integer>();
            for(String judge : judges)
                judgesMap.put(judge, DEFAULT_VALUE);
            pairsMap.put(pair, judgesMap);
        }

        data.put(criteria, pairsMap);
    }

    public boolean removeCriteria(String criteria) {
        hasChanged = true;
        return data.remove(criteria) != null;
    }

    public void updateElements() {
        hasChanged = true;
        List<Pair> newPairs = Pair.makePairs(elements);
        currentPairs = newPairs;
        for(String criteria : data.keySet()) {
            Set<Pair> oldPairs = data.get(criteria).keySet();

            // Adding a new element
            if(newPairs.size() > oldPairs.size()) {
                for(Pair pair : newPairs) {
                    if(!oldPairs.contains(pair)) {
                        Map<String, Integer> judgesMap = new LinkedHashMap<String, Integer>();
                        for(String judge : judges)
                            judgesMap.put(judge, DEFAULT_VALUE);
                        data.get(criteria).put(pair, judgesMap);
                    }
                }

            // Removing an old element
            } else if(newPairs.size() < oldPairs.size()) {
                for(Pair pair : newPairs) {
                    if(!newPairs.contains(pair)) {
                        data.get(criteria).remove(pair);
                    }
                }
            }
        }
    }

    public void addJudge(String judge) {
        hasChanged = true;
        for(String key : data.keySet()) {
            for(Pair pair : data.get(key).keySet()) {
                data.get(key).get(pair).put(judge, DEFAULT_VALUE);
            }
        }
    }

    public void removeJudge(String judge) {
        hasChanged = true;
        for(String key : data.keySet()) {
            for(Pair pair : data.get(key).keySet()) {
                data.get(key).get(pair).remove(judge);
            }
        }
    }

    public int getValue(String criteria, Pair pair, String judge) {
        if(data.containsKey(criteria)) {
            if(data.get(criteria).containsKey(pair)){
                if(data.get(criteria).get(pair).containsKey(judge)){
                    return data.get(criteria).get(pair).get(judge);
                }
                throw new IllegalArgumentException("Data does not contain judge: "+judge);
            }
            throw new IllegalArgumentException("Data does not contain pair: "+pair);
        }
        throw new IllegalArgumentException("Data does not contain criteria: "+criteria);
    }

    public void write(int c, int p, int j, int value) {
        String criteria = this.criteria.get(c);
        Pair pair = this.currentPairs.get(p);
        String judge = this.judges.get(j);

        data.get(criteria).get(pair).put(judge, value);
        if (lastRawData == null) {
            hasChanged = true;
        } else {
            lastRawData[c][p][j] = value;
        }
    }

    // The indexes must be consistent with the order in the lists.
    public int[][][] getRawData() {
        if (!hasChanged) {
            return lastRawData;
        }

        int[][][] rawData = new int[criteria.size()][currentPairs.size()][judges.size()];
        int c = 0;
        for(String criterion : criteria) {
            int p = 0;
            for(Pair pair : currentPairs) {
                int j = 0;
                for(String judge : judges) {
                    rawData[c][p][j] = data.get(criterion).get(pair).get(judge);
                    j++;
                }
                p++;
            }
            c++;
        }

        lastRawData = rawData;
        hasChanged = false;
        return rawData;

    }

    public static Data emptyData(List<String> criteria, List<String> elements, List<String> judges) {
        return new Data(criteria, elements, judges, null);
    }

    private Map<String, Map<Pair, Map<String, Integer>>> decodeRawData(int[][][] rawData) {
        // Structure: (Criteria:( Pair:(Judge: Integer) ))

        Map<String, Map<Pair, Map<String, Integer>>> map = new LinkedHashMap<String, Map<Pair, Map<String, Integer>>>();
        Pair.loading = true;
        currentPairs = Pair.makePairs(elements);
        Pair.loading = false;
        for(int c=0; c<criteria.size(); c++) {
            Map<Pair, Map<String, Integer>> pairMap = new LinkedHashMap<Pair, Map<String, Integer>>();
            for(int p=0; p<currentPairs.size(); p++) {
                Map<String, Integer> judgeMap = new LinkedHashMap<String, Integer>();
                for(int j=0; j<judges.size(); j++) {
                    int value = rawData == null ? DEFAULT_VALUE : rawData[c][p][j];
                    judgeMap.put(judges.get(j), value == 0 ? DEFAULT_VALUE : value);
                }
                pairMap.put(currentPairs.get(p), judgeMap);
            }
            map.put(criteria.get(c), pairMap);
        }
        return map;
    }

}
