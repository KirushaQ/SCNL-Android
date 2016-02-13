package home.com.smarthome;

import android.Manifest;
import android.content.pm.PackageManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import home.com.smarthome.sctp.ScKeynodes;
import home.com.smarthome.sctp.tasks.RequestCommand;
import home.com.smarthome.sctp.SctpClient;
import home.com.smarthome.sctp.tasks.ConnectTask;

import ai.api.AIConfiguration;
import ai.api.AIListener;
import ai.api.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Locale;
import java.util.Map;
import java.util.Set;


public class MainActivity extends AppCompatActivity implements AIListener, TextToSpeech.OnInitListener {

    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private AIService aiService;
    private Button listenButton;
    private TextView resultTextView;
    private TextView responseTextView;
    private ProgressBar listenProgressBar;
    private TextToSpeech tts = null;

    protected SctpClient sctpClient;
    protected ScKeynodes keynodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listenButton = (Button) findViewById(R.id.listenButton);
        resultTextView = (TextView) findViewById(R.id.resultTextView);
        responseTextView = (TextView) findViewById(R.id.responseTextView);
        listenProgressBar = (ProgressBar) findViewById(R.id.listenProgressBar);

        sctpClient = null;
        keynodes = new ScKeynodes();

        final AIConfiguration config = new AIConfiguration("730473034e3c4a0583ad3d52e8336f75",
                "1ba01ac5-f5d6-4056-8c9b-391af54a5594", AIConfiguration.SupportedLanguages.Russian,
                AIConfiguration.RecognitionEngine.System);

        aiService = AIService.getService(this, config);
        aiService.setListener(this);

        requestPermissions();

        tts = new TextToSpeech(this, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onListenButtonClicked(final View view) {
        if (sctpClient == null) {
            listenButton.setEnabled(false);

            ConnectTask Task = new ConnectTask();
            Task.mainActivity = this;
            Task.execute();
        } else {
            aiService.startListening();
            listenButton.setVisibility(View.INVISIBLE);
            listenProgressBar.setVisibility(View.VISIBLE);
        }
    }

    // AI Listener
    public void onResult(final AIResponse response) {
        Result result = response.getResult();

        JsonObject jsonRequest = new JsonObject();
        jsonRequest.addProperty("action", result.getAction());

        JsonObject jsonParams = new JsonObject();

        // Get parameters
        String parametersString = new String();
        if (result.getParameters() != null && !result.getParameters().isEmpty()) {
            for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                jsonParams.addProperty(entry.getKey(), entry.getValue().getAsString());
                parametersString += "( " + entry.getKey() + ": " + entry.getValue().getAsString() + " )";
            }
        }

        jsonRequest.add("params", jsonParams);

        // Show results in TextView.
        resultTextView.setText("Query:" + result.getResolvedQuery() +
                "\nAction: " + result.getAction() +
                "\nParameters: " + parametersString);

        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();

        RequestCommand task = new RequestCommand();
        task.mainActivity = this;
        task.client = sctpClient;
        task.json = gson.toJson(jsonRequest);

        task.execute();

    }

    @Override
    public void onError(final AIError error) {
        resultTextView.setText(error.toString());
        printResponse(new String("Error"));
    }

    @Override
    public void onListeningStarted() {}

    @Override
    public void onListeningCanceled() {}

    @Override
    public void onListeningFinished() {}

    @Override
    public void onAudioLevel(final float level) {}

    public SctpClient getSctpClient() {
        return sctpClient;
    }

    public void printResponse(String message) {
        responseTextView.setText(message);

        listenButton.setVisibility(View.VISIBLE);
        listenProgressBar.setVisibility(View.INVISIBLE);
    }

    public void sctpConnectResult(SctpClient client) {
        if (client != null) {
            sctpClient = client;
            resultTextView.setText("Connected");
            listenButton.setEnabled(true);
            listenButton.setText("Listen");
        } else {
            resultTextView.setText("Can't connect");
        }
    }

    public void sctpOnRequestResult(String message) {
        responseTextView.setText(message);
    }

    public void requestPermissions() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {
            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        PERMISSIONS_REQUEST_RECORD_AUDIO);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_RECORD_AUDIO: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(new Locale("ru"));

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            }
        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    public void speak(String text) {
        if (text != null) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public ScKeynodes getKeynodes() {
        return keynodes;
    }
}
