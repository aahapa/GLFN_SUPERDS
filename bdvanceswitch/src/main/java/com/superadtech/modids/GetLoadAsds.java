package com.superadtech.modids;

import static com.superadtech.modids.MyAdZOne.AD_MOB_APP_ID_Collapsible_Banner_Show;
import static com.superadtech.modids.MyAdZOne.AD_MOB_OpenAd_Fails_INTER_SHOW;
import static com.superadtech.modids.MyAdZOne.AD_MOB_SPLASH_INTER_FORCE;
import static com.superadtech.modids.MyAdZOne.All_Ads_Show;
import static com.superadtech.modids.MyAdZOne.app_BannerPeriority;
import static com.superadtech.modids.MyAdZOne.app_NativeAdCodeType;
import static com.superadtech.modids.MyAdZOne.app_UpdatePackageName;
import static com.superadtech.modids.MyAdZOne.app_onesingle_appid;
import static com.superadtech.modids.MyAdZOne.app_updateAppDialogStatus;
import static com.superadtech.modids.MyAdZOne.app_versionCode;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import java.util.Arrays;

public class GetLoadAsds {

    public static Activity activity;
    public static GetLoadAsds getLoadAsds;
    public static String mode = "";
    public static boolean isShowOpen = false;
    public static AppOpenManager appOpenManager;
    public static Intent intent;
    public static boolean need_internet = false;
    public static boolean is_retry;
    public static Handler refreshHandler;
    public static Runnable runnable;
    public static int vercode;

    public GetLoadAsds(Activity activity1) {
        activity = activity1;
    }

    public static GetLoadAsds getInstance(Activity activity1) {
        activity = activity1;
        if (getLoadAsds == null) {
            getLoadAsds = new GetLoadAsds(activity);
        }
        return getLoadAsds;
    }

