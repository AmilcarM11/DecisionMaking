package amilcarmenjivar.decisionmaking.data;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import amilcarmenjivar.decisionmaking.FileIO;
import amilcarmenjivar.decisionmaking.NavigationItem;
import amilcarmenjivar.decisionmaking.R;

/**
 * Accesses the information of the current instance.
 * Also handles creating, loading and saving instances.
 *
 * Created by Amilcar Menjivar on 24/05/2015.
 */
public class DataManager {

    private static Instance instance = null;

    public static boolean getIsInstanceLoaded() {
        return instance != null;
    }

    public static void setLoadedInstance(Instance instance) {
        DataManager.instance = instance;
    }

    public static Instance getLoadedInstance() {
        if(!getIsInstanceLoaded()) {
            instance = Instance.createTestInstance();
        }
        return instance;
    }

    // ---------- Access/Modify loaded Instance ---------- //

    public static List<String> getCandidates() {
        return getLoadedInstance().getCandidates();
    }

    public static List<String> getAttributes() {
        return getLoadedInstance().getAttributes();
    }

    public static List<String> getProfiles(){
        return getLoadedInstance().getProfiles();
    }

    public static List<String> getJudges(){
        return getLoadedInstance().getJudges();
    }

    public static boolean addCandidate(String c) {
        return getLoadedInstance().addCandidate(c);
    }

    public static boolean addAttribute(String a) {
        return getLoadedInstance().addAttribute(a);
    }

    public static boolean addProfile(String p) {
        return getLoadedInstance().addProfile(p);
    }

    public static boolean addJudge(String j){
        return getLoadedInstance().addJudge(j);
    }

    public static boolean removeCandidate(int index) {
        return getLoadedInstance().removeCandidate(index);
    }

    public static boolean removeAttribute(int index) {
        return getLoadedInstance().removeAttribute(index);
    }

    public static boolean removeProfile(int index) {
        return getLoadedInstance().removeProfile(index);
    }

    public static boolean removeJudge(int index) {
        return getLoadedInstance().removeJudge(index);
    }

    public static void writeAttributesInfo(int attribute, int pair, int judge, int value) {
        getLoadedInstance().writeAttributesInfo(attribute, pair, judge, value);
    }

    public static void writeProfilesInfo(int profile, int pair, int judge, int value) {
        getLoadedInstance().writeProfilesInfo(profile, pair, judge, value);
    }

    public static void updateAttributeData() {
        getAttributeData().updateElements();
    }

    public static void updateProfileData() {
        getProfileData().updateElements();
    }

    public static Data getAttributeData() {
        return getLoadedInstance().getAttributeData();
    }

    public static Data getProfileData() {
        return getLoadedInstance().getProfileData();
    }

    // ---------- Load/Save Data ---------- //

    public static void saveTempFile(Context context) {
        File file = FileIO.getTempFile(context);
        FileIO.saveTempFile(file);
    }

    public static boolean loadTempFile(Context context) {
        File file = FileIO.getTempFile(context);
        try{
            instance = FileIO.importFromFile(file);
            return true;
        }catch(Exception e) {
            Log.wtf("MyApp", "Problems reloading. ", e);
            instance = Instance.createTestInstance();
            return false;
        }
    }

    // If this is true, refresh data.
    public static boolean importData(String fileName) {
        File directory = FileIO.getSaveDirectory();
        File file = new File(directory, fileName+".csv");
        Instance loadedInstance = FileIO.importFromFile(file);
        if(loadedInstance != null) {
            instance = loadedInstance;
            return true;
        }
        return false;
    }

    //
    public static boolean exportData(String fileName) {
        return FileIO.exportInstanceToFile(getLoadedInstance(), fileName);
    }


    // ---------- Navigation Drawer Items ---------- //

    public static List<NavigationItem> getNavigationItems(Context context) {
        List<NavigationItem> list = new ArrayList<NavigationItem>();
        List<String> attributes = getLoadedInstance().getAttributes();
        List<String> profiles = getLoadedInstance().getProfiles();

        Resources resources = context.getResources();

        // Add all attributes
        list.add(new NavigationItem(NavigationItem.Type.ATTRIBUTE, resources.getString(R.string.attributes)));
        for (int i = 0; i <attributes.size(); i++) {
            list.add(new NavigationItem(NavigationItem.Type.ATTRIBUTE, attributes.get(i), i));
        }

        // Add all profiles
        list.add(new NavigationItem(NavigationItem.Type.PROFILE, resources.getString(R.string.profiles)));
        for (int i = 0; i <profiles.size(); i++) {
            list.add(new NavigationItem(NavigationItem.Type.PROFILE, profiles.get(i), i));
        }

        // Add other sections
        // Results
        list.add(new NavigationItem(NavigationItem.Type.OTHER, resources.getString(R.string.results)));

        return list;
    }


}
