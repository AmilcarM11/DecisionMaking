package amilcarmenjivar.decisionmaking;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import amilcarmenjivar.decisionmaking.dialogs.DialogAddFragment;

public class SetupActivity extends ActionBarActivity implements DialogAddFragment.OnDialogResultListener {

    private int mCurrentPage = 0;

    private ViewPager mPager;
    private SetupPagerAdapter mPageAdapter;

    private static final String STATE_CURRENT_PAGE = "current_page";

    public static final int SETUP_ACTIVITY_ID = 21;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        // Toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Pager
        mPager = (ViewPager) findViewById(R.id.elements_viewPager);
        mPageAdapter = new SetupPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPageAdapter);
        mPager.setCurrentItem(mCurrentPage, true);
        mPager.setOnPageChangeListener(mPageAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            if(checkDataIntegrity()) {
                startMainActivity();
            }
            return true;

        } else if(id == R.id.action_add) {
            DialogAddFragment fragment = DialogAddFragment.newInstance(mCurrentPage, this);
            fragment.show(getSupportFragmentManager(), "DialogAddFragment");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(checkDataIntegrity()) {
            startMainActivity();
        }
    }

    @Override
    public void onDialogResult(String userInput, boolean accepted) {
        if(accepted && userInput != null && !userInput.equals("")) {
            switch (mCurrentPage) {
                case 0:
                    InfoCenter.addCandidate(userInput);
                    break;
                case 1:
                    InfoCenter.addAttribute(userInput);
                    break;
                case 2:
                    InfoCenter.addProfile(userInput);
                    break;
                case 3:
                default:
                    InfoCenter.addJudge(userInput);
            }
            mPageAdapter.refresh();
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    // Check if there is at least 1 judge, 1 profile, 2 attributes and 2 candidates.
    private boolean checkDataIntegrity() {
        boolean result = true;

        int judges = InfoCenter.getJudges().size();
        int profiles = InfoCenter.getProfiles().size();
        int attributes = InfoCenter.getAttributes().size();
        int candidates = InfoCenter.getCandidates().size();

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

        // Notify that we <potentially> modified data.
        if(result == true){
            InfoCenter.onDataModified();
        }

        return result;
    }

    private class SetupPagerAdapter extends FragmentStatePagerAdapter implements ViewPager.OnPageChangeListener {

        private ElementsFragment[] fragments;

        private final int[] titlesRes = { R.string.candidates, R.string.attributes, R.string.profiles, R.string.judges };

        public SetupPagerAdapter(FragmentManager fm) {
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
