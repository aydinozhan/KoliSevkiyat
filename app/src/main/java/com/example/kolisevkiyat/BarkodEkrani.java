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
import java.util.ListIterator;

public class BarkodEkrani extends AppCompatActivity {

    final int barkodKarakterSayisi = 13;
    EditText etKoliBarkod;
    TextView tvOkutulanBarodSayisi, tvCariAdi;
    ListView lvKoli;
    Switch swTestReal;
    ArrayList<String> koliBarkodlari = new ArrayList<>();
    ArrayList<String> paletBarkodlari = new ArrayList<>();
    Connection connect;
    String ConnectionResult = "";
    Dto dto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barkod_ekrani);

        etKoliBarkod = findViewById(R.id.etBarkod);
        tvOkutulanBarodSayisi = findViewById(R.id.tvBarkodSayisi);
        lvKoli = findViewById(R.id.lvKoli);
        swTestReal = findViewById(R.id.switch1);
        tvCariAdi = findViewById(R.id.tvCariAdi);

        Intent intent = getIntent();
        dto = intent.getParcelableExtra("Dto");
        tvCariAdi.setText(getCariAdiByRecNo(dto.CariRecNo));


        swTestReal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String swText = (isChecked) ? "Canlı" : "Test";
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
                if (s.toString().length() == barkodKarakterSayisi) {
                    String barkod = s.toString();
                    etKoliBarkod.setText("");

                    if (swTestReal.isChecked()) {

                        if (barkod.startsWith("P")) {
                            if (!isPaletExist(paletBarkodlari, barkod)) {
                                for (String koli : getKoliInPalet(barkod)) {
                                    koliEkle(koli);
                                }
                                paletBarkodlari.add(barkod);
                                ListeGuncelle(koliBarkodlari);
                            }
                        }//palet ekleme işlemi

                        else if (barkod.startsWith("K")) {
                            if (!isKoliExist(koliBarkodlari, barkod)) {
                                koliEkle(barkod);
                            }
                            ListeGuncelle(koliBarkodlari);
                        }//tek koli ekleme işlemi

                    }//Canlı
                    else {
                        koliBarkodlari.add(barkod);
                        ListeGuncelle(koliBarkodlari);
                    }//Test
                }//barkod karakter sayisi istenen değere ulaşmış
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        System.out.println("BarkodEkrani onCreate");
    }//onCreate

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("BarkodEkrani OnPause");
    }
    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("BarkodEkrani onStop");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("BarkodEkrani onDestroy");
    }
    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("BarkodEkrani onStart");
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("BarkodEkrani OnRestrart");
    }
    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("BarkodEkrani onResume");
    }

    public boolean isKoliExist(ArrayList<String> koliler, String koli) {
        for (String item : koliler) {
            if (item.equals(koli)) {
                return true;
            }
        }
        return false;
    }//isKoliExist

    public void btnTamamlaOnClick(View v) {

        koliBarkodlari.clear();
        ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, koliBarkodlari);
        lvKoli.setAdapter(arrayAdapter);
        tvOkutulanBarodSayisi.setText("Okutulan Koli Sayısı : 0");

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

    public int addProcedure(String belgeNo, String koliEtiket) {
        try {
            ConnectionHelper connectionHelper = new ConnectionHelper();
            connect = connectionHelper.ConnectionClass();
            if (connect != null) {
                String query = String.format(" exec dbo.ZZ_PrgProc_TekIrsToFatKalem '%s','%s'", belgeNo, koliEtiket);
                Statement st = connect.createStatement();
                ResultSet rs = st.executeQuery(query);
                while (rs.next()) {
                    return rs.getInt(1);
                }
            } else {
                ConnectionResult = "Check Connection";
                Toast toast = Toast.makeText(getApplicationContext(), ConnectionResult.toString(), Toast.LENGTH_SHORT);
            }
        } catch (Exception ex) {
            Toast toast = Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT);
        }
        return 5;
    }

    public String getCariAdiByRecNo(int RecNo) {
        try {
            ConnectionHelper connectionHelper = new ConnectionHelper();
            connect = connectionHelper.ConnectionClass();
            if (connect != null) {
                String query = String.format("Select CARI_ADI from TBLCARISB where REC_NO = %d", RecNo);
                Statement st = connect.createStatement();
                ResultSet rs = st.executeQuery(query);
                while (rs.next()) {
                    String cariAdi = rs.getString(1);
                    System.out.println(cariAdi);
                    String trCariAdi = cariAdi.replaceAll("Ý", "İ");
                    trCariAdi = trCariAdi.replaceAll("Þ", "Ş");
                    trCariAdi = trCariAdi.replaceAll("Ð", "Ğ");
                    System.out.println(trCariAdi);
                    return trCariAdi;
                }
            } else {
                ConnectionResult = "Check Connection";
                Toast toast = Toast.makeText(getApplicationContext(), ConnectionResult.toString(), Toast.LENGTH_SHORT);
            }
        } catch (Exception ex) {
            Toast toast = Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT);
        }
        return "";
    }

    public void koliEkle(String barcode) {
        int sonuc = addProcedure(dto.BelgeNo, barcode);
        if (sonuc != 1) {
            Toast toast = Toast.makeText(getApplicationContext(), "Koli Eklenemedi", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            //başarılı ekleme
            koliBarkodlari.add(barcode);
        }
    }

    public ArrayList<String> getKoliInPalet(String paletBarkod) {
        ArrayList<String> koliler = new ArrayList<>();
        try {
            ConnectionHelper connectionHelper = new ConnectionHelper();
            connect = connectionHelper.ConnectionClass();
            if (connect != null) {
                String query = String.format("SELECT KOLI_ETIKET  FROM TBLTEKBOBINPALETHR WHERE PALET_ETIKET ='%s'", paletBarkod);
                Statement st = connect.createStatement();
                ResultSet rs = st.executeQuery(query);
                while (rs.next()) {
                    koliler.add(rs.getString(1));
                }
                return koliler;
            } else {
                ConnectionResult = "Check Connection";
                Toast toast = Toast.makeText(getApplicationContext(), ConnectionResult.toString(), Toast.LENGTH_SHORT);
            }
        } catch (Exception ex) {
            Toast toast = Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT);
        }
        return koliler;
    }

    public boolean isPaletExist(ArrayList<String> paletler, String paletBarkod) {
        for (String palet : paletler) {
            if (palet.equals(paletBarkod)) {
                return true;
            }
        }
        return false;
    }

    public void ListeGuncelle(ArrayList<String> barcodes){
        ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, barcodes);
        lvKoli.setAdapter(arrayAdapter);
        tvOkutulanBarodSayisi.setText("Okutulan Koli Sayısı : " + barcodes.size());
    }
}