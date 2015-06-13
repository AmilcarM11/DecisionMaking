package amilcarmenjivar.decisionmaking;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TableLayout;

import java.util.ArrayList;
import java.util.List;

import amilcarmenjivar.decisionmaking.data.Data;
import amilcarmenjivar.decisionmaking.data.DataManager;
import amilcarmenjivar.decisionmaking.views.ComboSeekBar;
import amilcarmenjivar.decisionmaking.views.MyTextView;

public class ComparisonFragment extends Fragment {

    private static final String ARG_ELEMENT_TYPE = "arg_elements";
    private static final String ARG_CRITERIA = "arg_criteria";
    private static final String ARG_JUDGE = "arg_judge";

    private int mElements = 0;
    private int mCriteria = 0;
    private int mJudge = 0;

    private List<ComboSeekBar> mBars;

    private OnComparisonChangedListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mElements = getArguments().getInt(ARG_ELEMENT_TYPE);
        mCriteria = getArguments().getInt(ARG_CRITERIA);
        mJudge = getArguments().getInt(ARG_JUDGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_comparison, container, false);
        TableLayout table = (TableLayout) rootView.findViewById(R.id.comparingTable);

        Data data = mElements == 0 ? DataManager.getAttributeData() : DataManager.getProfileData();
        String criteria = (mElements == 0 ? DataManager.getAttributes() : DataManager.getProfiles()).get(mCriteria);
        String judge = DataManager.getJudges().get(mJudge);

        // Asynchronously populate the comparison table.
        mBars = new ArrayList<ComboSeekBar>();
        new PopulateComparisonTableTask(table, inflater, data, criteria, judge).execute();
        return rootView;
    }

    public void showSuggestedValues() {
        if(mBars != null) {
            int n = mElements == 0 ? DataManager.getCandidates().size() : DataManager.getAttributes().size();
            double[] comparisonVector = mElements == 0 ?
                    DataManager.getLoadedInstance().getAttributePreferenceVector(mCriteria, mJudge)
                    : DataManager.getLoadedInstance().getProfilePreferenceVector(mCriteria, mJudge);
            int[] suggestions = DecisionAlgorithm.getConsistentSuggestions(n, comparisonVector);
            if(suggestions.length == mBars.size()) {
                for(int i = 0; i<suggestions.length; i++) {
                    mBars.get(i).setSuggestedValue(suggestions[i]);
                }
            }
        }
    }

    public void hideSuggestedValues() {
        if(mBars != null) {
            for (ComboSeekBar bar : mBars) {
                bar.setSuggestedValue(0);
            }
        }
    }

    public void updateInfo(int newElements, int newCriteria, int newJudge) {
        if(newElements != mElements) {
            mElements = newElements;
            getArguments().putInt(ARG_ELEMENT_TYPE, newElements);
        }
        if(newCriteria != mCriteria) {
            mCriteria = newCriteria;
            getArguments().putInt(ARG_CRITERIA, newCriteria);
        }
        if(newJudge != mJudge) {
            mJudge = newJudge;
            getArguments().putInt(ARG_JUDGE, newJudge);
        }

        // Update the values on the seek bars
        if(mBars != null) {
            Data data = mElements == 0 ? DataManager.getAttributeData() : DataManager.getProfileData();
            int[][][] rawData = data.getRawData();
            for(int i=0; i< mBars.size(); i++) {
                mBars.get(i).setInfo(i, rawData[mCriteria][i][mJudge]);
            }
        }
    }

    public static ComparisonFragment newInstance(int elemType, int criteria, int judge, OnComparisonChangedListener listener) {
        ComparisonFragment fragment = new ComparisonFragment();
        fragment.mListener = listener;
        Bundle args = new Bundle();
        args.putInt(ARG_ELEMENT_TYPE, elemType);
        args.putInt(ARG_CRITERIA, criteria);
        args.putInt(ARG_JUDGE, judge);
        fragment.setArguments(args);
        return fragment;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            MyTextView textView = (MyTextView) v;

            int index = textView.getIndex();
            int movement = textView.getMovement();

            if(mBars.get(index).changeValue(movement)) {
                int value = mBars.get(index).getSelectedValue();
                writeData(index, value);
            }
        }
    };

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ComboSeekBar seekBar = (ComboSeekBar) view;
            int value = seekBar.getSelectedValue();
            writeData(position, value);
        }
    };

    private void writeData(int pair, int value) {
        if(mElements==0) {
            DataManager.writeAttributesInfo(mCriteria, pair, mJudge, value);
            mListener.onComparisonChanged(mCriteria, mJudge);
        } else {
            DataManager.writeProfilesInfo(mCriteria, pair, mJudge, value);
            mListener.onComparisonChanged(mCriteria, mJudge);
        }
    }


    private class PopulateComparisonTableTask extends AsyncTask<Void, Void, List<Pair>> {

        private TableLayout table;
        private LayoutInflater inflater;
        private Data data;
        private String criteria;
        private String judge;

        public PopulateComparisonTableTask(TableLayout table, LayoutInflater inflater, Data data, String criteria, String judge) {
            this.inflater = inflater;
            this.table = table;
            this.data = data;
            this.criteria = criteria;
            this.judge = judge;
        }

        @Override
        protected void onPostExecute(List<Pair> pairs) {
            int i = 0;
            for(Pair pair : pairs){
                View row = inflater.inflate(R.layout.row_compare, table, false);
                MyTextView elem1 = (MyTextView) row.findViewById(R.id.textElem1);
                MyTextView elem2 = (MyTextView) row.findViewById(R.id.textElem2);
                ComboSeekBar seekBar = (ComboSeekBar) row.findViewById(R.id.seekBar);

                // Set the text
                elem1.setText(pair.elem1);
                elem2.setText(pair.elem2);

                // Add click listeners to the text views, so that clicking them move the seek bar value.
                elem1.configure(i, -1);
                elem2.configure(i, +1);
                elem1.setOnClickListener(onClickListener);
                elem2.setOnClickListener(onClickListener);

                int value = data.getValue(criteria, pair, judge);
                seekBar.setInfo(i, value);
                seekBar.setOnItemClickListener(onItemClickListener);
                mBars.add(seekBar);
                table.addView(row);
                i++;
            }
        }

        @Override
        protected void onPreExecute() {
            mBars.clear();
        }

        @Override
        protected List<Pair> doInBackground(Void... params) {
            // everything in here gets executed in a separate thread
            return data.getPairs();
        }

    }
}
