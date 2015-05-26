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

    // TODO: also accept stringResId

    public NavigationItem(Type type, String text) {
        this(type, text, -1);
        type.sectionTitle = text;
    }

    public NavigationItem(Type type, String text, int childID) {
        this.type = type;
        this.text = text;
        this.childID = childID;
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

    public enum Type {
        ATTRIBUTE, PROFILE, OTHER;

        String sectionTitle = "";
    }

}
