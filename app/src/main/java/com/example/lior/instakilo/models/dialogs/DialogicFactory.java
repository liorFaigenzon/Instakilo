package com.example.lior.instakilo.models.dialogs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.lior.instakilo.R;

/**
 * Created by lior on 13/08/2016.
 */
public class DialogicFactory {


    public static MaterialDialog getInputDialog(Context context) {
        return new MaterialDialog.Builder(context)
                .title(R.string.dialog_new_record_title)
                .input(R.string.dialog_new_record_hint, R.string.dialog_empty_string, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        Log.d("OnInput", input.toString());
                    }
                })
                .negativeText(R.string.dialog_cancel_button)
                .positiveText(R.string.dialog_confirm_button)
                .build();
    }

    public static MaterialDialog getAcceptDialog(Context context) {
        return new MaterialDialog.Builder(context)
                .title("Are you sure?")
                .negativeText(R.string.dialog_cancel_button)
                .positiveText(R.string.dialog_confirm_button)
                .build();
    }
}
