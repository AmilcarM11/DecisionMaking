package amilcarmenjivar.decisionmaking;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.List;

/**
 *
 * Created by Amilcar Menjivar on 28/04/2015.
 */
public class CompareFragment extends Fragment implements ActionBar.OnNavigationListener {

    private static final String ARG_PAGE_INDEX = "navigation_page_index";
    private static final String ARG_ELEMENTS = "selected_elements";
    private static final String ARG_CURRENT_PAGE = "selected_page";
    private static final String ARG_SELECTED_JUDGE = "selected_judge";
    private static final String STATE_CONSISTENT_CRITERIA = "consistent_data";

    private int mElements = 0;
    private int mCurrentPage = 0;
    private int mSelectedJudge = 0;

    private ViewPager mPager;
    private PagerTabStrip mPagerTabStrip;
    private ComparingPagerAdapter mPageAdapter;

    private boolean[] inconsistentCriteria = new boolean[0];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mElements = getArguments().getInt(ARG_ELEMENTS, 0);
        mCurrentPage = getArguments().getInt(ARG_CURRENT_PAGE, 0);
        inconsistentCriteria = new boolean[elements().size()];
        if(savedInstanceState != null ) {
            mCurrentPage = savedInstanceState.getInt(ARG_CURRENT_PAGE, 0);
            mSelectedJudge = savedInstanceState.getInt(ARG_SELECTED_JUDGE, 0);
            inconsistentCriteria = savedInstanceState.getBooleanArray(STATE_CONSISTENT_CRITERIA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_compare, container, false);

        // Pager for comparison's criteria
        mPager = (ViewPager) rootView.findViewById(R.id.compare_viewPager);
        mPageAdapter = new ComparingPagerAdapter(getMyActivity().getSupportFragmentManager());
        mPager.setAdapter(mPageAdapter);
        mPager.setOnPageChangeListener(mPageAdapter);

        // PagerTabStrip
        mPagerTabStrip = (PagerTabStrip) rootView.findViewById(R.id.tabStrip);
        mPagerTabStrip.setDrawFullUnderline(true);

        // Scroll to page
        if(mCurrentPage != 0) {
            mPager.setCurrentItem(mCurrentPage, true);
        }

        // Spinner for judges
        List<String> judges = InfoCenter.getJudges();
        if(judges.size() > 0) {
            final ActionBar actionBar = getMyActivity().getSupportActionBar();
            actionBar.setDisplayShowTitleEnabled(true);
            ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(
                    actionBar.getThemedContext(),
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    InfoCenter.getJudges());

            actionBar.setListNavigationCallbacks(mAdapter, this);
        }

        return rootView;
    }

   @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_CURRENT_PAGE, mCurrentPage); // Current page
        outState.putInt(ARG_SELECTED_JUDGE, mSelectedJudge); // Selected Judge
        outState.putBooleanArray(STATE_CONSISTENT_CRITERIA, inconsistentCriteria); // Inconsistent
    }

    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        mSelectedJudge = position;
        if(mPageAdapter != null)
            mPageAdapter.refresh();

        // TODO: get correct inconsistent data.
        inconsistentCriteria = new boolean[elements().size()];
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if(!getMyActivity().isDrawerOpen())
            inflater.inflate(R.menu.menu_compare, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_check) {
            checkConsistency();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setPage(int page) {
        mCurrentPage = page;
        mPager.setCurrentItem(mCurrentPage, true);
    }

    private void checkConsistency() {
        /*
        * TODO:
        * Depending on mElements, get attributes or profiles data.
        *
        * Option 1:
         * Per each criteria check consistency (include all judges)
         * If criteria is inconsistent, check per individual judge.
         *
         * Inconsistent per-judge criteria must be announced:
         * E.g. Judge 1 is inconsistent over Criteria 1
         *
        * Option 2:
         * For each judge, check consistency over each criteria.
         * Inconsistent criteria will have a red underline on the corresponding tab-judge combination.
         * Inconsistent criteria will have a warning icon on the navigation drawer.
         *
         * Toast message: found X inconsistencies by X judges.
         *
        * Option 3:
         * For each criteria check consistency (include all judges)
         * Inconsistent criteria will have a warning icon on the navigation drawer.
         *
        * */

        // This is just for testing. TODO: get this right.
        for(int i=0; i<inconsistentCriteria.length; i++) {
            inconsistentCriteria[i] = i % 2 == 1;
        }

        int count = 0;
        for(boolean b : inconsistentCriteria){
            if(b) count++;
        }

        if(count == 0) {
            Toast.makeText(getActivity(), "Current data is consistent", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Found "+count+" inconsistencies", Toast.LENGTH_SHORT).show();
        }
    }

    private List<String> elements() {
        if(mElements == 0)
            return InfoCenter.getAttributes();
        else
            return InfoCenter.getProfiles();
    }

    public static CompareFragment newInstance(int index, int elements, int page) {
        CompareFragment fragment = new CompareFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE_INDEX, index);
        args.putInt(ARG_CURRENT_PAGE, page);
        args.putInt(ARG_ELEMENTS, elements);
        fragment.setArguments(args);
        return fragment;
    }

    private MainActivity getMyActivity() {
        return (MainActivity) getActivity();
    }

    public class ComparingPagerAdapter extends FragmentStatePagerAdapter implements ViewPager.OnPageChangeListener {

        ComparisonFragment[] pages;

        public ComparingPagerAdapter(FragmentManager fm) {
            super(fm);
            int count = elements().size();
            pages = new ComparisonFragment[count];
        }

        @Override
        public Fragment getItem(int i) {
            if(pages[i] == null) {
                pages[i] = ComparisonFragment.newInstance(mElements, i, mSelectedJudge);
            }
            return pages[i];
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return elements().get(position);
        }

        @Override
        public void onPageScrolled(int i, float v, int i2) { }

        @Override
        public void onPageSelected(int i) {
            mCurrentPage = i;
            int color = inconsistentCriteria[i] ? Color.RED : Color.BLACK;
            mPagerTabStrip.setTabIndicatorColor(color);
            refresh();
        }

        @Override
        public void onPageScrollStateChanged(int i) { }

        @Override
        public int getCount() {
            return pages.length;
        }

        public void refresh(){
            if(pages[mCurrentPage] != null)
                pages[mCurrentPage].updateInfo(mElements, mCurrentPage, mSelectedJudge);
        }

    }


}
