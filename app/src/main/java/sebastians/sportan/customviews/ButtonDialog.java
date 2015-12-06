package sebastians.sportan.customviews;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;

/**
 * Created by kunterbunt on 14.06.15.
 */
public abstract class ButtonDialog {

    public ButtonDialog(Context context, @Nullable String title, String positiveMessage, @Nullable String neutralMessage,
                        @Nullable String negativeMessage, @Nullable String hint) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (title != null)
            builder.setMessage(title);

        builder.setPositiveButton(positiveMessage, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onPositiveButtonClick();
            }
        });

        if (neutralMessage != null) {
            builder.setNeutralButton(neutralMessage, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onNeutralButtonClick();
                }
            });
        }

        if (negativeMessage != null) {
            builder.setNegativeButton(negativeMessage, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onNegativeButtonClick();
                }
            });
        }

        builder.show();
    }

    public abstract void onPositiveButtonClick();

    public void onNeutralButtonClick() {
        throw new UnsupportedOperationException("onNeutralButtonClick needs to be overridden!");
    }

    public void onNegativeButtonClick() {
        throw new UnsupportedOperationException("onNegativeButtonClick needs to be overridden!");
    }
}
