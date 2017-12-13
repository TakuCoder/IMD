package imd.thiyagu.taku.Connection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by thiyagu on 12/12/2017.
 */

public class NetworkCheck {



    public boolean isNetworkConnected(Context context)
    {


        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null;



    }

}
