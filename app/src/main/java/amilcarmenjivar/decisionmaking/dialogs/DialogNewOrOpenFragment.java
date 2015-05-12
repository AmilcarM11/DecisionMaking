package amilcarmenjivar.decisionmaking.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;

import amilcarmenjivar.decisionmaking.R;

/**
 * Created by Amilcar Menjivar on 11/05/2015.
 */
public class DialogNewOrOpenFragment extends DialogFragment {

    public static int DECISION_CREATE = 0;
    public static int DECISION_LOAD = 1;

    OnDecisionMadeListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.app_name);
        builder.setMessage(R.string.new_or_load_message);

        builder.setPositiveButton("Create new", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (mListener != null) {
                    mListener.onDecisionMade(DECISION_CREATE);
                }
            }
        });

        builder.setNeutralButton("Load file", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (mListener != null) {
                    mListener.onDecisionMade(DECISION_LOAD);
                }
            }
        });

        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    dialog.cancel();
                    getActivity().finish();
                    return true;
                }
                return false;
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    public static DialogNewOrOpenFragment newInstance(OnDecisionMadeListener listener) {
        DialogNewOrOpenFragment fragment = new DialogNewOrOpenFragment();
        fragment.mListener = listener;

        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public interface OnDecisionMadeListener {

        public void onDecisionMade(int decisionCode);

    }

}
