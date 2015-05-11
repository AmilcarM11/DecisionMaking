package amilcarmenjivar.decisionmaking;


import android.content.Intent;
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
import android.widget.Toast;

import java.util.List;

import amilcarmenjivar.decisionmaking.dialogs.DialogFileNameFragment;
import amilcarmenjivar.decisionmaking.dialogs.DialogOpenFileFragment;


public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks, DialogOpenFileFragment.OnFileChosenListener, DialogFileNameFragment.OnDialogResultListener {

    private int mActiveSection = 0;

    private List<NavigationItem> mNavigationItems;

    private NavigationDrawerFragment mDrawerFragment;

    private Fragment mActiveFragment = null;

    private static final String ARG_ACTIVE_SECTION = "navigation_active_section";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Need to load information
        if(InfoCenter.isReloadNeeded()) {
            Log.wtf("DecisionMaker", "");
            if (FileIO.isTempFileFound(this)) {
                Log.wtf("DecisionMaker", "Loading data from temp file...");
                if(InfoCenter.reload(this)) {
                    Log.wtf("DecisionMaker", "Loading successful!");
                } else {
                    Log.wtf("DecisionMaker", "Loading failed. Using test values instead.");
                }
            } else {
                Log.wtf("DecisionMaker", "Temp file not found. Starting SetupActivity");
                Intent anIntent = new Intent(this, SetupActivity.class);
                //startActivityForResult(anIntent, SetupActivity.SETUP_ACTIVITY_ID);
                startActivity(anIntent);
                return;
            }
        } else {
            Log.wtf("DecisionMaker", "No need to reload, apparently!");
        }

        mNavigationItems = InfoCenter.getNavigationItems(this);
        setContentView(R.layout.activity_main);
        setupDrawer();
    }

    @Override
    protected void onStop() {
        Log.wtf("DecisionMaker", "Saving temp file");
        InfoCenter.save(this);
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


    // ----- Navigation Drawer ----- //

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
        transaction.commit();
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

            } else if( lastSelected >= 0 && lastSelected < navItems ) {
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

            replaceFragment(CompareFragment.newInstance(position, type, page));
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
        mNavigationItems = InfoCenter.getNavigationItems(this);
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

        if (id == R.id.action_edit) {
            Intent anIntent = new Intent(this, SetupActivity.class);
            //startActivityForResult(anIntent, SetupActivity.SETUP_ACTIVITY_ID);
            startActivity(anIntent);
            return true;

        } else if(id == R.id.action_open) {
            DialogOpenFileFragment fragment = DialogOpenFileFragment.newInstance(this);
            fragment.show(getSupportFragmentManager(), "DialogOpenFileFragment");
            return true;

        } else if (id == R.id.action_save) {
            // Can we save to external storage?
            if(FileIO.isExternalStorageWritable()) {
                DialogFileNameFragment fragment = DialogFileNameFragment.newInstance(this);
                fragment.show(getSupportFragmentManager(), "DialogFileNameFragment");
            } else {
                Toast.makeText(this, "External Storage unavailable", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(requestCode == SetupActivity.SETUP_ACTIVITY_ID){
//            refreshDrawer();
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }

    @Override
    public void onFileChosen(String fileName) {
        // Load file
        Toast.makeText(this, "Loading: "+fileName, Toast.LENGTH_SHORT).show();
        if(InfoCenter.importData(fileName)){

            // If loading was successful, refresh navigation drawer.
            refreshDrawer();
        } else {
            Toast.makeText(this, "Loading Failed!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveFileDialogResult(String userInput, boolean accepted) {
        if(accepted) {
            try{ // TODO: use string resources
                if(userInput == null || userInput.equals("")) {
                    Toast.makeText(this, "Enter a valid name.", Toast.LENGTH_SHORT).show();
                } else if(InfoCenter.exportData(userInput+".csv")) {
                    Toast.makeText(this, "File saved: " + userInput + ".csv", Toast.LENGTH_SHORT).show();
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
