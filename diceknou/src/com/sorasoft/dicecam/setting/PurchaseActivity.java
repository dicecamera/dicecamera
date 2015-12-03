package com.sorasoft.dicecam.setting;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.amazon.inapp.purchasing.Item;
import com.amazon.inapp.purchasing.PurchasingManager;
import com.android.vending.billing.IInAppBillingService;
import com.example.android.trivialdrivesample.util.IabHelper;
import com.example.android.trivialdrivesample.util.IabResult;
import com.example.android.trivialdrivesample.util.Inventory;
import com.example.android.trivialdrivesample.util.Purchase;
import com.example.android.trivialdrivesample.util.SkuDetails;
import com.sorasoft.dicecam.BaseActivity;
import com.sorasoft.dicecam.BuildConfig;
import com.sorasoft.dicecam.R;

public class PurchaseActivity extends BaseActivity {

	private Handler mMainHandler;
	
	private final String TAG = "PurchaseActivity";

	static final String DEBUG_SKU_PURCHASED = "android.test.purchased";
	static final String DEBUG_SKU_CANCELED = "android.test.canceled";
	static final String DEBUG_SKU_REFUNDED = "android.test.refunded";
	static final String DEBUG_SKU_UNAVAILABLE = "android.test.item_unavailable";

	static final int RC_REQUEST = 10001; // (arbitrary) request code for the
											// purchase flow

	static final String debugTAG = "iab";

	IInAppBillingService mService;
	IabHelper mHelper;

	SkuDetails mSkuDetails = null;

	Button buyButton = null;

	boolean isFailed = false;
	String failedMessage = null;

	private String getSKU() {
		// if ( BuildConfig.DEBUG ) return DEBUG_SKU_PURCHASED;
		// if ( BuildConfig.DEBUG ) return DEBUG_SKU_CANCELED;
		// if ( BuildConfig.DEBUG ) return DEBUG_SKU_REFUNDED;
		// if ( BuildConfig.DEBUG ) return DEBUG_SKU_UNAVAILABLE;
		return Settings.getSkuProUpgrade();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_purchase);

		ActionBar ab = getSupportActionBar();
		ab.setIcon(R.drawable.ico_title_fit);
		mMainHandler = new Handler(Looper.getMainLooper());
		

