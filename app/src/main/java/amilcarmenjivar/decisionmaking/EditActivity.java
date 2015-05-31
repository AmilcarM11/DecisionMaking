package amilcarmenjivar.decisionmaking;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import amilcarmenjivar.decisionmaking.data.DataManager;
import amilcarmenjivar.decisionmaking.data.Instance;
import amilcarmenjivar.decisionmaking.dialogs.DialogAddFragment;

public class EditActivity extends ActionBarActivity implements DialogAddFragment.OnDialogResultListener {

    private Instance mInstance;

    private int mCurrentPage = 0;

    private EditPagerAdapter mPageAdapter;

    public static final int SETUP_ACTIVITY_ID = 21;

    public static final String ARG_EDIT_MODE = "edit_mode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Determine what instance to deal with
        boolean editMode = getIntent().getBooleanExtra(ARG_EDIT_MODE, false);
        if(DataManager.getIsInstanceLoaded() && editMode) {
            mInstance = DataManager.getLoadedInstance().copy();
        } else {
            mInstance = Instance.createEmptyInstance();
        }

        setContentView(R.layout.activity_edit);

        // Toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Pager
        ViewPager mPager = (ViewPager) findViewById(R.id.elements_viewPager);
        mPageAdapter = new EditPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPageAdapter);
        mPager.setCurrentItem(mCurrentPage, true);
        mPager.setOnPageChangeListener(mPageAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setup, menu);
        // TODO: Make sure Home button is removed.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_add) {
            DialogAddFragment fragment = DialogAddFragment.newInstance(mCurrentPage, this);
            fragment.show(getSupportFragmentManager(), "DialogAddFragment");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }

    @Override
    public void onDialogResult(String userInput, boolean accepted) {
        if(accepted && userInput != null && !userInput.equals("")) {
            switch (mCurrentPage) {
                case 0:
                    mInstance.addCandidate(userInput);
                    break;
                case 1:
                    mInstance.addAttribute(userInput);
                    break;
                case 2:
                    mInstance.addProfile(userInput);
                    break;
                case 3:
                default:
                    mInstance.addJudge(userInput);
            }
            mPageAdapter.refresh();
        }
    }

    public void onSaveButtonPressed(View v) {
        if(checkDataIntegrity()) {
            DataManager.setLoadedInstance(mInstance);
            setResult(RESULT_OK);
            finish();
        }
    }

    public void onDiscardButtonPressed(View v) {
        setResult(RESULT_CANCELED);
        finish();
    }

    public Instance getEditingInstance() {
        return mInstance;
    }

    // Check if there is at least 1 judge, 1 profile, 2 attributes and 2 candidates.
    private boolean checkDataIntegrity() {
        boolean result = true;

        int judges = mInstance.getJudges().size();
        int profiles = mInstance.getProfiles().size();
        int attributes = mInstance.getAttributes().size();
        int candidates = mInstance.getCandidates().size();

        if(candidates <2) {
            result = false;
            String message = String.format(getString(R.string.insufficient_elements),
                    getString(R.string.candidates));
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        } else if(attributes <2) {
            result = false;
            String message = String.format(getString(R.string.insufficient_elements),
                    getString(R.string.attributes));
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        } else if(profiles <1) {
            result = false;
            String message = String.format(getString(R.string.insufficient_elements),
                    getString(R.string.profiles));
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        } else if(judges <1) {
            result = false;
            String message = String.format(getString(R.string.insufficient_elements),
                    getString(R.string.judges));
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }

        return result;
    }

    private class EditPagerAdapter extends FragmentStatePagerAdapter implements ViewPager.OnPageChangeListener {

        private ElementsFragment[] fragments;

        private final int[] titlesRes = { R.string.candidates, R.string.attributes, R.string.profiles, R.string.judges };

        public EditPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = new ElementsFragment[titlesRes.length];
        }

        @Override
        public Fragment getItem(int i) {
            if(fragments[i] == null) {
                fragments[i] = ElementsFragment.newInstance(i);
            }
            return fragments[i];
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getString(titlesRes[position]);
        }

        @Override
        public void onPageScrolled(int i, float v, int i2) { }

        @Override
        public void onPageSelected(int i) {
            mCurrentPage = i;
        }

        @Override
        public void onPageScrollStateChanged(int i) { }

        @Override
        public int getCount() {
            return titlesRes.length;
        }

        public void refresh() {
            fragments[mCurrentPage].refresh();
        }
    }
}
