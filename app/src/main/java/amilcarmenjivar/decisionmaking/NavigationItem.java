package amilcarmenjivar.decisionmaking;

/**
 * Used to describe each item on the Navigation Drawer
 *
 * Created by Amilcar Menjivar on 06/05/2015.
 */
public class NavigationItem {

    public final Type type;
    public final String text;
    public final int childID;
    public final int iconRes;

    protected NavigationItem(Type type, String text, int childID, int iconRes) {
        this.type = type;
        this.text = text;
        this.childID = childID;
        this.iconRes = iconRes;
    }

    public static NavigationItem newItem(String text, int iconRes) {
        return new NavigationItem(Type.OTHER, text, -1, iconRes);
    }

    public static NavigationItem newSection(Type type, String title) {
        type.sectionTitle = title;
        return new NavigationItem(type, title, -1, -1);
    }

    public static NavigationItem newSectionItem(Type type, String text, int childID) {
        return new NavigationItem(type, text, childID, -1);
    }

    public String getText() {
        if(type == Type.OTHER) {
            return text;
        } else if(childID == -1) {
            return type.sectionTitle;
        } else {
            return text;
        }
    }

    public String getSectionTitle() {
        if(type == Type.OTHER) {
            return text;
        } else {
            return type.sectionTitle;
        }
    }

    public int getIconRes() {
        return iconRes;
    }

    public enum Type {
        ATTRIBUTE, PROFILE, INSTANCE, OTHER;

        String sectionTitle = "";
    }

}
