package com.project.virus;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.core.app.NotificationCompat;

import com.amazonaws.amplify.generated.graphql.CreateTodoMutation;
import com.amazonaws.amplify.generated.graphql.ListTodosQuery;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.amazonaws.services.polly.AmazonPollyPresigningClient;
import com.amazonaws.services.polly.model.DescribeVoicesRequest;
import com.amazonaws.services.polly.model.DescribeVoicesResult;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import java.io.Serializable;
import java.util.List;
import java.net.URL;

import javax.annotation.Nonnull;

import type.CreateTodoInput;

public class StartingPoint {

    private AWSAppSyncClient mAWSAppSyncClient;

    private static final String TAG = "PollyDemo";

    //Media player
    MediaPlayer mediaPlayer;
    //Backend resources
    private AmazonPollyPresigningClient client;
    private List<Voice> voices;
    //UI
    Button btn;
    EditText editText;
    ToggleButton toggleButton;
    private int selectedPosition;
    String editString = "";
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mAWSAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();

        Log.d(TAG, "onCreate: ");

        btn = (Button) findViewById(R.id.button);
        editText = (EditText) findViewById(R.id.editText);
        toggleButton = (ToggleButton) findViewById(R.id.toggle);

        editString = editText.getText().toString();


        public void onToggleClicked (View view){
            //toggle is on?
            boolean on = ((ToggleButton) view).isChecked();

            if (editText.getText().toString().equalsIgnoreCase("1")) {
                toggleButton.setTextOff("TOGGLE ON");
                toggleButton.setChecked(true);
            } else if (editText.getText().toString().equalsIgnoreCase("0")) {
                toggleButton.setTextOn("TOGGLE OFF");
                toggleButton.setChecked(false);
            }
        }


        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
        }

        initPollyClient();
        setupNewMediaPlayer();
    }



        public void runMutation() {
            CreateTodoInput createTodoInput = CreateTodoInput.builder().
                    name("Use AppSync").
                    description("Realtime and Offline").
                    build();

            mAWSAppSyncClient.mutate(CreateTodoMutation.builder().input(createTodoInput).build())
                    .enqueue(mutationCallback);
        }

        private GraphQLCall.Callback<CreateTodoMutation.Data> mutationCallback = new GraphQLCall.Callback<CreateTodoMutation.Data>() {
            @Override
            public void onResponse(@Nonnull Response<CreateTodoMutation.Data> response) {
                Log.i("Results", "Added Todo");
            }

            @Override
            public void onFailure(@Nonnull ApolloException e) {
                Log.e("Error", e.toString());
            }
        };

        public void runQuery() {
            mAWSAppSyncClient.query(ListTodosQuery.builder().build())
                    .responseFetcher(AppSyncResponseFetchers.CACHE_AND_NETWORK)
                    .enqueue(todosCallback);
        }

        private GraphQLCall.Callback<ListTodosQuery.Data> todosCallback = new GraphQLCall.Callback<ListTodosQuery.Data>() {
            @Override
            public void onResponse(@Nonnull Response<ListTodosQuery.Data> response) {
                Log.i("Results", response.data().listTodos().toString());
            }

            @Override
            public void onFailure(@Nonnull ApolloException e) {
                Log.e("ERROR", e.toString());
            }
        };



        void initPollyClient() {
            AWSMobileClient.getInstance().initialize(this, new Callback<UserStateDetails>() {
                @Override
                public void onResult(UserStateDetails result) {
                    //Create a client that supports generation of presigned URLs.
                    client = new AmazonPollyPresigningClient(AWSMobileClient.getInstance());
                    Log.d(TAG, "onResult: Created polly pre-signing client");

                    if (voices == null) {
                        //Create describe voices request
                        DescribeVoicesRequest describeVoicesRequest = new DescribeVoicesRequest();

                        try {
                            //Synchronously ask the Polly Service to describe available TTS voices.
                            DescribeVoicesResult describeVoicesResult = client.describeVoices(describeVoicesRequest);


                            //Log a message with a list of available TTS voices.
                            Log.i(TAG, "Avaialbe Polly voices: " + voices);
                        } catch (RuntimeException e) {
                            Log.e(TAG, "Unable to get available voices.", e);
                            return;
                        }
                    }
                }



                @Override
                public void onError(Exception e) {

                }
            });
        }

        public void initChannels(Context context) {
            if (Build.VERSION.SDK_INT < 26) {
                return;
            }
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("default", "Channel name", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Channel_description");
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "default");
}
