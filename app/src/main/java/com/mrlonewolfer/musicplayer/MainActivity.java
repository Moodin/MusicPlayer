package com.mrlonewolfer.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    ImageView imgPlay,imgNext,imgPrev;
    TextView txtCurrentTime,txtTotalTime;
    ListView listView;
    MediaPlayer mediaPlayer,myplayer;
    SeekBar seekTime;
    Handler handler=new Handler();
    List<AudioBean> audiolist;
    ArrayList<String> arrayList;
    Uri myUri;
    ArrayAdapter<String> arrayAdapter;
    int lastsongId=1400;

    boolean isStarted=false;
     final static int MY_PERMISSION_REQUEST=1;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;
        seekTime=findViewById(R.id.seekTime);
        imgPlay=findViewById(R.id.imgPlay);
        imgPrev=findViewById(R.id.imgPrev);
        imgNext=findViewById(R.id.imgNext);
        listView=findViewById(R.id.listView);
        txtCurrentTime=findViewById(R.id.txtCurrentTime);
        txtTotalTime=findViewById(R.id.txtTotalTime);
        imgPlay.setOnClickListener(this);
        imgNext.setOnClickListener(this);
        imgPrev.setOnClickListener(this);
        imgPlay.setVisibility(View.GONE);
        imgPrev.setVisibility(View.GONE);
        imgNext.setVisibility(View.GONE);



        seekTime.setOnSeekBarChangeListener(this);

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},MY_PERMISSION_REQUEST);
            } else{
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},MY_PERMISSION_REQUEST);
            }

        }else{
            dostuff();
        }



    }

    private void dostuff() {
        arrayList=new ArrayList<>();
        getMusic();
       myplayer= new MediaPlayer();
       arrayAdapter=new ArrayAdapter<>(context,android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(arrayAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               myUri = Uri.parse(arrayList.get(position));
                myplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                if (isStarted==false ) {

                    if(lastsongId!=position){
                       startMySong(myUri,position);
                    }else {
                        isStarted = true;
                        myplayer.start();
                        imgPlay.setImageResource(android.R.drawable.ic_media_play);
                    }

                }else{
                    if(lastsongId==position) {
                        isStarted = false;
                        myplayer.pause();
                        imgPlay.setImageResource(android.R.drawable.ic_media_play);
                    }else {
                        startMySong(myUri,position);
                    }
                }
            }


        });
    }

    private void startMySong(Uri myUri, int position) {
        myplayer.stop();
        myplayer.reset();
        try {
            myplayer.setDataSource(getApplicationContext(), myUri);
            myplayer.prepare();

        } catch (IOException e) {
            e.printStackTrace();
        }
        myplayer.start();
        imgPlay.setVisibility(View.VISIBLE);
        imgNext.setVisibility(View.VISIBLE);
        imgPrev.setVisibility(View.VISIBLE);
        imgPlay.setImageResource(android.R.drawable.ic_media_pause);
        handler.postDelayed(runnable,1000);
        isStarted=true;
        lastsongId=position;
    }


    public void getMusic(){
        ContentResolver contentResolver=getContentResolver();
        Uri songUri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor= null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            songCursor = contentResolver.query(songUri,null,null,null);

        }
        if(songCursor!=null && songCursor.moveToFirst()){
            int sontTitle=songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist=songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songLocation=songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            do{
                String currentTitle=songCursor.getString(sontTitle);
                String currentArtist=songCursor.getString(songArtist);
                String currentLocation=songCursor.getString(songLocation);
                arrayList.add(currentLocation);

            }while(songCursor.moveToNext());

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions, int[] grantResults) {
        switch(requestCode){
            case MY_PERMISSION_REQUEST:{
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},MY_PERMISSION_REQUEST);
                        Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show();
                        dostuff();
                    }
                }else {
                    Toast.makeText(context, "No Permission Granted", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }   return;
        }
    }

    Runnable runnable=new Runnable() {
        @Override
        public void run() {

//            int totalDuration=mediaPlayer.getDuration();
//            int currentPosition=mediaPlayer.getCurrentPosition();
            int totalDuration=myplayer.getDuration();
            int currentPosition=myplayer.getCurrentPosition();

            txtTotalTime.setText(getTimeFromMillis(totalDuration));
            txtCurrentTime.setText(getTimeFromMillis(currentPosition));

            int progress=(currentPosition*100)/totalDuration;
            seekTime.setProgress(progress);


            handler.postDelayed(runnable,1000);
        }
    };

    private String getTimeFromMillis(int totalDuration) {
        int hour=totalDuration/(1000*60*60);
        int minute=(totalDuration%(1000*60*60))/(1000*60);
        int seconds=((totalDuration%(1000*60*60))%(1000*60))/1000;

        return hour+" : "+minute+" : "+seconds;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.imgPlay){
            if (isStarted==false ) {
              startMySong(myUri,lastsongId);
                }
            else{
                    isStarted = false;
                    myplayer.pause();
                    imgPlay.setImageResource(android.R.drawable.ic_media_play);
                }
            }
            if(v.getId()==R.id.imgNext){
                if(lastsongId<arrayList.size()-1){
                    lastsongId=lastsongId+1;
                    myUri= Uri.parse(arrayList.get(lastsongId));
                    startMySong(myUri,lastsongId);
                }else{
                    lastsongId=0;
                    myUri= Uri.parse(arrayList.get(lastsongId));
                    startMySong(myUri,lastsongId);
                }
            }
            if(v.getId()==R.id.imgPrev){
                if(lastsongId>0){
                    lastsongId=lastsongId-1;
                    myUri= Uri.parse(arrayList.get(lastsongId));
                    startMySong(myUri,lastsongId);
                }else{
                    lastsongId=arrayList.size()-1;
                    myUri= Uri.parse(arrayList.get(lastsongId));
                    startMySong(myUri,lastsongId);
                }

            }
        }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser){
            int totalDuration=myplayer.getDuration();
            int curretnPosition=(progress*totalDuration)/100;
            myplayer.seekTo(curretnPosition);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
