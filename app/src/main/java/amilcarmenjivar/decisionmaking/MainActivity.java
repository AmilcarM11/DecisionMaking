package amilcarmenjivar.decisionmaking;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import amilcarmenjivar.decisionmaking.data.DataManager;
import amilcarmenjivar.decisionmaking.dialogs.DialogFileNameFragment;


public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks, DialogFileNameFragment.OnDialogResultListener {

    private int mActiveSection = 0;

    private List<NavigationItem> mNavigationItems;

    private NavigationDrawerFragment mDrawerFragment;

    private Fragment mActiveFragment = null;

    private boolean mForceCreateCompareFragment = false;

    private static final String KEY_INSTANCE_NAME = "active_instance_name";

    private static final String ARG_ACTIVE_SECTION = "navigation_active_section";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load stuff from temporal file
        checkFirstLoad();

        mNavigationItems = DataManager.getNavigationItems(this);
        setContentView(R.layout.activity_main);
        setupDrawer();
    }

    @Override
    protected void onStop() {
        Log.wtf("DecisionMaker", "Saving temp file");
        if(DataManager.getIsInstanceLoaded()) {
            DataManager.saveTempFile(this);
            // Save active instance name
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(KEY_INSTANCE_NAME, DataManager.getLoadedInstance().getInstanceName());
            editor.apply();
        }
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //outState.putInt(ARG_ACTIVE_SECTION, mActiveSection);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
//        mActiveSection = savedInstanceState.getInt(ARG_ACTIVE_SECTION, 0);
//        if(mDrawerFragment != null)
//            mDrawerFragment.selectItem(mActiveSection);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == EditActivity.SETUP_ACTIVITY_ID) {
            if(resultCode == RESULT_OK) {
                mForceCreateCompareFragment = true;
                refreshDrawer();
                mDrawerFragment.selectItem(1);
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void checkFirstLoad() {
        if(!DataManager.getIsInstanceLoaded()) {
            if(FileIO.isTempFileFound(this)) {
                Log.wtf("DecisionMaker", "Loading data from temp file...");
                if(DataManager.loadTempFile(this)) {
                    Log.wtf("DecisionMaker", "Loading successful!");
                    SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                    String instanceName = sharedPref.getString(KEY_INSTANCE_NAME, "Reloaded Instance");
                    DataManager.getLoadedInstance().setInstanceName(instanceName);
                } else {
                    Log.wtf("DecisionMaker", "Loading failed. Using test values instead.");

                }
            } else {
                Log.wtf("DecisionMaker", "Temp file not found. What to do?");
            }
        }
    }

    // ----- Navigation Drawer ----- //

    public void changeDrawerList(View v) {
        mDrawerFragment.alternateLayout();
    }

    public List<NavigationItem> getNavDrawerItems() {
        return mNavigationItems;
    }

    private void setupDrawer() {
        // Toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Set up the drawer.
        mDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mDrawerFragment.setUp(R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.container, fragment);
        transaction.commitAllowingStateLoss();
        mActiveFragment = fragment;
    }

    // TODO: Note: when changing the List<NavigationItem> system, take care with these methods:
    // onNavigationDrawerItemSelected & restoreActionBar

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        int lastSelected = mActiveSection;
        mActiveSection = position;
        int navItems = getNavDrawerItems().size();

        if( position < navItems ) {
            NavigationItem item = getNavDrawerItems().get(position);

            if(item.type == NavigationItem.Type.OTHER) {
                replaceFragment(ResultsFragment.newInstance());
                return;

            } else if( !mForceCreateCompareFragment && lastSelected >= 0 && lastSelected < navItems ) {
                NavigationItem oldItem = getNavDrawerItems().get(lastSelected);
                if(item.type == oldItem.type && mActiveFragment != null && mActiveFragment instanceof CompareFragment) {
                    // Just set the page.
                    ((CompareFragment) mActiveFragment).setPage(item.childID);
                    return;
                }
            }
            // Replace with other compare fragment
            int type = item.type == NavigationItem.Type.ATTRIBUTE ? 0 : 1;
            int page = item.childID > 0 ? item.childID : 0;
            mForceCreateCompareFragment = false;

            replaceFragment(CompareFragment.newInstance(position, type, page));
        }
    }

    @Override
    public void onAlternateItemSelected(int position, NavigationItem item) {
        if(item == null) {
            Toast.makeText(this, "I don't even...", Toast.LENGTH_SHORT).show();

        } else if(item.childID == -1) {
            if(position == 0) { // Edit
                Intent anIntent = new Intent(this, EditActivity.class);
                anIntent.putExtra(EditActivity.ARG_EDIT_MODE, true);
                startActivityForResult(anIntent, EditActivity.SETUP_ACTIVITY_ID);
            } else if(position == 1) { // Save
                if(FileIO.isExternalStorageWritable()) {
                    String instanceName = DataManager.getLoadedInstance().getInstanceName();
                    DialogFileNameFragment fragment = DialogFileNameFragment.newInstance(this, instanceName);
                    fragment.show(getSupportFragmentManager(), "DialogFileNameFragment");
                } else {
                    Toast.makeText(this, "External Storage unavailable", Toast.LENGTH_SHORT).show();
                }
            } else if(position == 2) { // New
                Intent anIntent = new Intent(this, EditActivity.class);
                anIntent.putExtra(EditActivity.ARG_EDIT_MODE, false);
                startActivityForResult(anIntent, EditActivity.SETUP_ACTIVITY_ID);
            }
        } else {
            String fileName = item.text;
            Toast.makeText(this, "Loading: "+fileName, Toast.LENGTH_SHORT).show();
            if(DataManager.importData(fileName)){
                // If loading was successful, refresh navigation drawer.
                refreshDrawer();
                mDrawerFragment.selectItem(-1);
            } else {
                Toast.makeText(this, "Loading Failed!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @SuppressWarnings("deprecation")
    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();

        String title;
        int navigationMode;
        if( mActiveSection < getNavDrawerItems().size() ) {
            NavigationItem item = getNavDrawerItems().get(mActiveSection);
            title = item.getSectionTitle();
            if(item.type != NavigationItem.Type.OTHER) {
                navigationMode = ActionBar.NAVIGATION_MODE_LIST;
            } else {
                navigationMode = ActionBar.NAVIGATION_MODE_STANDARD;
            }
        } else {
            title = "Title Undetermined";
            navigationMode = ActionBar.NAVIGATION_MODE_STANDARD;
        }
        actionBar.setTitle(title);
        actionBar.setNavigationMode(navigationMode);
        actionBar.setDisplayShowTitleEnabled(true);
    }

    private void refreshDrawer() {
        mNavigationItems = DataManager.getNavigationItems(this);
        mDrawerFragment.refreshDrawer();
    }

    public boolean isDrawerOpen() {
        return mDrawerFragment.isDrawerOpen();
    }

    // ----- User interactions ----- //

    @Override
    @SuppressWarnings("deprecation")
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mDrawerFragment.isDrawerOpen()) {
            //getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        } else {
            getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveFileDialogResult(String userInput, boolean accepted) {
        if(accepted) {
            try{ // TODO: use string resources
                if(userInput == null || userInput.equals("")) {
                    Toast.makeText(this, "Enter a valid name.", Toast.LENGTH_SHORT).show();
                } else if(DataManager.exportData(userInput.trim()+".csv")) {
                    Toast.makeText(this, "File saved: " + userInput.trim() + ".csv", Toast.LENGTH_SHORT).show();
                    mDrawerFragment.alternateLayout();
                } else {
                    Toast.makeText(this, "Saving Failed!", Toast.LENGTH_SHORT).show();
                }
            }catch(Exception e) {
                Log.wtf("DecisionMaker", "Problems Saving", e);
                Toast.makeText(this, "A problem occurred while saving.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
