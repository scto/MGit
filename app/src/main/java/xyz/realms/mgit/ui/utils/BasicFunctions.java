package xyz.realms.mgit.ui.utils;

import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.jetbrains.annotations.NotNull;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import timber.log.Timber;
import xyz.realms.mgit.ui.dialogs.ErrorDialog;
import xyz.realms.mgit.ui.fragments.SheimiFragmentActivity;

/**
 * Created by sheimi on 8/19/13.
 */
public class BasicFunctions {

    private static SheimiFragmentActivity mActiveActivity;

    public static String md5(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                .getInstance("MD5");
            digest.update(s.getBytes());
            byte[] messageDigest = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; ++i) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            Timber.e(e);
        }
        return "";
    }

    public static void setAvatarImage(ImageView imageView, String email) {
        String avatarUri = "";
        if (!email.isEmpty())
            avatarUri = "avatar://" + md5(email);

        ImageLoader im = BasicFunctions.getImageLoader();
        im.displayImage(avatarUri, imageView);
    }

    public static SheimiFragmentActivity getActiveActivity() {
        return mActiveActivity;
    }

    public static void setActiveActivity(SheimiFragmentActivity activity) {
        mActiveActivity = activity;
    }

    public static ImageLoader getImageLoader() {
        return getActiveActivity().getImageLoader();
    }

    public static void showError(@NonNull @NotNull SheimiFragmentActivity activity, @StringRes final int errorTitleRes, @StringRes final int errorRes) {
        ErrorDialog errorDialog = new ErrorDialog();
        errorDialog.setErrorRes(errorRes);
        errorDialog.setErrorTitleRes(errorTitleRes);
        errorDialog.show(activity.getSupportFragmentManager(), "error-dialog");
    }

    public static void showException(@NonNull @NotNull SheimiFragmentActivity activity, Throwable throwable, @StringRes final int errorTitleRes, @StringRes final int errorRes) {
        ErrorDialog errorDialog = new ErrorDialog();
        errorDialog.setThrowable(throwable);
        errorDialog.setErrorRes(errorRes);
        errorDialog.setErrorTitleRes(errorTitleRes);
        errorDialog.show(activity.getSupportFragmentManager(), "exception-dialog");
    }


    public static void showException(@NonNull @NotNull SheimiFragmentActivity activity, @NonNull Throwable throwable, @StringRes final int errorRes) {
        showException(activity, throwable, 0, errorRes);
    }

    public static void showException(@NonNull @NotNull SheimiFragmentActivity activity, @NonNull Throwable throwable) {
        showException(activity, throwable, 0);
    }
}
