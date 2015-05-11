package amilcarmenjivar.decisionmaking;

import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;
import android.util.Log;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Amilcar Menjivar on 28/04/2015.
 */
public class InfoCenter { // TODO: order

    private List<String> candidates;
    private List<String> attributes;
    private List<String> profiles;
    private List<String> judges;

    private Data attributeData;
    private Data profileData;

    private boolean loaded = false;

    private InfoCenter() {
        candidates = new ArrayList<String>();
        attributes = new ArrayList<String>();
        profiles = new ArrayList<String>();
        judges = new ArrayList<String>();

        attributeData = Data.emptyData(attributes, candidates, judges);
        profileData = Data.emptyData(profiles, attributes, judges);
    }

    private static InfoCenter instance;
    public static InfoCenter instance() {
        if(instance == null) {
            instance = new InfoCenter();
        }
        return instance;
    }

    public static List<NavigationItem> getNavigationItems(Context context) {
        List<NavigationItem> list = new ArrayList<NavigationItem>();
        List<String> attributes = instance().attributes;
        List<String> profiles = instance().profiles;

        // Add all attributes
        list.add(new NavigationItem(NavigationItem.Type.ATTRIBUTE, getString(context.getResources(), R.string.attributes)));
        for (int i = 0; i <attributes.size(); i++) {
            list.add(new NavigationItem(NavigationItem.Type.ATTRIBUTE, attributes.get(i), i));
        }

        // Add all profiles
        list.add(new NavigationItem(NavigationItem.Type.PROFILE, getString(context.getResources(), R.string.profiles)));
        for (int i = 0; i <profiles.size(); i++) {
            list.add(new NavigationItem(NavigationItem.Type.PROFILE, profiles.get(i), i));
        }

        // Add other sections
            // Results
        list.add(new NavigationItem(NavigationItem.Type.OTHER, getString(context.getResources(), R.string.results)));

        return list;
    }

    private static String getString(Resources resources, int resID) {
        return resources.getString(resID);
    }

    public static Data getAttributeData() {
        return instance().attributeData;
    }

    public static Data getProfileData() {
        return instance().profileData;
    }

    public static int[][][] getAttributesInfo() {
        return getAttributeData().getRawData();
    }

    public static int[][][] getProfilesInfo() {
        return getProfileData().getRawData();
    }

    public static void writeAttributesInfo(int attribute, int pair, int judge, int value) {
        instance().attributeData.write(attribute, pair, judge, value);
    }

    public static void writePreferencesInfo(int profile, int pair, int judge, int value) {
        instance().profileData.write(profile, pair, judge, value);
    }

    public static boolean addCandidate(String c) {
        c = c.trim();
        if(!instance().candidates.contains(c)){
            instance.candidates.add(0, c);
            instance.attributeData.updateElements();
            return true;
        }
        return false;
    }

    public static boolean addAttribute(String a) {
        a = a.trim();
        if(!instance().attributes.contains(a)) {
            instance.attributes.add(0, a);
            instance.attributeData.addCriteria(a);
            instance.profileData.updateElements();
            return true;
        }
        return false;
    }

    public static boolean addProfile(String p) {
        p = p.trim();
        if(!instance().profiles.contains(p)) {
            instance.profiles.add(0, p);
            instance.profileData.addCriteria(p);
            return true;
        }
        return false;
    }

    public static boolean addJudge(String j){
        j = j.trim();
        if(!instance().judges.contains(j)) {
            instance().judges.add(0, j);
            instance().attributeData.addJudge(j);
            instance().profileData.addJudge(j);
            return true;
        }
        return false;
    }

    public static boolean removeCandidate(int index) {
        String candidate = instance().candidates.remove(index);
        if(candidate != null) {
            instance.attributeData.updateElements();
            return true;
        }
        return false;
    }

    public static boolean removeAttribute(int index) {
        String attribute = instance().attributes.remove(index);
        if(attribute != null) {
            instance.attributeData.removeCriteria(attribute);
            instance.profileData.updateElements();
            return true;
        }
        return false;
    }

    public static boolean removeProfile(int index) {
        String profile = instance().profiles.remove(index);
        if(profile != null) {
            instance.profileData.removeCriteria(profile);
            return true;
        }
        return false;
    }

    public static boolean removeJudge(int index) {
        String judge = instance().judges.remove(index);
        if(judge != null) {
            instance.attributeData.removeJudge(judge);
            instance.profileData.removeJudge(judge);
            return true;
        }
        return false;
    }

    public static List<String> getCandidates() {
        return instance().candidates;
    }

    public static List<String> getAttributes() {
        return instance().attributes;
    }

    public static List<String> getProfiles() {
        return instance().profiles;
    }

    public static List<String> getJudges() {
        return instance().judges;
    }

    private void useTestValues() {
        candidates.clear();
        candidates.add("Candidato 1");
        candidates.add("Candidato 2");
        candidates.add("Candidato 3");
        attributes.clear();
        attributes.add("Atributo 1");
        attributes.add("Atributo 2");
        attributes.add("Atributo 3");
        profiles.clear();
        profiles.add("Perfil 1");
        judges.clear();
        judges.add("Juez 1");

        attributeData = Data.emptyData(attributes, candidates, judges);
        profileData = Data.emptyData(profiles, attributes, judges);
    }

    public static boolean isReloadNeeded() {
        return !instance().loaded;
    }

    public static boolean reload(Context context) {
        File file = new File(context.getFilesDir(), FileIO.TEMP_FILE_NAME);
        RawData data = null;
        try{
            data = FileIO.importFromFile(file);
        }catch(Exception e) {
            Log.wtf("MyApp", "Problems reloading. ", e);
        }
        instance().loaded = true;
        if( data == null ) {
            instance().useTestValues();
            return false;
        } else {
            load(data);
            return true;
        }
    }

    public static void onDataModified() {
        instance().loaded = true;
    }

    public static void save(Context context) {
        File file = new File(context.getFilesDir(), FileIO.TEMP_FILE_NAME);
        FileIO.saveTempFile(file);
    }

    public static boolean importData(String fileName) {
        String root = Environment.getExternalStorageDirectory().toString();
        File directory = new File(root, "DecisionMaking");
        File file = new File(directory, fileName+".csv");
        RawData data = FileIO.importFromFile(file);
        if(data != null) {
            load(data);
            return true;
        }
        return false;
    }

    public static boolean exportData(String fileName) {
        // TODO: surround with try-catch
        return FileIO.exportResultToFile(DecisionAlgorithm.getResults(), fileName);
    }

    private static void load(RawData data) {
        instance();
        instance.candidates = data.c;
        instance.attributes = data.a;
        instance.profiles = data.p;
        instance.judges = data.j;

        instance.attributeData = new Data(instance.attributes, instance.candidates, instance.judges, data.attributeInfo);
        instance.profileData = new Data(instance.profiles, instance.attributes, instance.judges, data.profileInfo);
    }

    static class RawData {

        private final List<String> c;
        private final List<String> a;
        private final List<String> p;
        private final List<String> j;
        private final int[][][] attributeInfo;
        private final int[][][] profileInfo;

        RawData(List<String> c, List<String> a, List<String> p, List<String> j, int[][][] attributeInfo, int[][][] profileInfo) {
            this.c = c;
            this.a = a;
            this.p = p;
            this.j = j;
            this.attributeInfo = attributeInfo;
            this.profileInfo = profileInfo;
        }
    }

}
