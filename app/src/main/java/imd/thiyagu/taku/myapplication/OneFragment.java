package imd.thiyagu.taku.myapplication;


import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import imd.thiyagu.taku.Connection.NetworkCheck;


/**
 * Created by thiyagu on 12/1/2017.
 */

public class OneFragment extends Fragment {


    NetworkCheck networkCheck = new NetworkCheck();
    private SubsamplingScaleImageView imageView;
    public static ProgressDialog pDialog;

    public OneFragment() {

    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int wantedWidth, int wantedHeight) {
        Bitmap output = Bitmap.createBitmap(wantedWidth, wantedHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Matrix m = new Matrix();
        m.setScale((float) wantedWidth / bitmap.getWidth(), (float) wantedHeight / bitmap.getHeight());
        canvas.drawBitmap(bitmap, m, new Paint());

        return output;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_one, container, false);

        String link = "http://www.imd.gov.in/section/dwr/img/ppi_chn.gif";


        //findRealSize(getActivity());

        imageView = (SubsamplingScaleImageView) rootView.findViewById(R.id.imageView);

        DownloadFileFromURL task = new DownloadFileFromURL();


        if (!networkCheck.isNetworkConnected(getActivity())) {

            Toast.makeText(getActivity(), getResources().getString(R.string.internet_toast), Toast.LENGTH_LONG).show();

        } else {

            task.execute(new String[]{link});

        }


        return rootView;
    }


//  private class GetXMLTask extends AsyncTask<String, Void, Bitmap> {
//        @Override
//        protected Bitmap doInBackground(String... urls) {
//            Bitmap map = null;
//            for (String url : urls) {
//                map = downloadImage(url);
//            }
//
//            map = scaleBitmap(map, 1080, 1920);
//
//
//            return map;
//        }
//
//        // Sets the Bitmap returned by doInBackground
//        @Override
//        protected void onPostExecute(Bitmap result) {
//            imageView.setImage(ImageSource.bitmap(result));
//
//
//
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            progressbar1 = new ProgressDialog (getActivity());
//
//            progressbar1.setCancelable(true);//you can cancel it by pressing back button
//            progressbar1.setMessage("File downloading ...");
//            progressbar1.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//            progressbar1.setProgress(0);//initially progress is 0
//            progressbar1.setMax(100);//sets the maximum value 100
//            progressbar1.show();
//        }
//
//        // Creates Bitmap from InputStream and returns it
//        private Bitmap downloadImage(String url) {
//            Bitmap bitmap = null;
//            InputStream stream = null;
//            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//            bmOptions.inSampleSize = 1;
//
//            try {
//                stream = getHttpConnection(url);
//                bitmap = BitmapFactory.
//                        decodeStream(stream, null, bmOptions);
//                stream.close();
//            } catch (IOException e1) {
//                e1.printStackTrace();
//            }
//            return bitmap;
//        }
//
//        // Makes HttpURLConnection and returns InputStream
//        private InputStream getHttpConnection(String urlString)
//                throws IOException {
//            InputStream stream = null;
//            URL url = new URL(urlString);
//            URLConnection connection = url.openConnection();
//            int length = connection.getContentLength();
//
//
//            try {
//                HttpURLConnection httpConnection = (HttpURLConnection) connection;
//                httpConnection.setRequestMethod("GET");
//                httpConnection.connect();
//
//                if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
//                    stream = httpConnection.getInputStream();
//                }
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//            return stream;
//        }
//    }

    class DownloadFileFromURL extends AsyncTask<String, String, Bitmap>
    {

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Downloading file. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setMax(100);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected Bitmap doInBackground(String... f_url) {
            int count;
            Bitmap bitmap=null;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // this will be useful so that you can show a tipical 0-100% progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = conection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);

                //Output stream
                OutputStream output = new FileOutputStream("/sdcard/downloadedfile.jpg");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress(""+(int)((total*100)/lenghtOfFile));

                    // writing data to file
                   output.write(data, 0, count);
                }

                 //bitmap=  BitmapFactory.decodeStream(input);

                // flushing output
                output.flush();

                // closing streams
               output.close();

                input.close();


            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return bitmap;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
           // imageView.setImage("/sdcard/downloadedfile.jpg");



        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(Bitmap bitmap1) {

           // imageView.setImage(ImageSource.bitmap(bitmap));
            pDialog.dismiss();
            File file = new File("/sdcard/downloadedfile.jpg"); //or any other format supported

            FileInputStream streamIn = null;
            try {
                streamIn = new FileInputStream(file);
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                imageView.setImage(ImageSource.bitmap(bitmap));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


        }


    }

}

