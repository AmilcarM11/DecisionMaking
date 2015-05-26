package amilcarmenjivar.decisionmaking;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import amilcarmenjivar.decisionmaking.data.DataManager;
import amilcarmenjivar.decisionmaking.data.Result;


public class ResultsFragment extends Fragment implements ResultProvider {

    private static final String ARG_CURRENT_PAGE = "selected_page";

    private int mCurrentPage = 0;

    private Result mResult;

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
        mResult = DataManager.getLoadedInstance().getResult();
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

    @Override
    public List<String> getCriteriaForPage(int page) {
        switch(Pages.values()[page]) {
            case CANDIDATES_PER_PROFILE:
            case ATTRIBUTES_PER_PROFILE:
                return DataManager.getProfiles();
            case CANDIDATES_PER_ATTRIBUTE:
            case PROFILES_PER_ATTRIBUTE:
                return DataManager.getAttributes();
            case ATTRIBUTES_PER_CANDIDATE:
            case PROFILES_PER_CANDIDATE:
                return DataManager.getCandidates();
        }
        return null;
    }

    @Override
    public List<String> getElementsForPage(int page) {
        switch(Pages.values()[page]) {
            case CANDIDATES_PER_PROFILE:
            case CANDIDATES_PER_ATTRIBUTE:
                return DataManager.getCandidates();
            case ATTRIBUTES_PER_PROFILE:
            case ATTRIBUTES_PER_CANDIDATE:
                return DataManager.getAttributes();
            case PROFILES_PER_CANDIDATE:
            case PROFILES_PER_ATTRIBUTE:
                return DataManager.getProfiles();
        }
        return null;
    }

    @Override
    public double[][] getDataForPage(int page) {
        Pages p = Pages.values()[page];
        switch(p) {
            case CANDIDATES_PER_PROFILE:
                return mResult.resultMatrix;
            case ATTRIBUTES_PER_PROFILE:
                return mResult.profilesMatrix;
            case CANDIDATES_PER_ATTRIBUTE:
                return mResult.attributesMatrix;
            case ATTRIBUTES_PER_CANDIDATE:
                return transpose(mResult.attributesMatrix);
            case PROFILES_PER_CANDIDATE:
                return transpose(mResult.resultMatrix);
            case PROFILES_PER_ATTRIBUTE:
                return transpose(mResult.profilesMatrix);
        }
        return new double[0][];
    }

    private double[][] transpose(double[][] matrix) {
        double[][] transposed = new double[matrix[0].length][matrix.length];
        for(int i = 0; i < matrix.length; i++) {
            for(int e = 0; e<matrix[i].length; e++) {
                transposed[e][i] = matrix[i][e];
            }
        }
        return transposed;
    }

    public class ResultsPagerAdapter extends FragmentStatePagerAdapter implements ViewPager.OnPageChangeListener {

        ResultPageFragment[] pages;

        public ResultsPagerAdapter(FragmentManager fm) {
            super(fm);
            pages = new ResultPageFragment[6];
        }

        @Override
        public Fragment getItem(int i) {
            if(pages[i] == null) {
                pages[i] = ResultPageFragment.newInstance(i, ResultsFragment.this);
            }
            return pages[i];
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getString(Pages.values()[position].stringRes);
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

    private enum Pages {
        CANDIDATES_PER_PROFILE      (R.string.candidates_per_profile),
        ATTRIBUTES_PER_PROFILE      (R.string.attributes_per_profile),
        CANDIDATES_PER_ATTRIBUTE    (R.string.candidates_per_attribute),
        ATTRIBUTES_PER_CANDIDATE    (R.string.attributes_per_candidate),
        PROFILES_PER_CANDIDATE      (R.string.profiles_per_candidate),
        PROFILES_PER_ATTRIBUTE      (R.string.profiles_per_attribute);

        int stringRes;
        Pages(int stringRes){
            this.stringRes = stringRes;
        }
    }

}
