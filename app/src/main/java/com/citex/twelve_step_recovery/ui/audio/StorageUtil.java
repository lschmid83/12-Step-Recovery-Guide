package com.citex.twelve_step_recovery.ui.audio;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.util.Log;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.ICSVParser;
import com.opencsv.RFC4180ParserBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Original example taken from Valdio Veliu on 16-07-11.
 * https://github.com/sitepoint-editors/AudioPlayer
 *
 * Added by Lawrence Schmid:
 *
 * - Loads audio stream data from assets CSV file
 */
public class StorageUtil {

    private final String STORAGE = " com.citex.twelve_step_recovery.ui.audio.STORAGE";
    private SharedPreferences preferences;
    private Context context;

    private static final String TAG = StorageUtil.class.getName();

    public StorageUtil(Context context) {
        this.context = context;
    }

    /**
     * Stores audio CSV String in SharedPreferences.
     * @param audioCsvFilename String containing CSV file.
     */
    public void storeAudioFilename(String audioCsvFilename) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("audioCsvFilename", audioCsvFilename);
        editor.apply();
    }

    /**
     * Reads audio CSV String and creates an ArrayList of Audio objects.
     * @return ArrayList of Audio objects.
     */
    public ArrayList<Audio> loadAudio() {

        // Get audioCsv String from shared preferences.
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        String audioCsvFilename = preferences.getString("audioCsvFilename", null);

       ArrayList<String[]> audioContentsCsv = new ArrayList<>();

        try {
            AssetManager assetManager = context.getAssets();
            InputStream csvInputStream;
            csvInputStream = assetManager.open("audio/" + audioCsvFilename);

            RFC4180ParserBuilder rfc4180ParserBuilder = new RFC4180ParserBuilder();
            ICSVParser rfc4180Parser = rfc4180ParserBuilder.build();
            CSVReader reader = new CSVReaderBuilder(new InputStreamReader(csvInputStream))
                    .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS)
                    .withSkipLines(1)
                    .withCSVParser(rfc4180Parser)
                    .build();
            String[] nextLine;

            while ((nextLine = reader.readNext()) != null) {
                audioContentsCsv.add(nextLine);
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        // Add audioCsv items to Audio object ArrayList.
        ArrayList<Audio> audioList = new ArrayList<Audio>();
        for(int i = 0; i < audioContentsCsv.size(); i++) {

            String[] audioCsv = audioContentsCsv.get(i);

            Audio audio = new Audio();
            audio.setArtist(audioCsv[1]);
            audio.setAlbum(audioCsv[2]);
            audio.setTitle(audioCsv[3]);
            audio.setImage(audioCsv[4]);
            audio.setData(audioCsv[5]);

            audioList.add(audio);
        }

        return audioList;
    }

    /**
     * Stores the current audio index.
     * @param index Audio file index.
     */
    public void storeAudioIndex(int index) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("audioIndex", index);
        editor.apply();
    }

    /**
     * Gets the current audio index.
     * @return Audio index.
     */
    public int loadAudioIndex() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        return preferences.getInt("audioIndex", -1);//return -1 if no data found
    }

    /**
     * Clears the cached audio CSV shared preferences.
     */
    public void clearCachedAudioPlaylist() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }
}