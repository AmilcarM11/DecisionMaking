package amilcarmenjivar.decisionmaking;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Toast;

import com.terlici.dragndroplist.DragNDropAdapter;
import com.terlici.dragndroplist.DragNDropListView;

import java.util.List;

import amilcarmenjivar.decisionmaking.data.Instance;

/**
* Created by Amilcar Menjivar on 28/04/2015.
*/
public class ElementsFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String ARG_ELEMENT_TYPE = "arg_elements";

    private int mElements;
    private ElementsAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mElements = getArguments().getInt(ARG_ELEMENT_TYPE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_elements, container, false);
        DragNDropListView listView = (DragNDropListView) rootView.findViewById(R.id.setup_elements_list);

        mAdapter = new ElementsAdapter(getActivity());
        listView.setDragNDropAdapter(mAdapter);

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

    private Instance getInstance() {
        return ((EditActivity)getActivity()).getEditingInstance();
    }

    private List<String> elements() {
        if (mElements == 0) {
            return getInstance().getCandidates();
        } else if (mElements == 1) {
            return getInstance().getAttributes();
        } else if (mElements == 2) {
            return getInstance().getProfiles();
        } else {
            return getInstance().getJudges();
        }
    }

    private boolean tryDelete(int position) {
        if (mElements == 0) {
            return getInstance().removeCandidate(position);
        } else if (mElements == 1) {
            return getInstance().removeAttribute(position);
        } else if (mElements == 2) {
            return getInstance().removeProfile(position);
        } else {
            return getInstance().removeJudge(position);
        }
    }

    private void onListReOrdered() {
        if(mElements == 0) { // Candidates;
            getInstance().getAttributeData().updateElements();
        } else if(mElements == 1) { // Attributes
            getInstance().getProfileData().updateElements();
        }
    }

    private class ElementsAdapter extends ArrayAdapter<String> implements DragNDropAdapter {

        public ElementsAdapter(Context context) {
            super(context, R.layout.list_item, R.id.list_itemText, elements());
        }

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

        @Override
        public int getDragHandler() {
            return R.id.handler;
        }

        @Override
        public void onItemDrag(DragNDropListView parent, View view, int position, long id) { }

        @Override
        public void onItemDrop(DragNDropListView parent, View view, int startPosition, int endPosition, long id) {
            List<String> list = elements();

            if(startPosition > endPosition) {
                String element = list.remove(startPosition);
                list.add(endPosition, element);
            } else if(startPosition < endPosition) {
                String element = list.remove(startPosition);
                list.add(endPosition, element);
            }
            onListReOrdered();
        }
    }
}
