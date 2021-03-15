package com.example.kolisevkiyat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class BarkodEkrani extends AppCompatActivity {

    EditText etKoliBarkod;
    TextView tvOkutulanBarodSayisi,tvCariAdi ;
    ListView lvKoli ;
    Switch swTestReal;
    ArrayList<String> koliBarkodlari = new ArrayList<>();
    Connection connect;
    String ConnectionResult = "";
    Dto dto;
    final int barkodKarakterSayisi=13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barkod_ekrani);

        etKoliBarkod=findViewById(R.id.etBarkod);
        tvOkutulanBarodSayisi=findViewById(R.id.tvBarkodSayisi);
        lvKoli=findViewById(R.id.lvKoli);
        swTestReal = findViewById(R.id.switch1);
        tvCariAdi=findViewById(R.id.tvCariAdi);

        Intent intent = getIntent();
        dto =  intent.getParcelableExtra("Dto");


        tvCariAdi.setText(getCariAdiByRecNo(dto.CariRecNo));


        swTestReal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String swText = (isChecked) ? "Canlı":"Test";
                swTestReal.setText(swText);
            }
        });

        etKoliBarkod.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    etKoliBarkod.requestFocus();
                    /*
                    String koliBarkod = etKoliBarkod.getText().toString();
                    etKoliBarkod.setText("");

                    if (!isKoliExist(koliBarkodlari,koliBarkod)){
                        if (koliBarkod.length()>0) {
                            if (swTestReal.isChecked()){
                                int sonuc = addProcedure(dto.BelgeNo,koliBarkod);
                                if (sonuc!=1){
                                    Toast toast = Toast.makeText(getApplicationContext(),"Koli Eklenemedi",Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                                else {
                                    //başarılı ekleme
                                    koliBarkodlari.add(koliBarkod);
                                    ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, koliBarkodlari);
                                    lvKoli.setAdapter(arrayAdapter);
                                    tvOkutulanBarodSayisi.setText("Okutulan Koli Sayısı : " + koliBarkodlari.size());
                                }
                            }
                            else{
                                koliBarkodlari.add(koliBarkod);
                                ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, koliBarkodlari);
                                lvKoli.setAdapter(arrayAdapter);
                                tvOkutulanBarodSayisi.setText("Okutulan Koli Sayısı : " + koliBarkodlari.size());
                            }
                        }
                    }
                    */
                }
                return true;
            }
        });

        etKoliBarkod.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length()==barkodKarakterSayisi){
                    String koliBarkod = s.toString();
                    etKoliBarkod.setText("");
                    //etKoliBarkod.requestFocus();
                    if (!isKoliExist(koliBarkodlari,koliBarkod)){
                        if (swTestReal.isChecked()){
                            int sonuc = addProcedure(dto.BelgeNo,koliBarkod);
                            if (sonuc!=1){
                                Toast toast = Toast.makeText(getApplicationContext(),"Koli Eklenemedi",Toast.LENGTH_SHORT);
                                toast.show();
                            }
                            else {
                                //başarılı ekleme
                                koliBarkodlari.add(koliBarkod);
                                ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, koliBarkodlari);
                                lvKoli.setAdapter(arrayAdapter);
                                tvOkutulanBarodSayisi.setText("Okutulan Koli Sayısı : " + koliBarkodlari.size());
                            }
                        }//Canlı
                        else{
                            koliBarkodlari.add(koliBarkod);
                            ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, koliBarkodlari);
                            lvKoli.setAdapter(arrayAdapter);
                            tvOkutulanBarodSayisi.setText("Okutulan Koli Sayısı : " + koliBarkodlari.size());
                        }//Test
                    }//koli daha önce kaydedilmemiş
                }//barkod karakter sayisi istenen değere ulaşmış

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }//onCreate

    public boolean isKoliExist (ArrayList<String> koliler , String koli){
        for (String item:koliler) {
            if (item.equals(koli)){
                return true;
            }
        }
        return false;
    }//isKoliExist
    public void btnTamamlaOnClick(View v){
        koliBarkodlari.clear();
        ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1,koliBarkodlari);
        lvKoli.setAdapter(arrayAdapter);
        tvOkutulanBarodSayisi.setText("Okutulan Koli Sayısı : 0");

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

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
    public String getCariAdiByRecNo(int RecNo){
        try {
            ConnectionHelper connectionHelper = new ConnectionHelper();
            connect = connectionHelper.ConnectionClass();
            if (connect != null)
            {
                String query =String.format("Select CARI_ADI from TBLCARISB where REC_NO = %d",RecNo);
                Statement st = connect.createStatement();
                ResultSet rs = st.executeQuery(query);
                while (rs.next())
                {
                    String cariAdi = rs.getString(1);
                    System.out.println(cariAdi);
                    String trCariAdi = cariAdi.replaceAll("Ý","İ");
                    trCariAdi=trCariAdi.replaceAll("Þ","Ş");
                    trCariAdi=trCariAdi.replaceAll("Ð","Ğ");
                    System.out.println(trCariAdi);
                    return trCariAdi;
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
        return "";
    }
}