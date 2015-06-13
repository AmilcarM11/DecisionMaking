package amilcarmenjivar.decisionmaking;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import amilcarmenjivar.decisionmaking.data.ResultPage;


public class ResultsFragment extends Fragment {

    private static final String ARG_CURRENT_PAGE = "selected_page";

    private int mCurrentPage = 0;

    private ViewPager mPager;

    public static ResultsFragment newInstance() {
        ResultsFragment fragment = new ResultsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ResultsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            mCurrentPage = savedInstanceState.getInt(ARG_CURRENT_PAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_results, container, false);

        // Pager for comparison's criteria
        mPager = (ViewPager) rootView.findViewById(R.id.results_viewPager);
        ResultsPagerAdapter mPageAdapter = new ResultsPagerAdapter(getActivity().getSupportFragmentManager());
        mPager.setAdapter(mPageAdapter);
        mPager.setOnPageChangeListener(mPageAdapter);

        // Scroll to page
        if(mCurrentPage != 0) {
            setPage(mCurrentPage);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_CURRENT_PAGE, mCurrentPage); // Current page
    }

    public void setPage(int page) {
        mCurrentPage = page;
        mPager.setCurrentItem(mCurrentPage, true);
    }

    public class ResultsPagerAdapter extends FragmentStatePagerAdapter implements ViewPager.OnPageChangeListener {

        ResultPageFragment[] pages;

        public ResultsPagerAdapter(FragmentManager fm) {
            super(fm);
            pages = new ResultPageFragment[6];
        }

        @Override
        public Fragment getItem(int page) {
            if(pages[page] == null) {
                pages[page] = ResultPageFragment.newInstance(page);
            }
            return pages[page];
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getString(ResultPage.values()[position].stringRes).toUpperCase();
        }

        @Override
        public void onPageScrolled(int i, float v, int i2) {}

        @Override
        public void onPageSelected(int i) {
            mCurrentPage = i;
        }

        @Override
        public void onPageScrollStateChanged(int i) {}

        @Override
        public int getCount() {
            return pages.length;
        }
    }

}
