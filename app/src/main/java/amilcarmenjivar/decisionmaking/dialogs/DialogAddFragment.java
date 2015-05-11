package amilcarmenjivar.decisionmaking.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import amilcarmenjivar.decisionmaking.R;

/**
 *
 * Created by Amilcar Menjivar on 29/04/2015.
 */
public class DialogAddFragment extends DialogFragment {

    private static final String ARG_ELEMENT_TYPE = "arg_elements";

    private final int[] titles = { R.string.new_candidate, R.string.new_attribute, R.string.new_profile, R.string.new_judge };

    private OnDialogResultListener mDialogResultListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Bundle b = savedInstanceState != null ? savedInstanceState : getArguments();
        final int elementType = b == null ? 0 : b.getInt(ARG_ELEMENT_TYPE);

        final EditText inputView = new EditText(this.getActivity());
        inputView.setHint(R.string.hint_add_dialog);

        builder.setView(viewWithMargins(inputView))
                .setTitle(titles[elementType])
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String input = inputView.getText().toString();
                        mDialogResultListener.onDialogResult(input, true);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String input = inputView.getText().toString();
                        mDialogResultListener.onDialogResult(input, false);
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialog;
    }

    public static DialogAddFragment newInstance(int elementType, OnDialogResultListener listener) {
        DialogAddFragment fragment = new DialogAddFragment();
        fragment.mDialogResultListener = listener;

        Bundle args = new Bundle();
        args.putInt(ARG_ELEMENT_TYPE, elementType);
        fragment.setArguments(args);
        return fragment;
    }

    private float toPix(int size) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, getActivity().getResources().getDisplayMetrics());
    }

    private View viewWithMargins(EditText textView) {
        FrameLayout container = new FrameLayout(this.getActivity());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT);
        params.topMargin = (int) toPix(4);
        params.leftMargin = params.rightMargin = (int) toPix(20);

        textView.setSingleLine();
        textView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        textView.setLayoutParams(params);

        container.addView(textView);

        return container;
    }

    public interface OnDialogResultListener {

        /**
         * @param userInput What ever was introduced by the user.
         * @param accepted true if user clicked the positive button, false otherwise.
         */
        public void onDialogResult(String userInput, boolean accepted);

    }

}
