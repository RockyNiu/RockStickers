package com.rockyniu.stickers.util;

import android.app.Activity;
import android.content.res.Resources;
import android.widget.Toast;

import com.rockyniu.stickers.R;

public class ToastHelper {


	/**
	 * Shows an toast message with the given message.
	 * 
	 * @param activity
	 *            activity
	 * @param message
	 *            message to show or {@code null} for none
	 */
	public static void showErrorToast(Activity activity, String message) {
		String errorMessage = getErrorMessage(activity, message);
		ToastHelper.showToastInternal(activity, errorMessage);
	}

	private static String getErrorMessage(Activity activity, String message) {
		Resources resources = activity.getResources();
		if (message == null) {
			return resources.getString(R.string.error);
		}
		return resources.getString(R.string.error_format, message);
	}

	public static void showToastInternal(final Activity activity,
			final String toastMessage) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(activity, toastMessage, Toast.LENGTH_LONG)
						.show();
			}
		});
	}

	public static void showToastInternal(final Activity activity,
			final String toastMessage, final int duration,
			final float horizontalMargin, final float verticalMargin) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				Toast toast = Toast.makeText(activity, toastMessage,
						Toast.LENGTH_LONG);
				toast.setMargin(horizontalMargin, verticalMargin);
				toast.show();
			}
		});
	}

}
