package com.example.kolisevkiyat;



import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;


public class CameraEkrani extends AppCompatActivity {
    private  int CAMERA_REQUEST_CODE =101;
    private CodeScanner mCodeScanner;
    ListView lvKoli;
    TextView tvKoliAdet;
    ArrayList<String> koliBarkodlari = new ArrayList<>();
    Switch swTestReal;
    Dto dto;
    Connection connect;
    String ConnectionResult = "";

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camere_screen);
        final MediaPlayer ses = MediaPlayer.create(this, R.raw.bip);
        swTestReal = findViewById(R.id.switch2);

        Intent intent = getIntent();
        dto =  intent.getParcelableExtra("Dto");
        System.out.println("BelgeNo : "+dto.BelgeNo+"\n Cari Rec No : "+dto.CariRecNo+"\n Evrak No : "+dto.EvrakNo);

        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        lvKoli = (ListView)findViewById(R.id.lvKoli);
        tvKoliAdet=(TextView) findViewById(R.id.tvKoliAdet);


        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (!isKoliExist(koliBarkodlari,result.getText())){


                            if (swTestReal.isChecked()){


                                int sonuc = addProcedure(dto.BelgeNo,result.getText());
                                Toast toast = Toast.makeText(getApplicationContext(),""+sonuc,Toast.LENGTH_LONG);
                                toast.show();
                                if (sonuc!=1){
                                    //Toast toast = Toast.makeText(getApplicationContext(),"Koli Eklenemedi",Toast.LENGTH_LONG);
                                    //toast.show();
                                }
                                else {
                                    //başarılı ekleme
                                    koliBarkodlari.add(result.getText());
                                    ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1,koliBarkodlari);
                                    lvKoli.setAdapter(arrayAdapter);
                                    tvKoliAdet.setText("Okutulan Koli Sayısı : "+koliBarkodlari.size());
                                    ses.start();
                                }
                            }
                            else{
                                koliBarkodlari.add(result.getText());
                                ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1,koliBarkodlari);
                                lvKoli.setAdapter(arrayAdapter);
                                tvKoliAdet.setText("Okutulan Koli Sayısı : "+koliBarkodlari.size());
                                ses.start();
                            }
                        }
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });

        swTestReal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String swText = (isChecked) ? "Canlı":"Test";
                swTestReal.setText(swText);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    public boolean isKoliExist (ArrayList<String> koliler , String koli){
        for (String item:koliler) {
            if (item.equals(koli)){
                return true;
            }
        }
        return false;
    }

    public int addProcedure(String belgeNo,String koliEtiket){
        try {
            ConnectionHelper connectionHelper = new ConnectionHelper();
            connect = connectionHelper.ConnectionClass();
            if (connect != null)
            {
                String query =String.format(" exec dbo.ZZ_PrgProc_TekIrsToFatKalem '%s','%s'",belgeNo,koliEtiket);
                Statement st = connect.createStatement();
                ResultSet rs = st.executeQuery(query);
                while (rs.next())
                {
                    return rs.getInt(1);
                }
            }
            else
            {
                ConnectionResult="Check Connection";
                Toast toast=Toast. makeText(getApplicationContext(),ConnectionResult.toString(),Toast. LENGTH_SHORT);
            }
        } catch (Exception ex) {
            Toast toast=Toast. makeText(getApplicationContext(),ex.toString(),Toast. LENGTH_SHORT);
        }
        return 5;
    }

    public void Tamamla (View v){
        koliBarkodlari.clear();
        ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1,koliBarkodlari);
        lvKoli.setAdapter(arrayAdapter);
        tvKoliAdet.setText("Okutulan Koli Sayısı : 0");
    }

}
