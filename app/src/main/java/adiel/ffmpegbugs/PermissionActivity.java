package adiel.ffmpegbugs;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

public class PermissionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_permission);
        if ( ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "good permission !!", Toast.LENGTH_SHORT).show();
            startActivityMain();

        }else {
            Log.d("temp","no permission");
            Toast.makeText(this, "no permission !!", Toast.LENGTH_SHORT).show();
            String[] strings = new String[2];
            strings[0] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
            strings[1] = Manifest.permission.READ_EXTERNAL_STORAGE;

            ActivityCompat.requestPermissions(this,strings,100);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                           // do something
                startActivityMain();
            }
            return;
        }
    }

    private void startActivityMain() {
        finish();
    }
}