		buyButton = (Button) findViewById(R.id.purchase_buy_button);
		buyButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				goBuy();
			}
		});

		if (BuildConfig.DEBUG) {
			findViewById(R.id.purchase_thank_you_button).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					__back_to_not_purchased();
				}
			});
		}

		updateButtonVisibles();

		if (didPurchased() == false) {
			new Handler().post(new Runnable() {
				@Override
				public void run() {
					setupIabHelper();
				}
			});
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			closeActivity();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void setupIabHelper() {
		if (BuildConfig.DEBUG) {
			Log.d(debugTAG, "setup IabHelper : " + getSKU());
			Log.d(debugTAG, "Settings.getPublicKey(): \"" + Settings.getPublicKey() + "\"");
		}

		mHelper = new IabHelper(this, Settings.getPublicKey());
		if (BuildConfig.DEBUG) {
			mHelper.enableDebugLogging(true, debugTAG + ",helper");
		}
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result) {
				if (!result.isSuccess()) {
					// Oh noes, there was a problem.
					Log.d(debugTAG, "Problem setting up In-app Billing: " + result);
					setFailed("Problem setting up In-app Billing: (" + result.getResponse() + ") " + result.getMessage());
					return;
				}
				// Hooray, IAB is fully set up!
				Log.d(debugTAG, "IAB is fully set up!");

				new Handler().post(new Runnable() {

					@Override
					public void run() {
						requestInventoryAsync();
					}
				});
			}
		});
	}

	private void requestInventoryAsync() {
		Log.d(debugTAG, "requestInventoryAsync START");

		mSkuDetails = null;

		ArrayList<String> additionalSkuList = new ArrayList<String>();
		additionalSkuList.add(getSKU());

		try {
			mHelper.queryInventoryAsync(true, additionalSkuList, mQueryFinishedListener);
		} catch (Exception e) {
			e.printStackTrace();
			setFailed("Problem fetching the inventory: " + e.getLocalizedMessage());
		}
	}

	IabHelper.QueryInventoryFinishedListener mQueryFinishedListener = new IabHelper.QueryInventoryFinishedListener() {
		public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
			Log.d(debugTAG, "requestInventoryAsync FINISH");

			if (result.isFailure()) {
				// handle error
				Log.d(debugTAG, "Failure : " + result);
				displayError(result);
				return;
			}

			if (inventory.hasPurchase(getSKU())) {
				Purchase purchase = inventory.getPurchase(getSKU());
				Log.d(debugTAG, "Purchase: " + purchase);

				// purchased
				int purchaseStatus = purchase.getPurchaseState(); // 0
																	// (purchased),
																	// 1
																	// (canceled),
																	// or 2
																	// (refunded).
				if (purchaseStatus == 0) { // purchased
					Log.d(debugTAG, "ALREADY Purchased: " + inventory);
					setDidPurchased();
					return;
				} else if (purchaseStatus == 1) { // canceled
					Log.d(debugTAG, "CANCELED purchasing: " + inventory);
				} else if (purchaseStatus == 2) { // refunded
					Log.d(debugTAG, "REFUNDEDpurchasing: " + inventory);
				} else {
					Log.d(debugTAG, "INVALID purchase state: " + purchaseStatus);
				}
			}

			// ready to be purchased
			mSkuDetails = inventory.getSkuDetails(getSKU());
			Log.d(debugTAG, "READY to be purchased: " + mSkuDetails);

			updateButtonVisibles();
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mHelper != null) {
			try {
				mHelper.dispose();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		mHelper = null;
	}

	protected void setFailed(final String string) {
		isFailed = true;
		failedMessage = string;
		updateButtonVisibles();

		displayError(failedMessage);
	}

	protected void displayError(final String string) {
		final Context ctx = this;

		mMainHandler.post(new Runnable() {
			@Override
			public void run() {
				AlertDialog.Builder dlg = new AlertDialog.Builder(ctx);
				dlg.setTitle("Error");
				dlg.setMessage(string);
				dlg.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				dlg.show();
			}
		});
	}

	protected void displayError(IabResult result) {
		displayError("" + result.getMessage());
	}

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (null == netInfo || !netInfo.isConnected()) {
			return false;
		}
		return true;
	}

	protected void closeActivity() {
		this.finish();
	}

	protected void goBuy() {
		if (isFailed)
			return;
		if (isNotLoaded())
			return;
		try {
			mHelper.launchPurchaseFlow(this, getSKU(), RC_REQUEST, mPurchaseFinishedListener);
		} catch (Exception e) {
			e.printStackTrace();
			setFailed("Problem buying: " + e.getLocalizedMessage());
		}
	}

	// protected void goRestore() {
	// setDidPurchased();
	// }

	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
		public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
			if (result.isFailure()) {
				if (result.getResponse() == IabHelper.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED) {
					// Already Owned
					Log.d(debugTAG, "Already OWNED: " + result.getResponse() + "\n" + result);
					setDidPurchased();
				} else {
					Log.d(debugTAG, "Error purchasing: " + result.getResponse() + "\n" + result);
					displayError(result);
				}
			} else if (purchase.getSku().equals(getSKU())) {
				// give user access to premium content and update the UI
				int purchaseState = purchase.getPurchaseState();
				Log.d(debugTAG, "SUCCESS (purchaseState: " + purchaseState + ")");
				if (purchaseState == 0) {
					setDidPurchased();
				} else {

				}
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(debugTAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

		if (mHelper == null)
			return;

		// Pass on the activity result to the helper for handling
		if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
			// not handled, so handle it ourselves (here's where you'd
			// perform any handling of activity results not related to in-app
			// billing...
			super.onActivityResult(requestCode, resultCode, data);
		} else {
			Log.d(debugTAG, "onActivityResult handled by IABUtil.");
		}
	}

	protected boolean isNotLoaded() {
		return mSkuDetails == null ? true : false;
	}

	protected boolean didPurchased() {
		return Settings.getInstance().getProUpgrade();
	}

	protected void setDidPurchased() {
		Settings.getInstance().setProUpgrade(true);
		updateButtonVisibles();
	}

	protected void __back_to_not_purchased() {
		Settings.getInstance().setProUpgrade(false);
		updateButtonVisibles();
	}

	protected void updateButtonVisibles() {
		mMainHandler.post(new Runnable() {
			@Override
			public void run() {
				findViewById(R.id.purchase_restore_button_row).setVisibility(View.GONE);

				if (didPurchased()) {
					findViewById(R.id.purchase_buy_button_row).setVisibility(View.GONE);
					findViewById(R.id.purchase_thank_you_button_row).setVisibility(View.VISIBLE);
				} else if (isFailed) {
					buyButton.setText(R.string.error);
					findViewById(R.id.purchase_buy_button_row).setVisibility(View.VISIBLE);
					findViewById(R.id.purchase_thank_you_button_row).setVisibility(View.GONE);
				} else if (isNotLoaded()) {
					buyButton.setText(R.string.loading);
					findViewById(R.id.purchase_buy_button_row).setVisibility(View.VISIBLE);
					findViewById(R.id.purchase_thank_you_button_row).setVisibility(View.GONE);
				} else {
					String buyString = getResources().getString(R.string.buy);
					String text = (mSkuDetails == null ? buyString : String.format("%s (%s)", buyString, mSkuDetails.getPrice()));
					buyButton.setText(text);
					findViewById(R.id.purchase_buy_button_row).setVisibility(View.VISIBLE);
					findViewById(R.id.purchase_thank_you_button_row).setVisibility(View.GONE);
				}
			}
		});
	}
}
