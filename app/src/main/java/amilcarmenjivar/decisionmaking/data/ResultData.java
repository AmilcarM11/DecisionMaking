package amilcarmenjivar.decisionmaking.data;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * This contains all the info used by ResultsFragment and ResultPageFragment
 *
 * Created by Amilcar Menjivar on 11/06/2015.
 */
public class ResultData {

    private static final double TIER_1_SCORE = 0.35;
    private static final double TIER_2_SCORE = 0.65;

    // Page x Criteria x Element = Score
    private double [][][] matrices;

    // Pages x Criteria x Index = Element
    private int[][][] indexes;

    // Pages x Criteria x Element = Tier
    private int[][][] tiers;

    public ResultData(Result mResult) {
        // Develop matrices based on result.
        int pages = ResultPage.values().length;
        matrices = new double[pages][][];
        indexes = new int[pages][][];
        tiers = new int[pages][][];

        // Copy the data for each page
        for(int page = 0; page < matrices.length; page++) {
            ResultPage p = ResultPage.values()[page];
            double[][] correctData = null;
            boolean transposed = false;

            switch(p) {
                case CANDIDATES_PER_PROFILE:
                    matrices[page] = mResult.resultMatrix;
                    correctData = matrices[page];
                    break;
                case ATTRIBUTES_PER_PROFILE:
                    matrices[page] = mResult.profilesMatrix;
                    correctData = matrices[page];
                    break;
                case CANDIDATES_PER_ATTRIBUTE:
                    matrices[page] = mResult.attributesMatrix;
                    correctData = matrices[page];
                    break;
                case ATTRIBUTES_PER_CANDIDATE:
                    matrices[page] = transpose(mResult.attributesMatrix);
                    correctData = mResult.attributesMatrix;
                    transposed = true;
                    break;
                case PROFILES_PER_CANDIDATE:
                    matrices[page] = transpose(mResult.resultMatrix);
                    correctData = mResult.resultMatrix;
                    transposed = true;
                    break;
                case PROFILES_PER_ATTRIBUTE:
                    matrices[page] = transpose(mResult.profilesMatrix);
                    correctData = mResult.profilesMatrix;
                    transposed = true;
                    break;
            }

            int elements = matrices[page].length;
            int criteria = matrices[page][0].length;

            indexes[page] = new int[criteria][elements];
            tiers[page] = new int[criteria][elements];

            for(int c = 0; c<criteria; c++) {
                // Order results by each criteria
                double[][] order = getOrderedResults(matrices[page], c);
                for(int e = 0; e<elements; e++) {
                    indexes[page][c][e] = (int) order[e][0];
                }

                // Determine the tier for each criteria
                if(transposed) {
                    for(int e=0; e<elements;e++) {
                        int tier = 0;
                        double maxScore = 0;
                        for (double[] actualScores : correctData) {
                            double s = actualScores[e];
                            if (s > maxScore)
                                maxScore = s;
                        }
                        double score = matrices[page][e][c];
                        if(score/maxScore > TIER_1_SCORE) {
                            tier = 1;
                        }
                        if(score/maxScore > TIER_2_SCORE) {
                            tier = 2;
                        }
                        tiers[page][c][e] = tier;
                    }
                } else {
                    double maxScore = order[0][1];
                    for(int e = 0; e<elements; e++) {
                        int tier = 0;
                        double score = matrices[page][e][c];
                        if(score/maxScore > TIER_1_SCORE) {
                            tier = 1;
                        }
                        if(score/maxScore > TIER_2_SCORE) {
                            tier = 2;
                        }
                        tiers[page][c][e] = tier;
                    }
                }
            }
        }
    }

    public List<String> getCriteriaByPage(int page) {
        switch(ResultPage.values()[page]) {
            case CANDIDATES_PER_PROFILE:
            case ATTRIBUTES_PER_PROFILE:
                return DataManager.getProfiles();
            case CANDIDATES_PER_ATTRIBUTE:
            case PROFILES_PER_ATTRIBUTE:
                return DataManager.getAttributes();
            case ATTRIBUTES_PER_CANDIDATE:
            case PROFILES_PER_CANDIDATE:
                return DataManager.getCandidates();
        }
        return null;
    }

    public List<String> getElementsByPage(int page) {
        switch(ResultPage.values()[page]) {
            case CANDIDATES_PER_PROFILE:
            case CANDIDATES_PER_ATTRIBUTE:
                return DataManager.getCandidates();
            case ATTRIBUTES_PER_PROFILE:
            case ATTRIBUTES_PER_CANDIDATE:
                return DataManager.getAttributes();
            case PROFILES_PER_CANDIDATE:
            case PROFILES_PER_ATTRIBUTE:
                return DataManager.getProfiles();
        }
        return null;
    }

    public double[][] getScoreByPage(int page) {
        if(page >= 0 && page < matrices.length) {
            return matrices[page];
        }
        return new double[0][];
    }

    public int[][] getOrderedIndexesByPage(int page) {
        if(page >= 0 && page < matrices.length) {
            return indexes[page];
        }
        return new int[0][];
    }

    public int[][] getScoreTierByPage(int page) {
        if(page >= 0 && page < matrices.length) {
            return tiers[page];
        }
        return new int[0][];
    }

    private double[][] transpose(double[][] matrix) {
        double[][] transposed = new double[matrix[0].length][matrix.length];
        for(int i = 0; i < matrix.length; i++) {
            for(int e = 0; e<matrix[i].length; e++) {
                transposed[e][i] = matrix[i][e];
            }
        }
        return transposed;
    }

    //  [i][0] = element id
    //  [i][1] = score
    //  [0][1] = max score
    // [-1][1] = min score
    private double[][] getOrderedResults(double[][] scores, int c) {
        int elements = scores.length;
        double[][] results = new double[elements][2];
        for (int i = 0; i < elements; i++) {
            results[i][0] = i;
            results[i][1] = scores[i][c]; //mData[i][criteria];
        }

        // Order Results (descendant)
        Arrays.sort(results, new Comparator<double[]>() {
            public int compare(double[] d1, double[] d2) {
                Double numOfKeys1 = d1[1];
                Double numOfKeys2 = d2[1];
                return numOfKeys2.compareTo(numOfKeys1);
            }
        });

        return results;
    }

}
