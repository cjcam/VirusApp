package com.project.virus.MachineLearning;

import android.content.Context;
import android.speech.tts.Voice;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class PollyActivity {

    private List<Voice> voices;

    PollyActivity(Context context, List<Voice> voices) {
        this.voices = voices;
    }

    public void setVoices(List<Voice> voices) {
        this.voices = voices;

    }


    public int getCount() {
        return voices.size();
    }


    public Object getItem(int position) {
        return voices.get(position);
    }


    public long getItemId(int position) {
        return 0;
    }


}


