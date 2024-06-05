package com.citex.twelve_step_recovery;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends Activity {

    static Boolean websiteClicked = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Show the splash screen.
        setContentView(R.layout.activity_splash_screen);
        TextView splashWebsite = findViewById(R.id.text_splash_website);
        splashWebsite.setMovementMethod(LinkMovementMethod.getInstance());
        splashWebsite.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                Uri uri = Uri.parse("http://www.recoverymeetingfinder.com" );
                Intent intent = new Intent( Intent.ACTION_VIEW, uri);
                startActivity(intent);
                websiteClicked = true;
            }
        } );

        // Start application after displaying splash screen.
        new Thread(() -> {
            try {
                Thread.sleep(1500);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(!websiteClicked) {
                startApp();
                finish();
            }

        }).start();
    }

    private void startApp() {
        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
        startActivity(intent);
    }
}