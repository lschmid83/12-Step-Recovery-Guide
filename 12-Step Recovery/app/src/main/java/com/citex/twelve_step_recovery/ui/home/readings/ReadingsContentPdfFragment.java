package com.citex.twelve_step_recovery.ui.home.readings;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import com.citex.twelve_step_recovery.MainActivity;
import com.citex.twelve_step_recovery.R;

import com.citex.twelve_step_recovery.databinding.FragmentReadingsContentPdfBinding;
import com.citex.twelve_step_recovery.exceptions.ResourceUnavailableException;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;

import androidx.fragment.app.Fragment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class ReadingsContentPdfFragment extends Fragment {

    private View mainView;
    private String fileName;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        String url = getArguments().getString("indexFilename");
        fileName = url.substring(url.lastIndexOf('/') + 1);

        // Setup ActionBar.
        if(getActivity() != null) {
            ActionBar mActionBar = ((MainActivity) getActivity()).getSupportActionBar();
            if(mActionBar != null) {
                mActionBar.setIcon(R.drawable.ic_menu_book_24dp);
                mActionBar.setDisplayUseLogoEnabled(true);
                mActionBar.setDisplayShowHomeEnabled(true);
                mActionBar.setDisplayShowCustomEnabled(false);
                mActionBar.setTitle("  " + getArguments().getString("title"));
            }
        }

        // Setup View Binding.
        FragmentReadingsContentPdfBinding binding = FragmentReadingsContentPdfBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        container.removeAllViews();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mainView = view;
        downloadFile();
    }

    private void downloadFile() {

        // Show progress bar.
        mainView.findViewById(R.id.progress_layout).setVisibility(View.VISIBLE);

        // Start download task.
        DownloadFileTask task = new DownloadFileTask(
                this.getContext(),
                this,
                getArguments().getString("indexFilename"),
                fileName);
        task.startTask();
    }

    public void publishProgress(int progress) {

        // Display download progress.
        TextView progressText = mainView.findViewById(R.id.text_progress_description);
        progressText.setText("Opening PDF " + progress + "%");
    }

    public void onError(String message) {

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {

                // Display error message.
                mainView.findViewById(R.id.progress_layout).setVisibility(View.GONE);
                TextView textError;
                textError = mainView.findViewById(R.id.text_error);
                textError.setText(message);
                textError.setVisibility(View.VISIBLE);

            }
        });
    }

    public void onFileDownloaded() {

        // Hide progress bar.
        mainView.findViewById(R.id.progress_layout).setVisibility(View.GONE);

        // Store file to external storage.
        File file = new File(mainView.getContext().getCacheDir() + "/" + fileName);

        if (file.exists()) {

            // Show PDF viewer.
            mainView.findViewById(R.id.pdfView_reading_content).setVisibility(View.VISIBLE);

            // Open file in PDF viewer.
            PDFView pdfView;
            pdfView = mainView.findViewById(R.id.pdfView_reading_content);
            pdfView.fromFile(file)
                    .onError(new OnErrorListener() {
                        @Override
                        public void onError(Throwable t) {

                            Throwable a = t;
                        }
                    })
                    .pages(getArguments().getIntArray("pages"))
                    .defaultPage(getArguments().getInt("pageOffset"))
                    .pageFitPolicy(FitPolicy.WIDTH) // mode to fit pages in the view
                    .fitEachPage(true) // fit each page to the view, else smaller pages are scaled relative to largest page.
                    .pageSnap(true) // snap pages to screen boundaries
                    .load();



        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}

class DownloadFileTask {

    public static final String TAG = "DownloadFileTask";

    private Context context;
    private ReadingsContentPdfFragment readingsContentPdfFragment;
    private GetTask contentTask;
    private String url;
    private String fileName;


    public DownloadFileTask(Context context, ReadingsContentPdfFragment readingsContentContext, String url, String fileName) {
        this.context = context;
        this.readingsContentPdfFragment = readingsContentContext;
        this.url = url;
        this.fileName = fileName;
    }

    public void startTask() {
        doRequest();
    }

    private void doRequest() {
        contentTask = new GetTask();
        contentTask.execute();
    }

    private class GetTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... arg0) {

            // Check if file has already been downloaded and cached
            File file = new File(context.getCacheDir() + "/" + fileName);
            if (file.exists())
                return "downloaded_file";

            int count;
            try {
                Log.d(TAG, "url = " + url);
                URL _url = new URL(url);
                HttpURLConnection connection = (HttpURLConnection)_url.openConnection();
                connection.connect();

                int fileLength = connection.getContentLength();

                InputStream input = new BufferedInputStream(_url.openStream(),
                        8192);
                OutputStream output = new FileOutputStream(
                        context.getCacheDir() + "/" + fileName);
                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {

                    total += count;
                    readingsContentPdfFragment.publishProgress((int) (total * 100 / fileLength));

                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
            }
            catch (FileNotFoundException e) {
                readingsContentPdfFragment.onError(context.getResources().getString(R.string.resource_unavailable));
                return null;
            }
            catch (Exception e) {
                readingsContentPdfFragment.onError(context.getResources().getString(R.string.no_network));
                return null;
            }
            return "downloaded_file";
        }

        protected void onPostExecute(String data) {
            if(data == "downloaded_file")
                readingsContentPdfFragment.onFileDownloaded();
        }
    }
}
