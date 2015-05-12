package amilcarmenjivar.decisionmaking;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import amilcarmenjivar.decisionmaking.dialogs.DialogNewOrOpenFragment;
import amilcarmenjivar.decisionmaking.dialogs.DialogOpenFileFragment;

/**
 * Activity used solely to load saved data,
 * When that data is not found, open a dialog to decide whether to load data or start a new instance.
 */
public class StartActivity extends ActionBarActivity implements DialogNewOrOpenFragment.OnDecisionMadeListener, DialogOpenFileFragment.OnFileChosenListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Must load information

        if (FileIO.isTempFileFound(this)) {
            Log.wtf("DecisionMaker", "Loading data from temp file...");
            if(InfoCenter.reload(this)) {
                Log.wtf("DecisionMaker", "Loading successful!");
            } else {
                Log.wtf("DecisionMaker", "Loading failed. Using test values instead.");
            }
            startMainActivity();
        } else {
            Log.wtf("DecisionMaker", "Temp file not found. What to do?");
            openDialog();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onDecisionMade(int decisionCode) {
        if(decisionCode == DialogNewOrOpenFragment.DECISION_CREATE) {
            Log.wtf("DecisionMaker", "Creating a new instance");
            startSetupActivity();

        } else if(decisionCode == DialogNewOrOpenFragment.DECISION_LOAD) {
            Log.wtf("DecisionMaker", "Selecting file to load...");
            openDialog2();
        }
    }

    @Override
    public void onFileChosen(String fileName) {
        if(fileName == null) {
            this.finish();
            return;
        }
        // Load file
        Toast.makeText(this, "Loading: " + fileName, Toast.LENGTH_SHORT).show();
        if(InfoCenter.importData(fileName)) {
            Log.wtf("DecisionMaker", "Loaded file successfully!");
            startMainActivity();
        } else {
            Log.wtf("DecisionMaker", "Loading failed. What to do now?");
            Toast.makeText(this, "Loading Failed!", Toast.LENGTH_SHORT).show();
            openDialog();
        }
    }

    public void openDialog() {
        Log.wtf("DecisionMaker", "Opening dialog: DialogNewOrOpenFragment");
        DialogNewOrOpenFragment fragment = DialogNewOrOpenFragment.newInstance(this);
        fragment.show(getSupportFragmentManager(), "DialogNewOrOpenFragment");
    }

    public void openDialog2() {
        Log.wtf("DecisionMaker", "Opening dialog: DialogOpenFileFragment");
        DialogOpenFileFragment fragment = DialogOpenFileFragment.newInstance(this);
        fragment.show(getSupportFragmentManager(), "DialogOpenFileFragment");
    }

    public void startMainActivity() {
        Intent anIntent = new Intent(this, MainActivity.class);
        startActivity(anIntent);
        this.finish();
    }

    public void startSetupActivity() {
        // TODO: make sure MainACtivity is up on the stack of SetupActivity

        Intent anIntent = new Intent(this, SetupActivity.class);
        startActivity(anIntent);
        this.finish();
    }
}
