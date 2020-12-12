package com.example.app_design;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class Home extends AppCompatActivity {
    private TextView login_account,chinese_tv,taiwanese_tv;
    private Button back_btn,chinese_btn,taiwanese_btn;

    private String account;
    private TextToSpeech talk_object;
    private SpeechRecognizer recognizer;

    private TaiwaneseRecorder taiwaneseRecorder;
    private MediaRecorder mediaRecorder = null;
    private File recordFile;
    private String taiwaneseRecordString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        findObject();
        checkPermissions();
        setLoginInfo();
        buttonClickEvent();
    }

    public void findObject(){
        login_account = findViewById(R.id.account_tv);
        back_btn = findViewById(R.id.back_btn);
        chinese_tv = findViewById(R.id.chinese_tv);
        taiwanese_tv = findViewById(R.id.taiwanese_tv);
        chinese_btn = findViewById(R.id.chinese_btn);
        taiwanese_btn = findViewById(R.id.taiwanese_btn);
    }

    public void checkPermissions(){
        int recordPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        if(recordPermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Home.this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    1);
        }
    }

    public void setLoginInfo(){
        account = getIntent().getStringExtra("user_account");
        login_account.append(String.valueOf(account));

        initTalkObject();
        botSayChinese(login_account.getText().toString());
    }

    // 中文語音合成
    public TextToSpeech initTalkObject(){
        talk_object = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                    // 調整語調
                    talk_object.setPitch((float)1.0);
                    // 調整語速
                    talk_object.setSpeechRate((float)2.0);
                    // 地區
                    Locale locale = Locale.TAIWAN;
                    if(talk_object.isLanguageAvailable(locale) >= TextToSpeech.LANG_AVAILABLE){
                        talk_object.setLanguage(locale);
                    }
                }
            }
        });
        return talk_object;
    }

    public void botSayChinese(final  String s){
        Handler handler = new Handler();
        // (要做什麼事，delay的豪秒數)
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                talk_object.speak(s,TextToSpeech.QUEUE_FLUSH,null);
                while(talk_object.isSpeaking()){}
            }
        },500);
    }

    // 中文語音辨識
    public void startListening(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizer = SpeechRecognizer.createSpeechRecognizer(this);
        recognizer.startListening(intent);
    }

    public void recognize(){
        recognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onResults(Bundle results) {
                ArrayList resList = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String result = resList.get(0).toString();
                chinese_tv.setText(result);
            }

            @Override
            public void onError(int error) {
                Toast.makeText(Home.this,"辨識失敗，請再試一次",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });
    }

    // 結束台語語音辨識
    public void endTaiwaneseRecognition(){
        mediaRecorder.stop();
        mediaRecorder.reset();
        mediaRecorder.release();
        mediaRecorder = null;
        taiwaneseRecorder = new TaiwaneseRecorder();
        try {
            taiwaneseRecordString = taiwaneseRecorder.execute(recordFile.getAbsolutePath()).get();
        } catch (InterruptedException e){
            e.printStackTrace();
        } catch (ExecutionException e){
            e.printStackTrace();
        }
        taiwanese_tv.setText(taiwaneseRecordString);
    }

    public void buttonClickEvent(){
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        chinese_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startListening();
                recognize();
            }
        });

        taiwanese_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 按住
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    Toast.makeText(Home.this,"請按住按鈕",Toast.LENGTH_SHORT).show();
                    try {
                        recordFile = File.createTempFile("record_temp",".m4a", getCacheDir());
                        mediaRecorder = new MediaRecorder();
                        mediaRecorder.setOutputFile(recordFile.getAbsolutePath());
                        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                        mediaRecorder.setAudioEncodingBitRate(326000);
                        mediaRecorder.setAudioSamplingRate(44100);
                        mediaRecorder.setAudioChannels(1);
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    }
                    catch (IOException e){
                        e.printStackTrace();
                    }

                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    // 放開
                    Toast.makeText(Home.this,"處理中，請稍等一下",Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            endTaiwaneseRecognition();
                        }
                    },500);
                }
                return true;
            }
        });
    }

}