    public void sendRequest(String model, Intent intent1, final int cversion) {
        need_internet = !model.isEmpty();

        vercode = cversion;
        final Dialog dialog = new Dialog(activity);
        dialog.setCancelable(false);
        View view = activity.getLayoutInflater().inflate(R.layout.retry_layout, null);
        dialog.setContentView(view);
        final TextView retry_buttton = view.findViewById(R.id.retry_buttton);

        if (!isNetworkAvailable(activity) && need_internet) {
            is_retry = false;
            dialog.show();
        }

        dialog.dismiss();
        refreshHandler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (isNetworkAvailable(activity)) {
                    is_retry = true;
                    retry_buttton.setText("Retry Again");
                } else if (need_internet) {
                    dialog.show();
                    is_retry = false;
                    retry_buttton.setText("Please Connect To Internet");
                }
                refreshHandler.postDelayed(this, 1000);
            }
        };

        intent = intent1;
        try {
            mode = AESSUtils.Logd(model);
        } catch (Exception e) {
        }
        refreshHandler.postDelayed(runnable, 1000);
        retry_buttton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (is_retry) {
                    if (need_internet) {
                        activity.startActivity(new Intent(activity, activity.getClass()));
                        activity.finish();
                    } else {
                        SuccessloadedRedirect();
                    }
                } else {
                    activity.startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                }
            }
        });

        RequestQueue queue = Volley.newRequestQueue(activity);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, mode, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("success") == 1) {
                        MyAdZOne.getInstance(activity).configDatas(jsonObject);
                        if (All_Ads_Show.equalsIgnoreCase("true")) {
                            Allloadeddarts();
                        } else {
                            NoInzilseAllloadeddarts();
                        }
                    } else {
                        Toast.makeText(activity, "Not Found Data!!!", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    if (need_internet) {
                        dialog.dismiss();
                        refreshHandler = new Handler();
                        runnable = new Runnable() {
                            @Override
                            public void run() {
                                if (isNetworkAvailable(activity)) {
                                    is_retry = true;
                                    retry_buttton.setText("Retry Again");
                                } else {
                                    dialog.show();
                                    is_retry = false;
                                    retry_buttton.setText("Please Connect To Internet");
                                }
                                refreshHandler.postDelayed(this, 1000);
                            }
                        };
                    } else {
                        SuccessloadedRedirect();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (need_internet) {
                    dialog.dismiss();
                    refreshHandler = new Handler();
                    runnable = new Runnable() {
                        @Override
                        public void run() {
                            if (isNetworkAvailable(activity)) {
                                is_retry = true;
                                retry_buttton.setText("Retry Again");
                            } else {
                                dialog.show();
                                is_retry = false;
                                retry_buttton.setText("Please Connect To Internet");
                            }
                            refreshHandler.postDelayed(this, 1000);
                        }
                    };
                } else {
                    SuccessloadedRedirect();
                }
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(8000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);

    }

    public void getInlize() {
        Log.e("TAG", "getInlize: ");
        if (All_Ads_Show.equalsIgnoreCase("true")) {
            AudienceNetworkAds.initialize(activity);

            MobileAds.initialize(activity, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });
        }
    }

    public void NoInzilseAllloadeddarts() {
        if (app_onesingle_appid != null) {
            OneSignal.initWithContext(activity);
            OneSignal.setAppId(app_onesingle_appid);
        }

        if (app_updateAppDialogStatus.equalsIgnoreCase("true") && checkUpdate(vercode)) {
            showUpdateDialog(activity, app_UpdatePackageName);
            return;
        }
        SuccessloadedRedirect();
    }

    public void Allloadeddarts() {
        getInlize();

        MyAdZOne.getInstance(activity).loadInterstitialAd(activity);

        if (app_BannerPeriority.equalsIgnoreCase("native")) {
            MyAdZOne.getInstance(activity).Load_NativeBannerAds();
        } else {
            MyAdZOne.getInstance(activity).Load_BannerAds();
        }

        if (app_NativeAdCodeType.equalsIgnoreCase("new")) {
            MyAdZOne.getInstance(activity).Load_NativeNewAds();
        }

        if (app_onesingle_appid != null) {
            OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
            OneSignal.initWithContext(activity);
            OneSignal.setAppId(app_onesingle_appid);
        }

        if (app_updateAppDialogStatus.equalsIgnoreCase("true") && checkUpdate(vercode)) {
            showUpdateDialog(activity, app_UpdatePackageName);
            return;
        }

        isShowOpen = false;
        AppOpenManager.OnAppOpenClose onAppOpenClose = new AppOpenManager.OnAppOpenClose() {
            @Override
            public void OnAppOpenFailToLoad() {
                if (isShowOpen) {
                    isShowOpen = false;

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MyAdZOne.appOpenFailLoadeds(new MyAdZOne.OnAdListner() {
                                @Override
                                public void OnClose() {
                                    if (AD_MOB_SPLASH_INTER_FORCE.equalsIgnoreCase("true")) {
                                        MyAdZOne.getInstance(activity).Show_Next_InterstitialAd(activity, new MyAdZOne.MyCallback() {
                                            public void callbackCall() {
                                                AD_MOB_SPLASH_INTER_FORCE = "false";
                                                SuccessloadedRedirect();
                                            }
                                        }, 0);
                                    } else {
                                        if (AD_MOB_OpenAd_Fails_INTER_SHOW.equalsIgnoreCase("true")) {
                                            MyAdZOne.getInstance(activity).Show_Next_InterstitialAd(activity, new MyAdZOne.MyCallback() {
                                                public void callbackCall() {
                                                    AD_MOB_OpenAd_Fails_INTER_SHOW = "false";
                                                    SuccessloadedRedirect();
                                                }
                                            }, 0);
                                        } else {
                                            SuccessloadedRedirect();
                                        }
                                    }
                                }
                            });
                        }
                    }, 3500);
                }
            }

            @Override
            public void OnAppOpenClose() {
                if (isShowOpen) {
                    isShowOpen = false;
                    SuccessloadedRedirect();
                }
            }
        };
        isShowOpen = true;
        appOpenManager = new AppOpenManager(Applicationclass.getInstant(), onAppOpenClose);
    }

    public void SuccessloadedRedirect() {
        activity.startActivity(intent);
    }

    public static boolean isNetworkAvailable(Activity contnex) {
        ConnectivityManager manager =
                (ConnectivityManager) contnex.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission")
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            // Network is present and connected
            isAvailable = true;
        }
        return isAvailable;
    }

    public static boolean checkUpdate(int mobile_version) {
        if (app_updateAppDialogStatus.equalsIgnoreCase("true")) {
            String str[] = app_versionCode.split(",");

            try {
                for (String s : str) {
                    int server_update_value = Integer.parseInt(s.trim());
                    if (server_update_value > mobile_version) {
                        return true;
                    }
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static void showUpdateDialog(Activity context, String updatePackageName) {
        final Dialog dialog = new Dialog(context);
        dialog.setCancelable(false);
        View view = context.getLayoutInflater().inflate(R.layout.redirect_newapp_dialog, null);
        dialog.setContentView(view);
        TextView update = view.findViewById(R.id.update);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Uri marketUri = Uri.parse("https://play.google.com/store/apps/details?id=" + updatePackageName);
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                    context.startActivity(marketIntent);
                } catch (ActivityNotFoundException ignored1) {
                }
            }
        });
        dialog.create();
        dialog.show();
    }
}
