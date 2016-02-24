package com.zxing.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksky.tox.R;
import com.google.zxing.WriterException;
import com.zxing.encoding.EncodingHandler;

public class CallActivity extends Activity {

    private TextView resultTextView;
    private EditText qrStrEditText;
    private ImageView qrImgImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        resultTextView = (TextView) this.findViewById(R.id.tv_scan_result);
        qrStrEditText = (EditText) this.findViewById(R.id.et_qr_string);
        qrImgImageView = (ImageView) this.findViewById(R.id.iv_qr_image);

        Button scanBarCodeButton = (Button) this.findViewById(R.id.btn_scan_barcode);
        scanBarCodeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent openCameraIntent = new Intent(CallActivity.this, CaptureActivity.class);
                startActivityForResult(openCameraIntent, 0);
            }
        });

        Button generateQRCodeButton = (Button) this.findViewById(R.id.btn_add_qrcode);
        generateQRCodeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    String contentString = qrStrEditText.getText().toString();
                    if (!contentString.equals("")) {

                        Bitmap qrCodeBitmap = EncodingHandler.createQRCode(contentString, 350);
                        qrImgImageView.setImageBitmap(qrCodeBitmap);
                    } else {
                        Toast.makeText(CallActivity.this, "Text can not be empty", Toast.LENGTH_SHORT).show();
                    }

                } catch (WriterException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");
            resultTextView.setText(scanResult);
        }
    }
}
