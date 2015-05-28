package amilcarmenjivar.decisionmaking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import amilcarmenjivar.decisionmaking.data.DataManager;

/**
 *
 * Created by Amilcar on 09 May/15.
 */
public class NavDrawerAdapter extends ArrayAdapter<NavigationItem> {

    private boolean[] warnings;
    private LayoutInflater inflater;

    private static final int LAYOUT_SECTION = R.layout.item_drawer_section;
    private static final int LAYOUT_SECTION_TITLE = R.layout.item_drawer_section_title;
    private static final int LAYOUT_SECTION_CHILD = R.layout.item_drawer_item;

    private static final int[] layouts = { LAYOUT_SECTION, LAYOUT_SECTION_TITLE, LAYOUT_SECTION_CHILD };

    private static final int LAYOUT_TEXT_VIEW_ID = R.id.itemText;
    private static final int LAYOUT_ICON_VIEW_ID = R.id.itemIcon;

    private static final int TYPE_SECTION = 0;
    private static final int TYPE_SECTION_TITLE = 1;
    private static final int TYPE_SECTION_CHILD = 2;

    public NavDrawerAdapter(Context context, List<NavigationItem> objects) {
        super(context, LAYOUT_SECTION_CHILD, LAYOUT_TEXT_VIEW_ID, objects);
        warnings = new boolean[objects == null ? 0 : objects.size()];
        inflater = LayoutInflater.from(context);
        updateWarnings();
    }

    public void updateWarnings() {
        this.warnings = new boolean[getCount()];
        if(getShouldCheckWarnings() && DataManager.getIsInstanceLoaded()) {
            for(int i = 0; i < warnings.length; i++) {
                NavigationItem item = getItem(i);
                if(item.childID != -1) {
                    double consistency;
                    if(item.type == NavigationItem.Type.ATTRIBUTE) {
                        consistency = DataManager.getLoadedInstance().getAttributeConsistency(item.childID, -1);
                    } else {
                        consistency = DataManager.getLoadedInstance().getProfileConsistency(item.childID, -1);
                    }
                    warnings[i] = !DecisionAlgorithm.isConsistencyAcceptable(consistency);
                }
            }
        }
    }

    protected boolean getShouldCheckWarnings(){
        return true;
    }

    @Override
    public int getViewTypeCount() {
        return layouts.length;
    }

    @Override
    public int getItemViewType(int position) {
        NavigationItem item = this.getItem(position);
        if(item.type == NavigationItem.Type.OTHER) {
            return TYPE_SECTION;
        } else if(item.childID == -1) {
            return TYPE_SECTION_TITLE;
        } else {
            return TYPE_SECTION_CHILD;
        }
    }

    @Override
    public boolean isEnabled(int position) {
        return getItemViewType(position) != TYPE_SECTION_TITLE;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        int viewType = this.getItemViewType(position);
        NavigationItem item = this.getItem(position);

        if(convertView == null) {
            int layout = layouts[viewType];
            holder = new ViewHolder();
            convertView = inflater.inflate(layout, parent, false);

            // Find views
            holder.textView = (TextView) convertView.findViewById(LAYOUT_TEXT_VIEW_ID);
            if(viewType == TYPE_SECTION_CHILD) {
                holder.warningIcon = (ImageView) convertView.findViewById(R.id.warningBtn);
            }
            if(viewType == TYPE_SECTION_TITLE || viewType == TYPE_SECTION){
                holder.sectionDivider = convertView.findViewById(R.id.section_divider);
            }
            if(viewType == TYPE_SECTION) {
                holder.mainIcon = (ImageView) convertView.findViewById(LAYOUT_ICON_VIEW_ID);
            }

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Set Text
        holder.textView.setText( item.getText() );

        // Set the main icon
        if(viewType == TYPE_SECTION) {
            int icon = item.getIconRes();
            if(icon != -1 && holder.mainIcon != null) {
                holder.mainIcon.setImageResource(icon);
            }
        }

        // Set the warning icon visible when corresponding
        if(getShouldCheckWarnings() && viewType == TYPE_SECTION_CHILD && holder.warningIcon != null) {
            if(position < warnings.length) {
                holder.warningIcon.setVisibility( warnings[position] ? View.VISIBLE : View.INVISIBLE );
            }
        }

        return convertView;
    }

    private class ViewHolder {
        TextView textView = null;
        ImageView warningIcon = null;
        View sectionDivider = null;
        ImageView mainIcon = null;
    }

}
