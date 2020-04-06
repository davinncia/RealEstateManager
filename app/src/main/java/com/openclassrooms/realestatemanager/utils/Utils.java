package com.openclassrooms.realestatemanager.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.VisibleForTesting;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Philippe on 21/02/2018.
 */

public class Utils {

    /**
     * Conversion d'un prix d'un bien immobilier (Dollars vers Euros)
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @param dollars amount to convert
     * @return amount in euro
     */
    //Use of BigDecimal for better accuracy
    public static BigDecimal convertDollarToEuro(BigDecimal dollars){
        return dollars.multiply(BigDecimal.valueOf(0.90));
    }

    public static BigDecimal convertEuroToDollar(BigDecimal euros){
        return euros.multiply(BigDecimal.valueOf(1.10));
    }

    /**
     * Conversion de la date d'aujourd'hui en un format plus approprié
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @return date in format: 31/01/2020
     */
    @SuppressWarnings("unused")
    public static String getTodayDate(){
        Date date = new Date();
        return formatTodayDate(date);
    }

    @VisibleForTesting
    public static String formatTodayDate(Date today){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return dateFormat.format(today);
    }

    /**
     * Vérification de la connexion réseau
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @param context current state of application
     * @return boolean activeNetwork
     */
    //This method will be replaced by a networkRepository, exposing a LiveData for dynamic updates
    @SuppressWarnings("unused")
    public static Boolean isInternetAvailable(Context context){
        //WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        //return wifi.isWifiEnabled();

        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        //NetworkInfo activeNetwork = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK);

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
