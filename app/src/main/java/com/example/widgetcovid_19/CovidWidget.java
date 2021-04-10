package com.example.widgetcovid_19;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.RemoteViews;

import com.example.widgetcovid_19.API.ApiRequestData;
import com.example.widgetcovid_19.API.RetroServer;
import com.example.widgetcovid_19.Model.ResponseModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Implementation of App Widget functionality.
 */

 /**
  * Android Studio 3.5.3
  * Build #AI-191.8026.42.35.6010548, built on November 15, 2019
  * JRE: 1.8.0_202-release-1483-b03 amd64
  * JVM: OpenJDK 64-Bit Server VM by JetBrains s.r.o
  * Windows 10 10.0
  */


public class CovidWidget extends AppWidgetProvider {

    List<ResponseModel> responseModels;
    ResponseModel result;

    static long konfirmasi;
    static int mati;
    static int pulih;
    static String negara;

    final Handler handler = new Handler();  // untuk membuat delay pada eksekusi koding

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.covid_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);


        if(konfirmasi == 0){ // jika data yg didapatkan dari API == 0, maka data tidak di update
            System.out.println("DATA KONFIRMASI == NOL, jadi data pada widget tidak di update");
//            Toast.makeText(context, "Gagal Update Data", Toast.LENGTH_SHORT).show();
            views.setTextViewText(R.id.appwidget_text, widgetText+" : Gagal Update");
        } else {
            System.out.println("SET VIEW TEXT DI EKSEKUSI");
            views.setTextViewText(R.id.confirmed, String.valueOf(konfirmasi));
            views.setTextViewText(R.id.deaths, String.valueOf(mati));
            views.setTextViewText(R.id.recovered, String.valueOf(pulih));

            views.setTextViewText(R.id.appwidget_text, widgetText+" : "+negara);
        }

        // tombol refresh untuk update data
        Intent intentUpdate = new Intent(context, CovidWidget.class);
        intentUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        int[] idArray = new int[]{appWidgetId};
        intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray);

        PendingIntent pendingUpdate = PendingIntent.getBroadcast(context, appWidgetId, intentUpdate, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.tombol, pendingUpdate);                                        //
        //

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them

        getDataCovid();

        for (final int appWidgetId : appWidgetIds) {

            handler.postDelayed(new Runnable() { // delay dibuat untuk mendapatkan data terlebih dahulu pada method getDataCovid sebelum dipush ke tampilan widget.
                @Override
                public void run() {
                    updateAppWidget(context, appWidgetManager, appWidgetId);
                }
            }, 3000);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {

        getDataCovid();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateAppWidget(context, appWidgetManager, appWidgetId);
            }
        }, 3000);

        System.out.println("OPTIONS CHANGED DIEKSEKUSI");

        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    // Mendapatkan Data Covid 19
    public void getDataCovid(){
        ApiRequestData apiRequestData = RetroServer.konekRetrofit().create((ApiRequestData.class));
        Call<List<ResponseModel>> call = apiRequestData.ardGetDataCovid();

        call.enqueue(new Callback<List<ResponseModel>>() {
            @Override
            public void onResponse(Call<List<ResponseModel>> call, Response<List<ResponseModel>> response) {
                responseModels = response.body();
                result = responseModels.get(0); // ini kuncinya untuk mengakses isi dalam LIST<>

                konfirmasi = result.getConfirmed();
                pulih      = result.getRecovered();
                mati       = result.getDeaths();
                negara     = result.getCountryRegion();

                System.out.println("ISI NEGARA : "+ result.getCountryRegion());
            }

            @Override
            public void onFailure(Call<List<ResponseModel>> call, Throwable t) {
                try {
                    System.out.print("RESULT : ON FAILUREEEE throwable: "+t);
                    konfirmasi = 0;
                } catch (Exception e) {
                    konfirmasi = 0;
                    System.out.print("RESULT : ON FAILUREEEE catch: "+t);
                    e.printStackTrace();
                }
            }
        });
    }

}






