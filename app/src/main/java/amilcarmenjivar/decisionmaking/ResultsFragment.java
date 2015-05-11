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
public class ResultsFragment extends Fragment {

    private static final String ARG_PAGE_INDEX = "navigation_page_index";

    private DecimalFormat dFormat = new DecimalFormat("#.0000");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_results, container, false);
        TableLayout table = (TableLayout) rootView.findViewById(R.id.table_results);

        List<String> profiles = InfoCenter.getProfiles();
        List<String> candidates = InfoCenter.getCandidates();
        double[][] data = DecisionAlgorithm.getResults().result;

        for(int p=0; p<profiles.size(); p++) {
            TableRow titleRow = (TableRow) inflater.inflate(R.layout.row_result_title, container, false);

            // Profile title
            TextView title = (TextView) titleRow.findViewById(R.id.result_title);
            title.setText(profiles.get(p));
            table.addView(titleRow);

            // Get Results
            double[][] results = new double[candidates.size()][2];
            for (int c = 0; c < results.length; c++) {
                results[c][0] = c;
                results[c][1] = data[c][p];
            }

            // Order Results (descendant)
            Arrays.sort(results, new Comparator<double[]>() {
                public int compare(double[] d1, double[] d2) {
                    Double numOfKeys1 = d1[1];
                    Double numOfKeys2 = d2[1];
                    return numOfKeys2.compareTo(numOfKeys1);
                }
            });

            // Display Results
            for (int c = 0; c < candidates.size(); c++) {
                View row = inflater.inflate(R.layout.row_result, table, false);
                TextView score = (TextView) row.findViewById(R.id.result_score);
                TextView name = (TextView) row.findViewById(R.id.result_candidate);

                name.setText(candidates.get((int) results[c][0]));
                score.setText(dFormat.format(results[c][1]));

                table.addView(row);
            }
        }
        return rootView;
    }

    public static ResultsFragment newInstance(int pageIndex) {
        ResultsFragment fragment = new ResultsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE_INDEX, pageIndex);

        fragment.setArguments(args);
        return fragment;
    }

}
