package amilcarmenjivar.decisionmaking;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import amilcarmenjivar.decisionmaking.data.DataManager;
import amilcarmenjivar.decisionmaking.data.ResultData;

/**
 *
 * Created by Amilcar Menjivar on 29/04/2015.
 */
public class ResultPageFragment extends Fragment {

    private static final String ARG_PAGE = "results_page";

    private int mPage = 0;

    private DecimalFormat dFormat = new DecimalFormat("#.0000");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPage = getArguments().getInt(ARG_PAGE, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_results_page, container, false);
        TableLayout table = (TableLayout) rootView.findViewById(R.id.table_results);

        ResultData resultData = DataManager.getLoadedInstance().getResultData();

        List<String> mTitles = resultData.getCriteriaByPage(mPage);
        List<String> mElements = resultData.getElementsByPage(mPage);

        // Get Scores
        double[][] scores = resultData.getScoreByPage(mPage);

        // Get ordered indexes
        int[][] indexes = resultData.getOrderedIndexesByPage(mPage);

        // Get tiers for each score
        int[][] tiers = resultData.getScoreTierByPage(mPage);
        int[] iconResourceByTier = new int[] {
                R.drawable.ic_star_empty, // tier 0
                R.drawable.ic_star_half,  // tier 1
                R.drawable.ic_star_full   // tier 2
        };

        for(int criteria=0; criteria<mTitles.size(); criteria++) {
            TableRow titleRow = (TableRow) inflater.inflate(R.layout.row_result_title, container, false);

            // Title
            TextView title = (TextView) titleRow.findViewById(R.id.result_title);
            title.setText(mTitles.get(criteria).toUpperCase());
            table.addView(titleRow);

            // Display Results
            for (int i = 0; i < mElements.size(); i++) {
                View row = inflater.inflate(R.layout.row_result, table, false);
                TextView scoreTextView = (TextView) row.findViewById(R.id.result_score);
                TextView nameTextView = (TextView) row.findViewById(R.id.result_candidate);
                ImageView starImageView = (ImageView) row.findViewById(R.id.result_star_icon);

                int indx = indexes[criteria][i];
                double score = scores[indx][criteria];
                int tier = tiers[criteria][indx];

                nameTextView.setText(mElements.get(indx));
                scoreTextView.setText(dFormat.format(score));

                // Star Icon: full above 65%, half above 35%, empty below 35%
                int starIcon = iconResourceByTier[tier];
                starImageView.setImageResource(starIcon);

                table.addView(row);
            }
        }
        return rootView;
    }

    public static ResultPageFragment newInstance(int page) {
        ResultPageFragment fragment = new ResultPageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);

        fragment.setArguments(args);
        return fragment;
    }

}
