package amilcarmenjivar.decisionmaking.data;

import java.util.ArrayList;
import java.util.List;

import amilcarmenjivar.decisionmaking.DecisionAlgorithm;

import static amilcarmenjivar.decisionmaking.DecisionAlgorithm.translatePreference;

/**
 * Class used to keep the information of the current instance
 *
 * Created by Amilcar Menjivar on 24/05/2015.
 */
public class Instance {

    private static int instanceCount = 0;

    private String instanceName = "Instance #" + instanceCount++;

    private List<String> candidates;
    private List<String> attributes;
    private List<String> profiles;
    private List<String> judges;

    private Data attributeData;
    private Data profileData;

    private boolean validAttributeData = false;
    private boolean validProfileData = false;

    // ----- Temporal variables ----- //

    private double[][][] attributePreferenceMatrix = new double[0][][];
    private double[][][] profilePreferenceMatrix = new double[0][][];

    private double[][] attributeConsistencyMatrix = new double[0][];
    private double[][] profileConsistencyMatrix = new double[0][];

    private Result result = null;
    private ResultData resultData = null;

    // ----- Constructors ----- //

    protected Instance(List<String> candidates, List<String> attributes, List<String> profiles, List<String> judges, int[][][] attributeInfo, int[][][] profileInfo) {
        this(candidates, attributes, profiles, judges,
                new Data(attributes, candidates, judges, attributeInfo),
                new Data(profiles, attributes, judges, profileInfo));
    }

    protected Instance(List<String> candidates, List<String> attributes, List<String> profiles, List<String> judges, Data attributeData, Data profileData) {
        this.candidates = candidates;
        this.attributes = attributes;
        this.profiles = profiles;
        this.judges = judges;
        this.attributeData = attributeData;
        this.profileData = profileData;
    }

    // ----- Access Data ----- //

    public String getInstanceName() {
        return instanceName;
    }

    public List<String> getJudges() {
        return judges;
    }

