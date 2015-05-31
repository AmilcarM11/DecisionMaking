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
 * Created by Amilcar Menjivar on 30/04/2015.
 */
public class DialogFileNameFragment extends DialogFragment {

    private OnDialogResultListener mDialogResultListener;

    private String suggestedFileName = "";

    public static final String ARG_SUGGESTED_FILENAME = "suggested_filename";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        suggestedFileName = getArguments().getString(ARG_SUGGESTED_FILENAME, "");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final EditText inputView = new EditText(this.getActivity());
        inputView.setHint(R.string.hint_file_name);

        // Suggested Text
        inputView.setText(suggestedFileName);
        inputView.setSelectAllOnFocus(true);


        builder.setView(viewWithMargins(inputView))
                .setTitle("Save Results")
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String input = inputView.getText().toString();
                        mDialogResultListener.onSaveFileDialogResult(input, true);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String input = inputView.getText().toString();
                        mDialogResultListener.onSaveFileDialogResult(input, false);
                    }
                });

        // Open keyboard
        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialog;
    }

    public static DialogFileNameFragment newInstance(OnDialogResultListener listener, String suggestedFileName) {
        DialogFileNameFragment fragment = new DialogFileNameFragment();
        fragment.mDialogResultListener = listener;

        Bundle args = new Bundle();
        args.putString(ARG_SUGGESTED_FILENAME, suggestedFileName);
        fragment.setArguments(args);
        return fragment;
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

    private float toPix(int size) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, getActivity().getResources().getDisplayMetrics());
    }

    public interface OnDialogResultListener {

        /**
         * @param userInput What ever was introduced by the user.
         * @param accepted true if user clicked the positive button, false otherwise.
         */
        public void onSaveFileDialogResult(String userInput, boolean accepted);

    }


}
