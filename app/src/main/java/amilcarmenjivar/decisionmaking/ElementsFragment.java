package amilcarmenjivar.decisionmaking;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

/**
* Created by Amilcar Menjivar on 28/04/2015.
*/
public class ElementsFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String ARG_ELEMENT_TYPE = "arg_elements";

    private int mElements;
    private ArrayAdapter<String> mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Bundle b = savedInstanceState != null ? savedInstanceState : getArguments();
        //mElements = b == null ? 0 : b.getInt(ARG_ELEMENT_TYPE);
        mElements = getArguments().getInt(ARG_ELEMENT_TYPE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_elements, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.setup_elements_list);

        mAdapter = createAdapter();
        listView.setAdapter(mAdapter);

        return rootView;
    }

    public void refresh() {
        mAdapter.notifyDataSetChanged();
    }

    public static ElementsFragment newInstance(int elemType) {
        ElementsFragment fragment = new ElementsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ELEMENT_TYPE, elemType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String t = (String) parent.getItemAtPosition(position);
        Toast.makeText(getActivity(), "Removing :"+ t, Toast.LENGTH_SHORT).show();
    }

    private ArrayAdapter<String> createAdapter() {
        return new ArrayAdapter<String>(this.getActivity(), R.layout.list_item, R.id.list_itemText, elements()){
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ImageButton button = (ImageButton) view.findViewById(R.id.list_item_removeButton);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = mAdapter.getItem(position);
                        String message = tryDelete(position) ? "Deleted "+name : "Failed to delete "+name;
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        notifyDataSetInvalidated();
                    }
                });
                return view;
            }
        };
    }


    private List<String> elements() {
        if (mElements == 0) {
            return InfoCenter.getCandidates();
        } else if (mElements == 1) {
            return InfoCenter.getAttributes();
        } else if (mElements == 2) {
            return InfoCenter.getProfiles();
        } else {
            return InfoCenter.getJudges();
        }
    }

    private boolean tryDelete(int position) {
        if (mElements == 0) {
            return InfoCenter.removeCandidate(position);
        } else if (mElements == 1) {
            return InfoCenter.removeAttribute(position);
        } else if (mElements == 2) {
            return InfoCenter.removeProfile(position);
        } else {
            return InfoCenter.removeJudge(position);
        }
    }
}
