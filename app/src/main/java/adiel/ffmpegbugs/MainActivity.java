package adiel.ffmpegbugs;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import adiel.ffmpegbugs.dialogs.ExplnationDialog;
import adiel.ffmpegbugs.dialogs.SecondExplnationDialog;


public class MainActivity extends AppCompatActivity {

    static final int REQUEST_VIDEO_CAPTURE = 1;

    private Uri _videoFileUri;
    Uri videoUri;
    TextView tvUri;
    TextView tvUriOfCompressedFile;
    TextView tvProgressStatus;
    TextView tvIsSuceeds;
    TextView tvFinishSTatus;
    private Uri vidPathOutput;
    boolean isVideoStillProcessing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_compress_in_main_thread);

            tvUri = (TextView) findViewById(R.id.tvUri);
            tvUriOfCompressedFile = (TextView) findViewById(R.id.tvUriOfCompressedFile);
            tvProgressStatus = (TextView) findViewById(R.id.tvProgressStatus);
            tvIsSuceeds = (TextView) findViewById(R.id.tvIsSuceeds);
            tvFinishSTatus = (TextView) findViewById(R.id.tvFinishSTatus);
        if ( ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
        }else {
            startActivity(new Intent(MainActivity.this, PermissionActivity.class));
        }

    }

    public void compress(View view) {
        String vidPath = videoUri.getPath();
        vidPathOutput = generateTimeStampVideoFileUri("FfOutput");
        if(vidPathOutput!=null) {
            String[] cmd = new String[]{"-i", vidPath, "-b:v", "2.4M", "-bufsize", "2.404M", "-maxrate", "5M", "-preset", "fast", vidPathOutput.getPath()};
            loadBinary();
            exeCompress(cmd);
        }else {
            Toast.makeText(this, "cant generate output path", Toast.LENGTH_SHORT).show();
        }

    }
    private void loadBinary(){
        FFmpeg ffmpeg = FFmpeg.getInstance(getApplicationContext());
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {
                    Log.d("temp","loadBinary onStart");
                }

                @Override
                public void onFailure() {
                    Log.d("temp","loadBinary onFailure");
                }

                @Override
                public void onSuccess() {
                    Log.d("temp","loadBinary onSuccess");
                }

                @Override
                public void onFinish() {
                    Log.d("temp","loadBinary onFinish");
                }
            });
        } catch (FFmpegNotSupportedException e) {
            // Handle if FFmpeg is not supported by device
        }
    }

    private void exeCompress(String[] cmd){
        tvFinishSTatus.setText("");
        tvIsSuceeds.setText("");
        tvProgressStatus.setText("");
        FFmpeg ffmpeg = FFmpeg.getInstance(getApplicationContext());
        try {
            // to execute "ffmpeg -version" command you just need to pass "-version"
            isVideoStillProcessing = true;
            ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                    Log.d("temp","exeCompress onStart");
                    String s = "exeCompress onStart:";
                    Log.d("temp",s);
                }

                @Override
                public void onProgress(String message) {
                    String s = "exeCompress onProgress:" + message;
                    tvProgressStatus.setText(message);
                    Log.d("temp",s);
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(MainActivity.this, "onFailure", Toast.LENGTH_SHORT).show();
                    String s = "exeCompress onFailure:" + message;
                    tvIsSuceeds.setText("failed");
                    Log.d("temp",s);
                }

                @Override
                public void onSuccess(String message) {
                    Toast.makeText(MainActivity.this, "onSuccess", Toast.LENGTH_SHORT).show();
                    Log.d("temp","exeCompress onSuccess:"+message);
                    String s = "exeCompress onSuccess:" + message;
                    tvIsSuceeds.setText("SUCCEED");
                    Log.d("temp",s);
                    //tvUriOfCompressedFile.setText(vidPathOutput.toString()); //TODO VERY VERY STRANGE vidPathOutput BECOME TO BE NULL ????
                }

                @Override
                public void onFinish() {
                    Toast.makeText(MainActivity.this, "onFinish", Toast.LENGTH_SHORT).show();
                    String s = "exeCompress onFinish:" ;
                    tvFinishSTatus.setText("finished");
                    Log.d("temp",s);
                    isVideoStillProcessing = false;
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // Handle if FFmpeg is already running
            e.printStackTrace();
            Log.d("temp","FFmpegCommandAlreadyRunningException");
            String s = "FFmpegCommandAlreadyRunningException";
            Log.d("temp",s);
        }

    }

    public void takeVideo(View view) {
        _videoFileUri = generateTimeStampVideoFileUri("FfInput");
        if (_videoFileUri != null) {
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, _videoFileUri);
            startActivityForResult(intent, REQUEST_VIDEO_CAPTURE);
        }
    }

    private Uri generateTimeStampVideoFileUri(String dirName) {
        Uri photoFileUri = null;
        File outputDir = getVideoDirectory(dirName);

        if(outputDir != null) {
            String timeStamp = new SimpleDateFormat("yyyyMMDD_HHmmss").format(new Date());
            String photoFileName = "VID_" + timeStamp + ".mp4";

            File photoFile = new File(outputDir, photoFileName);
            photoFileUri = Uri.fromFile(photoFile);
        }


        return photoFileUri;
    }

    File getVideoDirectory(String dirName) {
        File outputDir = null;
        String externalStorageState = Environment.getExternalStorageState();
        if(externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
            File pictureDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
            outputDir = new File(pictureDir, dirName);
            if (!outputDir.exists()) {
                if(!outputDir.mkdirs()) {
                    Toast.makeText(this, "Failed to create directory: " + outputDir.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    Log.d("temp","Failed to create directory: " + outputDir.getAbsolutePath());
                    outputDir = null;
                }
            }
        }
        Log.d("temp","outputDir:"+outputDir);
        return outputDir;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            videoUri = intent.getData();
            Log.d("temp","vidPath:"+videoUri.getPath() );
            tvUri.setText(videoUri.toString());
        }
    }


    public void compressVid(View view) {
        loadBinary();
        if(videoUri!=null) {
            String vidPath = videoUri.getPath();
            Uri vidPathOutput = generateTimeStampVideoFileUri("FfOutput");
            String[] cmd = new String[]{"-i", vidPath, "-b:v", "2.4M", "-bufsize", "2.404M", "-maxrate", "5M", "-preset", "fast", vidPathOutput.getPath()};
            exeCompress(cmd);
        }else {
            Toast.makeText(this, "take video first", Toast.LENGTH_SHORT).show();
        }
    }

    public void chechRTl(View view) {
        if(!isVideoStillProcessing) {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }else {
            Toast.makeText(this, "wait till video finish to compress", Toast.LENGTH_SHORT).show();
        }
    }

    public void popupExplanation(View view) {
        ExplnationDialog explnationDialog = new ExplnationDialog();
        explnationDialog.show(getSupportFragmentManager(),"explanation");
    }

    public void popupSecoExplanation(View view) {
        SecondExplnationDialog secondExplnationDialog = new SecondExplnationDialog();
        secondExplnationDialog.show(getSupportFragmentManager(),"secondExplanation");
    }
}
