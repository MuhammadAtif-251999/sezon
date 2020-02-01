package io.sezon.sezon.utils;

import androidx.annotation.StringRes;
import android.view.View;

/**
 * Created by bradhawk on 10/17/2016.
 */

public interface SnackbarController {
    void showSnackbar(@StringRes int stringRes, int duration, @StringRes int actionResText, View.OnClickListener onClickListener);
}
