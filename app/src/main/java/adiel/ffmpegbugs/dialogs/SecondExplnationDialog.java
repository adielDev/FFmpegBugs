package adiel.ffmpegbugs.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import adiel.ffmpegbugs.R;


/**
 * Created by recntrek7 on 30/04/17.
 */

public class SecondExplnationDialog extends DialogFragment {


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.secondExplanation));
        return builder.create();
    }
}
