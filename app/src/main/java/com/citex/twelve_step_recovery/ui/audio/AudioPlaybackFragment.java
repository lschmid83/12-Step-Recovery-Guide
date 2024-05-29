package com.citex.twelve_step_recovery.ui.audio;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.citex.twelve_step_recovery.MainActivity;
import com.citex.twelve_step_recovery.R;
import com.citex.twelve_step_recovery.database.DbHelper;
import com.citex.twelve_step_recovery.databinding.FragmentAudioPlaybackBinding;
import com.google.android.gms.common.util.IOUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.ICSVParser;
import com.opencsv.RFC4180ParserBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class AudioPlaybackFragment extends Fragment {

    public static final String Broadcast_PLAY_NEW_AUDIO = "com.citex.twelve_step_recovery.ui.audio.PlayNewAudio";

    public static MediaPlayerService player;
    boolean serviceBound = false;

    private static final String TAG = AudioContentsFragment.class.getName();

    /**
     * Creates and returns the view hierarchy associated with the fragment.
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Setup ActionBar.
        if(getActivity() != null) {
            ActionBar mActionBar = ((MainActivity) getActivity()).getSupportActionBar();
            if(mActionBar != null) {
                mActionBar.setIcon(R.drawable.ic_headphones_24dp);
                mActionBar.setDisplayUseLogoEnabled(true);
                mActionBar.setDisplayShowHomeEnabled(true);
                mActionBar.setDisplayShowCustomEnabled(false);
                if(getArguments() != null)
                    mActionBar.setTitle("  " + getArguments().getString("audioTitle"));
            }
        }

        // Setup View Binding.
        FragmentAudioPlaybackBinding binding = FragmentAudioPlaybackBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        container.removeAllViews();

        return root;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * Called immediately after onCreateView() has returned, but before any saved state has been
     * restored in to the view.
     * @param view The View returned by onCreateView().
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *                           saved state as given here. This value may be null.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        if(getActivity() != null) {
            FloatingActionButton actionButtonPlay = view.findViewById(R.id.action_button_play);
            actionButtonPlay.setOnClickListener(view13 -> {
                playAudio(0);
            });
        }
    }

    /**
     * Plays an audio file.
     * @param audioIndex Index of audio file.
     */
    private void playAudio(int audioIndex) {


        StorageUtil storage = new StorageUtil(getContext().getApplicationContext());

        // Check is service is active.
        if (!serviceBound) {

            // Store CSV audio list to SharedPreferences.
            storage.storeAudioFilename(getArguments().getString("audioContentsFilename"));
            storage.storeAudioIndex(audioIndex);

            Intent playerIntent = new Intent(getContext(), MediaPlayerService.class);
            getContext().startService(playerIntent);
            getContext().bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {

            // Store the new audio index to SharedPreferences.
            storage.storeAudioIndex(audioIndex);

            // Service is active.
            // Send a broadcast to the service -> PLAY_NEW_AUDIO.
            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            getContext().sendBroadcast(broadcastIntent);
        }
    }

    /**
     * Binding this Client to the AudioPlayer Service.
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };
}