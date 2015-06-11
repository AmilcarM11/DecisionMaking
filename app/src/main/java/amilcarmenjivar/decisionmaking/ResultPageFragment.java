package amilcarmenjivar.decisionmaking;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 *
 * Created by Amilcar Menjivar on 29/04/2015.
 */
public class ResultPageFragment extends Fragment {

    private static final String ARG_PAGE = "results_page";

    private List<String> mTitles;
    private List<String> mElements;
    private double[][] mData;

    private ResultProvider provider;
    private DecimalFormat dFormat = new DecimalFormat("#.0000");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int mPage = getArguments().getInt(ARG_PAGE);
        // TODO: provider becomes null.
        if(provider != null) {
            mTitles = provider.getCriteriaForPage(mPage);
            mElements = provider.getElementsForPage(mPage);
            mData = provider.getDataForPage(mPage);
        } else {
            // problem!
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_results_page, container, false);
        TableLayout table = (TableLayout) rootView.findViewById(R.id.table_results);

        for(int criteria=0; criteria<mTitles.size(); criteria++) {
            TableRow titleRow = (TableRow) inflater.inflate(R.layout.row_result_title, container, false);

            // Title
            TextView title = (TextView) titleRow.findViewById(R.id.result_title);
            title.setText(mTitles.get(criteria));
            table.addView(titleRow);

            // Get Results
            double[][] results = getOrderedResults(criteria);

            // Display Results
            for (int i = 0; i < mElements.size(); i++) {
                View row = inflater.inflate(R.layout.row_result, table, false);
                TextView score = (TextView) row.findViewById(R.id.result_score);
                TextView name = (TextView) row.findViewById(R.id.result_candidate);

                name.setText(mElements.get((int) results[i][0]));
                score.setText(dFormat.format(results[i][1]));

                table.addView(row);
            }
        }
        return rootView;
    }

    private double[][] getOrderedResults(int criteria) {
        double[][] results = new double[mElements.size()][2];
        for (int i = 0; i < results.length; i++) {
            results[i][0] = i;
            results[i][1] = mData[i][criteria];
        }

        // Order Results (descendant)
        Arrays.sort(results, new Comparator<double[]>() {
            public int compare(double[] d1, double[] d2) {
                Double numOfKeys1 = d1[1];
                Double numOfKeys2 = d2[1];
                return numOfKeys2.compareTo(numOfKeys1);
            }
        });

        return results;
    }

    public static ResultPageFragment newInstance(int page, ResultProvider provider) {
        ResultPageFragment fragment = new ResultPageFragment();
        fragment.provider = provider;
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);

        fragment.setArguments(args);
        return fragment;
    }

}
