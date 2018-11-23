package com.example.seo.qrcodetestapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {
    private final int REQUEST_PERMISSION_CONTACTS=1;
    private IntentIntegrator qrScan;

    private Button qrscan_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        qrscan_button = (Button)findViewById(R.id.qrscanbutton);

        showContactsPermission(); //런타임 권한체크//

        qrscan_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* QR code Scanner Setting */
                qrScan = new IntentIntegrator(MainActivity.this);
                qrScan.setPrompt("QR Code에 가져다 놓으세요");
                //qrScan.setOrientationLocked(false);
                qrScan.initiateScan();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() == null) {
                Log.d("QR Scan", "ERROR");
            } else { //QR코드, 내용 존재
                try {
                    /* QR 코드 내용*/
                    String temp = result.getContents();

                    Intent intent = new Intent(MainActivity.this, QrReadingView.class);
                    intent.putExtra("QRvalue", temp);

                    startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("QR Scan", "SCAN FAIL");

                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void showContactsPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                showExplanation("요청 권한이 필요합니다.", "[카메라] 접근권한이 있어야지 서비스가 가능합니다.", Manifest.permission.CAMERA, REQUEST_PERMISSION_CONTACTS); //권한필요 설명//
            } else {
                requestPermission(Manifest.permission.CAMERA, REQUEST_PERMISSION_CONTACTS);
            }
        } else {
            Toast.makeText(MainActivity.this, "요청권한이 이미 승인되었습니다. 서비스를 계속합니다.", Toast.LENGTH_SHORT).show();

            //이후 작업진행//
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CONTACTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "권한 승인", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "권한 거부로 정상적인 서비스가 불가합니다. 서비스를 이용하실려면 권한을 승인해야 합니다.", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void showExplanation(String title, String message, final String permission, final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode); //권한 재요청//
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this, new String[]{permissionName}, permissionRequestCode); //권한요청 다이얼로그//
    }
}
