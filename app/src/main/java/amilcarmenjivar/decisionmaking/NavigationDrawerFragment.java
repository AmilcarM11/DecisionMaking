package amilcarmenjivar.decisionmaking;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import amilcarmenjivar.decisionmaking.data.DataManager;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;

    private ListView mDrawerListView;
    private ListView mInstanceListView;
    private InstanceListAdapter mInstanceListAdapter;

    private TextView mInstanceTextView;
    private TextView mCandidatesTextView;
    private TextView mAttributesTextView;
    private TextView mProfilesTextView;
    private TextView mJudgesTextView;
    private ImageView mDrawerIndicatorIcon;

    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mAlternativeLayoutShown = false;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    public NavigationDrawerFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        // Header views
        mInstanceTextView = (TextView) root.findViewById(R.id.instanceNameText);
        mCandidatesTextView = (TextView) root.findViewById(R.id.candidatesText);
        mAttributesTextView = (TextView) root.findViewById(R.id.attributesText);
        mProfilesTextView = (TextView) root.findViewById(R.id.profilesText);
        mJudgesTextView = (TextView) root.findViewById(R.id.judgesText);
        mDrawerIndicatorIcon = (ImageView) root.findViewById(R.id.drawerLayoutIndicator);

        mDrawerListView = (ListView) root.findViewById(R.id.drawer_listView);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        mInstanceListView = (ListView) root.findViewById(R.id.alternative_listView);
        mInstanceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mCallbacks != null) {
                    NavigationItem item = (NavigationItem) mInstanceListView.getAdapter().getItem(position);
                    mCallbacks.onAlternateItemSelected(position, item);
                }
            }
        });

        return root;
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        Log.wtf("DecisionMaker", "Setting-up Navigation Drawer");

        mInstanceListAdapter = new InstanceListAdapter(getActivity());
        mInstanceListView.setAdapter(mInstanceListAdapter);

        mDrawerListView.setAdapter(new NavDrawerAdapter(getActivity(), getMyActivity().getNavDrawerItems()));
        // Select position
        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        if(mDrawerLayout != null) {
            // set a custom shadow that overlays the main content when the drawer opens
            mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

            mDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                    if (!isAdded()) {
                        return;
                    }
                    getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    if (!isAdded()) {
                        return;
                    }
                    if (!mUserLearnedDrawer) {
                        // The user manually opened the drawer; store this flag to prevent auto-showing
                        // the navigation drawer automatically in the future.
                        mUserLearnedDrawer = true;
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                    }
                    getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
                }
            };

            // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
            // per the navigation drawer design guidelines.
//        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
//            mDrawerLayout.openDrawer(mFragmentContainerView);
//        }
            if (!mFromSavedInstanceState) {
                mDrawerLayout.openDrawer(mFragmentContainerView);
            }
            mDrawerLayout.setDrawerListener(mDrawerToggle);

            // Defer code dependent on restoration of previous instance state.
            mDrawerLayout.post(new Runnable() {
                @Override
                public void run() {
                    mDrawerToggle.syncState();
                }
            });
        }

        updateHeader();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.menu_open_drawer, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mDrawerToggle != null) {
            return mDrawerToggle.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    public void selectItem(int position) {
        // Shortcut for opening the results section
        if(position == -1) {
            position = getMyActivity().getNavDrawerItems().size() - 1;
        }

        Log.wtf("DecisionMaker", "Selected Drawer Item: "+position);
        mCurrentSelectedPosition = position;

        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    private static void replaceViews(final View collapseView, final View expandView) {
        collapseView.setVisibility(View.GONE);
        expandView.setVisibility(View.VISIBLE);
    }

    public void alternateLayout() {
        if(mAlternativeLayoutShown) {
            replaceViews(mInstanceListView, mDrawerListView);
            mDrawerIndicatorIcon.setImageResource(R.drawable.ic_keyboard_arrow_down_white_18dp);
        } else {
            replaceViews(mDrawerListView, mInstanceListView);
            mInstanceListAdapter.update();
            mDrawerIndicatorIcon.setImageResource(R.drawable.ic_keyboard_arrow_up_white_18dp);
        }

        mAlternativeLayoutShown = !mAlternativeLayoutShown;
    }

    private void updateHeader() {
        String candidates = "-";
        String attributes = "-";
        String profiles = "-";
        String judges = "-";
        String instanceName = "";
        if(DataManager.getIsInstanceLoaded()) {
            candidates = DataManager.getCandidates().size() + "";
            attributes = DataManager.getAttributes().size() + "";
            profiles = DataManager.getProfiles().size() + "";
            judges = DataManager.getJudges().size() + "";
            instanceName = DataManager.getLoadedInstance().getInstanceName();
        }
        mInstanceTextView.setText(instanceName);
        mCandidatesTextView.setText(candidates);
        mAttributesTextView.setText(attributes);
        mProfilesTextView.setText(profiles);
        mJudgesTextView.setText(judges);
    }

    public void refreshDrawer() {
        List<NavigationItem> navItems = getMyActivity().getNavDrawerItems();

        updateHeader();

        // Re-setting the adapter causes a full redraw of the drawer.
        mDrawerListView.setAdapter(new NavDrawerAdapter(getActivity(), navItems));

        // Set the drawer back to regular view
        if(mAlternativeLayoutShown) {
            alternateLayout();
        }

        if(mDrawerLayout != null) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    private MainActivity getMyActivity() {
        return (MainActivity) getActivity();
    }

    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);

        void onAlternateItemSelected(int position, NavigationItem item);

    }

    private class InstanceListAdapter extends NavDrawerAdapter {

        public InstanceListAdapter(Context context) {
            super(context, new ArrayList<NavigationItem>());
            update();
        }

        public void update() {
            new PopulateItemsTask().execute();
        }

        @Override
        public boolean getShouldCheckWarnings() {
            return false;
        }


        private String getString(int stringRes) {
            return getContext().getString(stringRes);
        }

        private class PopulateItemsTask extends AsyncTask<Void, Void, List<String>> {

            @Override
            protected void onPostExecute(List<String> items) {
                int i = 0;
                for(String item : items) {
                    if(item != null && !item.equals("")) {
                        add(NavigationItem.newSectionItem(NavigationItem.Type.INSTANCE, item, i++));
                    }
                }
            }

            @Override
            protected void onPreExecute() {
                clear();
                // Edit
                add(NavigationItem.newItem(getString(R.string.action_edit), R.drawable.ic_create_grey600_24dp));
                // Save
                add(NavigationItem.newItem(getString(R.string.action_save), R.drawable.ic_save_grey600_24dp));
                // New
                add(NavigationItem.newItem(getString(R.string.new_instance), R.drawable.ic_add_circle_outline_grey600_24dp));

                // title: Load Instance
                add(NavigationItem.newSection(NavigationItem.Type.INSTANCE, getString(R.string.load_instance)));
            }

            @Override
            protected List<String> doInBackground(Void... params) {
                // everything in here gets executed in a separate thread
                return FileIO.listFiles();
            }

        }

    }

}
