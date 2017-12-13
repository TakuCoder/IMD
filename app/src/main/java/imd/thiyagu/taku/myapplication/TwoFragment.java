package imd.thiyagu.taku.myapplication;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import imd.thiyagu.taku.Connection.NetworkCheck;
import imd.thiyagu.taku.ImageUtils.ImageUtils;

/**
 * Created by thiyagu on 12/1/2017.
 */

public class TwoFragment extends Fragment {
    NetworkCheck networkCheck = new NetworkCheck() ;
    private SubsamplingScaleImageView imageView;
    public TwoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_one, container, false);

        String link = "http://www.imd.gov.in/section/dwr/img/ppz_chn.gif";
       //findRealSize(getActivity());

        imageView = (SubsamplingScaleImageView)rootView.findViewById(R.id.imageView);
        GetXMLTask task = new GetXMLTask();
        if (!networkCheck.isNetworkConnected(getActivity()))
        {

            Toast.makeText(getActivity(),getResources().getString(R.string.internet_toast),Toast.LENGTH_LONG).show();

        }
        else {

            task.execute(new String[] { link });

        }

        return rootView;
    }
    private void findRealSize(Activity activity)
    {
        Point size = new Point();
        Display display = activity.getWindowManager().getDefaultDisplay();

        if (Build.VERSION.SDK_INT >= 17)
            display.getRealSize(size);
        else
            display.getSize(size);

        int realWidth = size.x;
        int realHeight = size.y;

        Log.i("LOG_TAG", "realWidth: " + realWidth + " realHeight: " + realHeight);

    }
    private class GetXMLTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap map = null;
            for (String url : urls) {
                map = downloadImage(url);
            }
            ImageUtils imageUtils = new ImageUtils();

            map = imageUtils.scaleBitmap(map,1080,1920);


            return map;
        }

        // Sets the Bitmap returned by doInBackground
        @Override
        protected void onPostExecute(Bitmap result) {
            imageView.setImage(ImageSource.bitmap(result));
        }

        // Creates Bitmap from InputStream and returns it
        private Bitmap downloadImage(String url) {
            Bitmap bitmap = null;
            InputStream stream = null;
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 1;

            try {
                stream = getHttpConnection(url);
                bitmap = BitmapFactory.
                        decodeStream(stream, null, bmOptions);
                stream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return bitmap;
        }

        // Makes HttpURLConnection and returns InputStream
        private InputStream getHttpConnection(String urlString)
                throws IOException {
            InputStream stream = null;
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();

            try {
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod("GET");
                httpConnection.connect();

                if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    stream = httpConnection.getInputStream();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return stream;
        }
    }



}
