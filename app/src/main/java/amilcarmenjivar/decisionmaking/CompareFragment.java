package amilcarmenjivar.decisionmaking;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
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

import java.text.DecimalFormat;
import java.util.List;

import amilcarmenjivar.decisionmaking.data.DataManager;
import amilcarmenjivar.decisionmaking.views.PagerTabStrip;

import static amilcarmenjivar.decisionmaking.DecisionAlgorithm.isConsistencyAcceptable;

/**
 *
 * Created by Amilcar Menjivar on 28/04/2015.
 */
public class CompareFragment extends Fragment implements ActionBar.OnNavigationListener, OnComparisonChangedListener {

    private static final String ARG_PAGE_INDEX = "navigation_page_index";
    private static final String ARG_ELEMENTS = "selected_elements";
    private static final String ARG_CURRENT_PAGE = "selected_page";
    private static final String ARG_SELECTED_JUDGE = "selected_judge";

    private int mElements = 0;
    private int mCurrentPage = 0;
    private int mSelectedJudge = 0;

    private ViewPager mPager;
    private PagerTabStrip mPagerTabStrip;
    private ComparingPagerAdapter mPageAdapter;

    private boolean[] inconsistentCriteria = new boolean[0];
    private double[] consistencies = new double[0];

    DecimalFormat formatter = new DecimalFormat("0.000");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mElements = getArguments().getInt(ARG_ELEMENTS, 0);
        mCurrentPage = getArguments().getInt(ARG_CURRENT_PAGE, 0);
        inconsistentCriteria = new boolean[elements().size()];
        consistencies = new double[elements().size()];
        if(savedInstanceState != null ) {
            mCurrentPage = savedInstanceState.getInt(ARG_CURRENT_PAGE, 0);
            mSelectedJudge = savedInstanceState.getInt(ARG_SELECTED_JUDGE, 0);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_compare, container, false);

        // Pager for comparison's criteria
        mPager = (ViewPager) rootView.findViewById(R.id.compare_viewPager);
        mPageAdapter = new ComparingPagerAdapter(getMyActivity().getSupportFragmentManager());
        mPager.setAdapter(mPageAdapter);
        mPager.setOnPageChangeListener(mPageAdapter);

        // PagerTabStrip
        mPagerTabStrip = (PagerTabStrip) rootView.findViewById(R.id.tabStrip);

        // Scroll to page
        if(mCurrentPage != 0) {
            mPager.setCurrentItem(mCurrentPage, true);
        }

        // Spinner for judges
        List<String> judges = DataManager.getJudges();
        if(judges.size() > 0) {
            final ActionBar actionBar = getMyActivity().getSupportActionBar();
            actionBar.setDisplayShowTitleEnabled(true);
            ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(
                    actionBar.getThemedContext(),
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    DataManager.getJudges());

            actionBar.setListNavigationCallbacks(mAdapter, this);
        }

        return rootView;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null) {
            mSelectedJudge = savedInstanceState.getInt(ARG_SELECTED_JUDGE, mSelectedJudge);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_CURRENT_PAGE, mCurrentPage); // Current page
        outState.putInt(ARG_SELECTED_JUDGE, mSelectedJudge); // Selected Judge
    }

    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        mSelectedJudge = position;
        checkConsistency();
        if(mPageAdapter != null)
            mPageAdapter.refresh();
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

    @Override
    public void onComparisonChanged(int criteria, int judge) {
        double oldConsistency = consistencies[criteria];
        consistencies[criteria] = getConsistency(criteria, judge);
        inconsistentCriteria[criteria] = !isConsistencyAcceptable(consistencies[criteria]);
        double diff = consistencies[criteria] - oldConsistency;

        // Set tab strip color
        updateTabStrip();

        // Toast tracking consistency changes
        String toast = "Consistency" + (diff > 0 ? "--" : "++");
        Toast.makeText(getActivity(), toast, Toast.LENGTH_SHORT).show();
    }

    private void updateTabStrip() {
        if( inconsistentCriteria[mCurrentPage] ) {
            mPagerTabStrip.setTabIndicatorColorResource(R.color.inconsistentColor);
        } else {
            mPagerTabStrip.setTabIndicatorColorResource(R.color.accentColor);
        }
    }


    // Checks consistency per criteria and judge (judge=-1 means all judges)
    private double getConsistency(int criteria, int judge) {
        double consistency;
        if( mElements == 0 ){
            consistency = DataManager.getLoadedInstance().getAttributeConsistency(criteria, judge);
        } else {
            consistency = DataManager.getLoadedInstance().getProfileConsistency(criteria, judge);
        }
        return consistency;
    }

    public void setPage(int page) {
        mCurrentPage = page;
        mPager.setCurrentItem(mCurrentPage, true);
    }

    private void checkConsistency() {
        for(int i=0; i<inconsistentCriteria.length; i++) {
            consistencies[i] = getConsistency(i, mSelectedJudge);
            inconsistentCriteria[i] = !isConsistencyAcceptable(consistencies[i]);
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
            return DataManager.getAttributes();
        else
            return DataManager.getProfiles();
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
                pages[i] = ComparisonFragment.newInstance(mElements, i, mSelectedJudge, CompareFragment.this);
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

            // Toast consistency
            double consistency = consistencies[mCurrentPage];
            Toast.makeText(getActivity(), "Consistency: "+ formatter.format(consistency), Toast.LENGTH_SHORT).show();

            refresh();
        }

        @Override
        public void onPageScrollStateChanged(int i) { }

        @Override
        public int getCount() {
            return pages.length;
        }

        public void refresh(){
            if(pages[mCurrentPage] != null) {
                pages[mCurrentPage].updateInfo(mElements, mCurrentPage, mSelectedJudge);
                updateTabStrip();
            }
        }

    }


}
