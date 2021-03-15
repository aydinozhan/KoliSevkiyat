package com.example.kolisevkiyat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    Connection connect;
    String ConnectionResult = "";
    TextView evrakNo, belgeNo, plasiyer, plasiyerKod;
    Spinner cariKodlar;
    androidx.appcompat.widget.SwitchCompat sw;
    ListView rvSonIslemler;

    public ArrayList<String> cariAdlari = new ArrayList<String>();
    public ArrayList<Cari> Cariler = new ArrayList<Cari>();
    public int REC_NO = 0;
    final String ekran = "barkod";

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);

        belgeNo = findViewById(R.id.tvBelgeNo);
        evrakNo = findViewById(R.id.tvEvrakNo);
        cariKodlar = findViewById(R.id.spCari);
        plasiyer = findViewById(R.id.tvPlasiyer);
        plasiyerKod = findViewById(R.id.tvPlasiyerKod);
        sw = findViewById(R.id.switch3);
        rvSonIslemler = findViewById(R.id.rvSonIslemler);

        evrakNo.setText("");
        belgeNo.setText("");
        plasiyer.setText("");
        plasiyerKod.setText("");

        FillListSonIslemler(getSonIslemler());
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String switchText = (isChecked) ? "Online" : "Offline";
                sw.setText(switchText);

            }
        });
        cariKodlar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                plasiyer.setText(getPlasiyerName(Cariler.get(position).CARI_PLASIYER_KODU));
                plasiyerKod.setText(Cariler.get(position).CARI_PLASIYER_KODU);
                REC_NO = Cariler.get(position).REC_NO;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("MainActitivy OnPause");

    }

    public String getPlasiyerName(String plasiyerKod) {
        try {
            ConnectionHelper connectionHelper = new ConnectionHelper();
            connect = connectionHelper.ConnectionClass();
            if (connect != null) {
                String query = String.format("select PLASIYER_ADI from TBLPLASIYERSB WHERE PLASIYER_KODU ='%s'", plasiyerKod);
                Statement st = connect.createStatement();
                ResultSet rs = st.executeQuery(query);
                while (rs.next()) {
                    return rs.getString(1);
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

    public ArrayList<Cari> getCariler() {
        try {
            ConnectionHelper connectionHelper = new ConnectionHelper();
            connect = connectionHelper.ConnectionClass();
            if (connect != null) {
                if (Cariler.size() > 0)
                    Cariler.clear();

                String query = "Select REC_NO,CARI_ADI,CARI_PLASIYER_KODU from TBLCARISB where CARI_TIPI = 'A' order by CARI_ADI";
                Statement st = connect.createStatement();
                ResultSet rs = st.executeQuery(query);
                while (rs.next()) {
                    Cari cari = new Cari(rs.getInt(1), rs.getString(2), rs.getString(3));
                    Cariler.add(cari);
                }
                return Cariler;
            } else {
                ConnectionResult = "Check Connection";
                Toast toast = Toast.makeText(getApplicationContext(), ConnectionResult.toString(), Toast.LENGTH_SHORT);
            }
        } catch (Exception ex) {
            Toast toast = Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT);
        }
        return Cariler;
    }

    public String getSonBelgeNo() {
        try {
            ConnectionHelper connectionHelper = new ConnectionHelper();
            connect = connectionHelper.ConnectionClass();
            if (connect != null) {
                String query = " SELECT MAS_SONNO FROM [TBLNUMMAS] WITH (NOLOCK) WHERE MAS_MODUL = 'F03' AND MAS_SERI = '21MF03000140'";
                Statement st = connect.createStatement();
                ResultSet rs = st.executeQuery(query);
                while (rs.next()) {
                    return rs.getString(1);
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

    public String getSonIrsNo() {
        try {
            ConnectionHelper connectionHelper = new ConnectionHelper();
            connect = connectionHelper.ConnectionClass();
            if (connect != null) {

                String query = "SELECT FATIRS_SONNO FROM [TBLFATBELGENUMSB] WITH (NOLOCK) WHERE FATIRSIP = 3 AND SUBE_KODU = 0";
                Statement st = connect.createStatement();
                ResultSet rs = st.executeQuery(query);
                while (rs.next()) {
                    return rs.getString(1);
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

    public void addFATSB(String values) {
        try {
            ConnectionHelper connectionHelper = new ConnectionHelper();
            connect = connectionHelper.ConnectionClass();
            if (connect != null) {
                String query = "INSERT INTO TBLFATSB" +
                        "(REC_DATE,REC_UPDATE,REC_CHANGED,SUBE_KODU,BELGE_NO,EVRAK_NO,BELGE_TIPI,CARI_KODU_RECID,TARIH,DETAY_TIP,KDV_DAHILMI,ISKONTO_MATRAHTAN,DOVIZ_TIPI,BT_STRING_5,BELGE_PLASIYER_KODU) " +
                        values;
                System.out.println(query);
                Statement st = connect.createStatement();
                st.executeUpdate(query);
            } else {
                ConnectionResult = "Check Connection";
                Toast toast = Toast.makeText(getApplicationContext(), ConnectionResult, Toast.LENGTH_SHORT);
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    public void updateBelgeNo(String sonNo) {
        try {
            ConnectionHelper connectionHelper = new ConnectionHelper();
            connect = connectionHelper.ConnectionClass();
            if (connect != null) {
                //String query = "update TBLNUMMAS set MAS_SONNO ='' where MAS_MODUL = 'F03' AND MAS_SERI = '21MF03000140'";
                String query = String.format("update TBLNUMMAS set MAS_SONNO = '%s' where MAS_MODUL = 'F03' AND MAS_SERI = '21MF03000140'", sonNo);
                System.out.println(query);
                Statement st = connect.createStatement();
                st.executeUpdate(query);
            } else {
                ConnectionResult = "Check Connection";
                Toast toast = Toast.makeText(getApplicationContext(), ConnectionResult, Toast.LENGTH_SHORT);
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    public void updateEvrakNo(String sonEvrakNo) {
        try {
            ConnectionHelper connectionHelper = new ConnectionHelper();
            connect = connectionHelper.ConnectionClass();
            if (connect != null) {
                String query = String.format("update TBLFATBELGENUMSB set FATIRS_SONNO ='%s',FATIRSIP_NO='%s' WHERE FATIRSIP = 3 AND SUBE_KODU = 0", sonEvrakNo, sonEvrakNo);
                System.out.println(query);
                Statement st = connect.createStatement();
                st.executeUpdate(query);
            } else {
                ConnectionResult = "Check Connection";
                Toast toast = Toast.makeText(getApplicationContext(), ConnectionResult, Toast.LENGTH_SHORT);
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    public void createBelgeNo(View v) {
        if (sw.isChecked()) {
            String sonBelgeNo = getSonBelgeNo();
            if (sonBelgeNo != "") {
                int sonSayi = Integer.parseInt(sonBelgeNo.substring(sonBelgeNo.length() - 4));
                int yeniSonSayi = sonSayi + 1;
                String sayisizBelgeNo = sonBelgeNo.substring(0, sonBelgeNo.length() - 4);
                String sonSayiStr = "" + yeniSonSayi;
                while (sonSayiStr.length() != 4) {
                    sonSayiStr = "0" + sonSayiStr;
                }
                String yeniSonBelgeNo = sayisizBelgeNo + sonSayiStr;
                belgeNo.setText(yeniSonBelgeNo);
            }
            String sonIrsNo = getSonIrsNo();
            if (sonIrsNo != "") {
                int irsNo = Integer.parseInt(sonIrsNo);
                int yeniIrsNo = irsNo + 1;
                String IrsNoStr = "" + yeniIrsNo;
                while (IrsNoStr.length() != 10) {
                    IrsNoStr = "0" + IrsNoStr;
                }
                evrakNo.setText(IrsNoStr);
            }

            for (Cari cari : getCariler()) {
                String trCariAdi = cari.CARI_ADI.replaceAll("Ý", "İ");
                trCariAdi = trCariAdi.replaceAll("Þ", "Ş");
                trCariAdi = trCariAdi.replaceAll("Ð", "Ğ");

                cariAdlari.add(trCariAdi);
            }

            ArrayAdapter<String> cariAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cariAdlari);
            cariAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            cariKodlar.setAdapter(cariAdapter);
        } else
            Toast.makeText(getApplicationContext(), "Offline Çalışma", Toast.LENGTH_SHORT).show();
    }

    public void barcodeEkranaGit(View v) {
        if (sw.isChecked()) {
            if (belgeNo.getText() != null && evrakNo.getText() != null && plasiyer.getText() != null && REC_NO != 0) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                // String rec_date =formatter.format(date);
                String rec_date = "dbo.PrgfnDateTimeToDate(GETDATE())";
                String rec_update = rec_date;
                String rec_changed = "C";
                int sube_kodu = 0;
                String belge_no = belgeNo.getText().toString();
                String evrak_no = evrakNo.getText().toString();
                int belge_tipi = 3;
                int cari_kodu_recid = REC_NO;
                String Tarih = rec_date;
                int detay_tip = 1;
                String kdv_dahilmi = "H";
                String iskonto_matrahtan = "H";
                int doviz_tipi = 0;
                String bt_string_5 = "KutuCikis";
                String belge_plasiyer = ((plasiyerKod.getText() == null) ? "" : plasiyerKod.getText().toString());
                String query = String.format("values(%s,%s,'%s',%d,'%s','%s',%d,%d,%s,%d,'%s','%s',%d,'%s','%s')",
                        rec_date, rec_update, rec_changed, sube_kodu, belge_no, evrak_no, belge_tipi, cari_kodu_recid, Tarih, detay_tip, kdv_dahilmi, iskonto_matrahtan, doviz_tipi, bt_string_5, belge_plasiyer);


                updateEvrakNo(evrakNo.getText().toString());
                updateBelgeNo(belgeNo.getText().toString());
                addFATSB(query);


                Intent intent = new Intent(this, BarkodEkrani.class);
                Dto dto = new Dto(belgeNo.getText().toString(), evrakNo.getText().toString(), REC_NO);
                intent.putExtra("Dto", dto);
                startActivity(intent);
            } else
                Toast.makeText(getApplicationContext(), "Belge Oluşturulmadı", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, BarkodEkrani.class);
            Dto dto = new Dto("BelgeNo", "EvrakNo", 0);
            intent.putExtra("Dto", dto);
            startActivity(intent);
        }
    }

    public void cameraEkranaGit(View v) {
        if (sw.isChecked()) {
            if (belgeNo.getText() != null && evrakNo.getText() != null && plasiyer.getText() != null && REC_NO != 0) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                // String rec_date =formatter.format(date);
                String rec_date = "dbo.PrgfnDateTimeToDate(GETDATE())";
                String rec_update = rec_date;
                String rec_changed = "C";
                int sube_kodu = 0;
                String belge_no = belgeNo.getText().toString();
                String evrak_no = evrakNo.getText().toString();
                int belge_tipi = 3;
                int cari_kodu_recid = REC_NO;
                String Tarih = rec_date;
                int detay_tip = 1;
                String kdv_dahilmi = "H";
                String iskonto_matrahtan = "H";
                int doviz_tipi = 0;
                String bt_string_5 = "KutuCikis";
                String belge_plasiyer = ((plasiyerKod.getText() == null) ? "" : plasiyerKod.getText().toString());
                String query = String.format("values(%s,%s,'%s',%d,'%s','%s',%d,%d,%s,%d,'%s','%s',%d,'%s','%s')",
                        rec_date, rec_update, rec_changed, sube_kodu, belge_no, evrak_no, belge_tipi, cari_kodu_recid, Tarih, detay_tip, kdv_dahilmi, iskonto_matrahtan, doviz_tipi, bt_string_5, belge_plasiyer);


                updateEvrakNo(evrakNo.getText().toString());
                updateBelgeNo(belgeNo.getText().toString());
                addFATSB(query);


                Intent intent = new Intent(this, CameraEkrani.class);
                Dto dto = new Dto(belgeNo.getText().toString(), evrakNo.getText().toString(), REC_NO);
                intent.putExtra("Dto", dto);
                startActivity(intent);
            } else
                Toast.makeText(getApplicationContext(), "Belge Oluşturulmadı", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, CameraEkrani.class);
            Dto dto = new Dto("BelgeNo", "EvrakNo", 0);
            intent.putExtra("Dto", dto);
            startActivity(intent);
        }
    }

    public ArrayList<SonIslem> getSonIslemler() {
        ArrayList<SonIslem> sonIslemler = new ArrayList<SonIslem>();
        try {
            ConnectionHelper connectionHelper = new ConnectionHelper();
            connect = connectionHelper.ConnectionClass();
            if (connect != null) {
                String query = "SELECT TOP 5  F.BELGE_NO,F.EVRAK_NO,C.REC_NO,C.CARI_ADI from TBLCARISB AS C INNER JOIN TBLFATSB AS F ON C.REC_NO=F.CARI_KODU_RECID ORDER BY F.REC_NO DESC";
                Statement st = connect.createStatement();
                ResultSet rs = st.executeQuery(query);

                while (rs.next()) {
                    SonIslem sonIslem = new SonIslem(rs.getString(1), rs.getString(2), rs.getInt(3), rs.getString(4));
                    sonIslemler.add(sonIslem);
                }
                return sonIslemler;
            } else {
                ConnectionResult = "Check Connection";
                Toast toast = Toast.makeText(getApplicationContext(), ConnectionResult.toString(), Toast.LENGTH_SHORT);
                toast.show();
            }
        } catch (Exception ex) {
            //Toast toast = Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT);
        }
        return sonIslemler;
    }

    public void FillListSonIslemler(ArrayList<SonIslem> sonIslemler) {
        ArrayList<String> last5 = new ArrayList<>();
        for (SonIslem item:sonIslemler){
            String trCariAdi = item.CariAdi.replaceAll("Ý", "İ");
            trCariAdi = trCariAdi.replaceAll("Þ", "Ş");
            trCariAdi = trCariAdi.replaceAll("Ð", "Ğ");
            last5.add(trCariAdi);
        }


        ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, last5);
        rvSonIslemler.setAdapter(arrayAdapter);
    }
}