    public List<String> getProfiles() {
        return profiles;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public List<String> getCandidates() {
        return candidates;
    }

    public Data getProfileData() {
        return profileData;
    }

    public Data getAttributeData() {
        return attributeData;
    }

    // ----- Modify data ----- //

    public void setInstanceName(String name) {
        if(name != null) {
            this.instanceName = name;
        }
    }

    public boolean addCandidate(String c) {
        c = c.trim();
        if(!getCandidates().contains(c)){
            getCandidates().add(0, c);
            getAttributeData().updateElements();
            invalidateAttributeData();
            return true;
        }
        return false;
    }

    public boolean addAttribute(String a) {
        a = a.trim();
        if(!getAttributes().contains(a)) {
            getAttributes().add(0, a);
            getAttributeData().addCriteria(a);
            getProfileData().updateElements();
            invalidateAll();
            return true;
        }
        return false;
    }

    public boolean addProfile(String p) {
        p = p.trim();
        if(!profiles.contains(p)) {
            profiles.add(0, p);
            profileData.addCriteria(p);
            invalidateProfileData();
            return true;
        }
        return false;
    }

    public boolean addJudge(String j){
        j = j.trim();
        if(!getJudges().contains(j)) {
            getJudges().add(0, j);
            getAttributeData().addJudge(j);
            getProfileData().addJudge(j);
            invalidateAll();
            return true;
        }
        return false;
    }

    public boolean removeCandidate(int index) {
        String candidate = getCandidates().remove(index);
        if(candidate != null) {
            getAttributeData().updateElements();
            invalidateAttributeData();
            return true;
        }
        return false;
    }

    public boolean removeAttribute(int index) {
        String attribute = getAttributes().remove(index);
        if(attribute != null) {
            getAttributeData().removeCriteria(attribute);
            getProfileData().updateElements();
            invalidateAll();
            return true;
        }
        return false;
    }

    public boolean removeProfile(int index) {
        String profile = getProfiles().remove(index);
        if(profile != null) {
            getProfileData().removeCriteria(profile);
            invalidateProfileData();
            return true;
        }
        return false;
    }

    public boolean removeJudge(int index) {
        String judge = getJudges().remove(index);
        if(judge != null) {
            getAttributeData().removeJudge(judge);
            getProfileData().removeJudge(judge);
            invalidateAll();
            return true;
        }
        return false;
    }

    public void writeAttributesInfo(int attribute, int pair, int judge, int value) {
        getAttributeData().write(attribute, pair, judge, value);
        invalidateAttributeData();
    }

    public void writeProfilesInfo(int profile, int pair, int judge, int value) {
        getProfileData().write(profile, pair, judge, value);
        invalidateProfileData();
    }

    // ----- Calculations ----- //


    // judge = -1 equals all judges
    public double[] getAttributePreferenceVector(int attribute, int judge) {
        checkValidity();
        int j = judge < 0 ? getJudges().size()-1 : judge;
        double[] vector = attributePreferenceMatrix[attribute][j];
        if(vector == null || vector.length == 0) {
            Data data = getAttributeData();
            vector = getPreferenceVector(data.getRawData(), attribute, judge);
            attributePreferenceMatrix[attribute][j] = vector;
        }
        return vector;
    }

    public double[] getProfilePreferenceVector(int profile, int judge) {
        checkValidity();
        int j = judge < 0 ? getJudges().size()-1 : judge;
        double[] vector = profilePreferenceMatrix[profile][j];
        if(vector == null || vector.length == 0) {
            Data data = getProfileData();
            vector = getPreferenceVector(data.getRawData(), profile, judge);
            profilePreferenceMatrix[profile][j] = vector;
        }
        return vector;
    }

    public double getAttributeConsistency(int attribute, int judge) {
        checkValidity();
        int j = judge < 0 ? getJudges().size()-1 : judge;
        double consistency = attributeConsistencyMatrix[attribute][j];
        if(consistency == -1) {
            int n = getCandidates().size();
            double[] consistencyVector = getAttributeConsistencyVector(attribute, judge);
            consistency = DecisionAlgorithm.calculateConsistency(n, consistencyVector);
            attributeConsistencyMatrix[attribute][j] = consistency;
        }
        return consistency;
    }

    public double getProfileConsistency(int profile, int judge) {
        checkValidity();
        int j = judge < 0 ? getJudges().size()-1 : judge;
        double consistency = profileConsistencyMatrix[profile][j];
        if(consistency == -1) {
            int n = getAttributes().size();
            double[] consistencyVector = getProfileConsistencyVector(profile, judge);
            consistency = DecisionAlgorithm.calculateConsistency(n, consistencyVector);
            profileConsistencyMatrix[profile][j] = consistency;
        }
        return consistency;
    }

    public double[] getAttributeConsistencyVector(int attribute, int judge) {
        checkValidity();
        int n = getCandidates().size();
        double[] preferenceVector = getAttributePreferenceVector(attribute, judge);
        return DecisionAlgorithm.getPreferenceConsistencyVector(n, preferenceVector);
    }

    public double[] getProfileConsistencyVector(int profile, int judge) {
        checkValidity();
        int n = getAttributes().size();
        double[] preferenceVector = getProfilePreferenceVector(profile, judge);
        return DecisionAlgorithm.getPreferenceConsistencyVector(n, preferenceVector);
    }

    private double[] getPreferenceVector(int[][][] rawData, int criteria, int judge) {
        int judges = getJudges().size();
        double[] vector = new double[rawData[criteria].length];

        if(judge == -1) { // Include all judges' preferences
            for(int i = 0; i<vector.length; i++) {
                double score = 1.0;
                for(int j = 0; j < judges; j++) {
                    score *= translatePreference(rawData[criteria][i][j]);
                }
                vector[i] = Math.pow(score, 1.0/judges);
            }
        } else { // Use per-judge preference
            for(int i = 0; i<vector.length; i++) {
                vector[i] = translatePreference(rawData[criteria][i][judge]);
            }
        }
        return vector;
    }

    public Result getResult() {
        checkValidity();

        if(result == null) {
            int candidates = getCandidates().size();
            int attributes = getAttributes().size();
            int profiles = getProfiles().size();
            int judges = getJudges().size();

            int[][][] attributesData = getAttributeData().getRawData();
            int[][][] profilesData = getProfileData().getRawData();

            result = DecisionAlgorithm.getResults(candidates, attributes, profiles, judges, attributesData, profilesData);
        }
        return result;
    }

    public Result getResultByJudge(int judge) {
        if(judge < 0 && judge > getJudges().size()) {
            return null;
        }
        int candidates = getCandidates().size();
        int attributes = getAttributes().size();
        int profiles = getProfiles().size();

        int[][][] attributesData = getAttributeData().getRawData();
        int[][][] profilesData = getProfileData().getRawData();

        attributesData = justOneJudge(attributesData, attributes, candidates, judge);
        profilesData = justOneJudge(profilesData, profiles, attributes, judge);

        return DecisionAlgorithm.getResults(candidates, attributes, profiles, 1, attributesData, profilesData);
    }

    public ResultData getResultData() {
        checkValidity();

        if(resultData == null) {
            Result result = getResult();
            resultData = new ResultData(result);
        }
        return resultData;
    }

    private int[][][] justOneJudge(int[][][] rawData, int criteria, int elements, int judge) {
        int pairs = (elements * (elements - 1)) / 2;
        int[][][] foo = new int[criteria][pairs][1];

        for(int c = 0; c < criteria; c++) {
            for (int p = 0; p < pairs; p++) {
                foo[c][p][0] = rawData[c][p][judge];
            }
        }
        return foo;
    }


    // ----- Validations ----- //

    public void invalidateAll() {
        invalidateAttributeData();
        invalidateProfileData();
    }

    public void invalidateAttributeData() {
        this.validAttributeData = false;
    }

    public void invalidateProfileData() {
        this.validProfileData = false;
    }

    protected void checkValidity(){
        if(!this.validAttributeData || !this.validProfileData) {
            revalidate();
        }
    }

    protected void revalidate() {
        int attributes = getAttributes().size();
        int profiles = getProfiles().size();
        int judges = getJudges().size();

        if(!validAttributeData) {
            attributePreferenceMatrix = new double[attributes][judges+1][0];

            attributeConsistencyMatrix = new double[attributes][judges+1];
            for(int a=0; a<attributes; a++) {
                for(int j=0; j<judges+1; j++) {
                    attributeConsistencyMatrix[a][j] = -1; // -1 means consistency not calculated.
                }
            }

            this.validAttributeData = true;
        }

        if(!validProfileData) {
            profilePreferenceMatrix = new double[profiles][judges+1][0];

            profileConsistencyMatrix = new double[profiles][judges+1];
            for(int p=0; p<profiles; p++) {
                for(int j=0; j<judges+1; j++) {
                    profileConsistencyMatrix[p][j] = -1; // -1 means consistency not calculated.
                }
            }

            this.validProfileData = true;
        }

        this.result = null;
        this.resultData = null;
    }


    public Instance copy() {
        List<String> c = new ArrayList<String>(getCandidates()),
                a = new ArrayList<String>(getAttributes()),
                p = new ArrayList<String>(getProfiles()),
                j = new ArrayList<String>(getJudges());

        int[][][] attData = getAttributeData().getRawData();
        int[][][] profData = getProfileData().getRawData();

        Instance copy = newInstance(c, a, p, j, attData, profData);
        copy.setInstanceName(this.getInstanceName());
        return copy;
    }

    /**
     * Creates a new Instance with all the data provided.
     *
     * If the parameters do not contain valid/sufficient information, this will return null.
     */
    public static Instance newInstance(List<String> candidates, List<String> attributes, List<String> profiles, List<String> judges, int[][][] attributeInfo, int[][][] profileInfo){
        if(candidates != null && candidates.size() > 1) {
            if(attributes != null && attributes.size() > 1) {
                if(profiles != null && profiles.size() > 0) {
                    if(judges != null && judges.size() > 0) {
                        return new Instance(candidates, attributes, profiles, judges, attributeInfo, profileInfo);
                    }
                }
            }
        }
        return null;
    }

    public static Instance createTestInstance() {
        Instance instance = createEmptyInstance();

        instance.addCandidate("Candidato 1");
        instance.addCandidate("Candidato 2");
        instance.addCandidate("Candidato 3");
        instance.addAttribute("Atributo 1");
        instance.addAttribute("Atributo 2");
        instance.addAttribute("Atributo 3");
        instance.addProfile("Perfil 1");
        instance.addJudge("Juez 1");

        return instance;
    }

    public static Instance createEmptyInstance() {
        List<String> candidates = new ArrayList<String>();
        List<String> attributes = new ArrayList<String>();
        List<String> profiles = new ArrayList<String>();
        List<String> judges = new ArrayList<String>();

        Data attributeData = Data.emptyData(attributes, candidates, judges);
        Data profileData = Data.emptyData(profiles, attributes, judges);

        return new Instance(candidates, attributes, profiles, judges, attributeData, profileData);
    }

}
