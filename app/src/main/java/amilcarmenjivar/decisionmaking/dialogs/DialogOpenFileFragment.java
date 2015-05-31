package amilcarmenjivar.decisionmaking.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import java.util.List;

import amilcarmenjivar.decisionmaking.FileIO;
import amilcarmenjivar.decisionmaking.R;

/**
 * Created by Amilcar Menjivar on 07/05/2015.
 */
// TODO: Remove!
public class DialogOpenFileFragment extends DialogFragment {

    private OnFileChosenListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.import_file));

//        final String[] mFileList = FileIO.listFiles();
        List<String> mFileList = FileIO.listFiles();

        if( mFileList == null || mFileList.size() == 0) {
            builder.setMessage("No valid files found"); // TODO: stringRes & FileIO.SAVE_DIRECTORY
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (mListener != null) {
                        mListener.onFileChosen(null);
                    }
                }
            });
        } else {
//            builder.setItems(mFileList, new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int which) {
//                    if (mListener != null) {
//                        mListener.onFileChosen(mFileList[which]);
//                    }
//                }
//            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (mListener != null) {
                        mListener.onFileChosen(null);
                    }
                }
            });
        }
        return builder.create();
    }

    public static DialogOpenFileFragment newInstance(OnFileChosenListener listener) {
        DialogOpenFileFragment fragment = new DialogOpenFileFragment();
        fragment.mListener = listener;

        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public interface OnFileChosenListener {

        public void onFileChosen(String fileName);

    }

}
