package com.reltv.retribusi;

import android.app.*;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.*;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.*;
import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends Activity {
    private static final String[] CATS = {"JASA PARKIR","JASA PELABUHAN SRI SIANTAN","JASA PELABUHAN PADANG MELANG (JEMAJA)","JASA PELABUHAN ANTANG","JASA PELABUHAN KAMPUNG BARU","JASA PELABUHAN RORO MATAK","JASA TAMBAT PELABUHAN RORO MATAK"};
    private static final int NAVY=Color.rgb(13,54,91), BLUE=Color.rgb(22,105,166), PALE=Color.rgb(237,245,251);
    private DB helper; private LinearLayout root;
    private NumberFormat fmt=NumberFormat.getCurrencyInstance(new Locale("id","ID"));
    public void onCreate(Bundle b){super.onCreate(b);helper=new DB();dashboard();}
    private String rp(long n){return fmt.format(n).replace(",00","");}
    private TextView label(String s,int z,int c,boolean bold){
        TextView t=new TextView(this);t.setText(s);t.setTextSize(z);t.setTextColor(c);if(bold)t.setTypeface(null,Typeface.BOLD);t.setPadding(5,8,5,8);return t;
    }
    private void base(String title){
        ScrollView sc=new ScrollView(this);root=new LinearLayout(this);root.setOrientation(1);root.setPadding(16,14,16,24);sc.addView(root);setContentView(sc);
        TextView h=label(title,19,Color.WHITE,true);h.setGravity(Gravity.CENTER);h.setBackgroundColor(NAVY);h.setPadding(10,22,10,22);root.addView(h);
    }
    private Button btn(String s,Runnable r){Button b=new Button(this);b.setText(s);b.setAllCaps(false);b.setOnClickListener(v->r.run());return b;}
    private long sum(String cat){
        SQLiteDatabase d=helper.getReadableDatabase();
        Cursor c=cat==null?d.rawQuery("SELECT COALESCE(SUM(amount),0) FROM tx WHERE date LIKE '2026-%'",null):d.rawQuery("SELECT COALESCE(SUM(amount),0) FROM tx WHERE category=? AND date LIKE '2026-%'",new String[]{cat});
        long n=c.moveToFirst()?c.getLong(0):0;c.close();return n;
    }
    private void dashboard(){
        base("REKAPAN RETRIBUSI JASA KEPELABUHANAN\nUPT TRANS • TAHUN 2026");
        TextView total=label("TOTAL PENDAPATAN GABUNGAN 7 RETRIBUSI\n"+rp(sum(null)),19,NAVY,true);total.setGravity(Gravity.CENTER);total.setBackgroundColor(PALE);total.setPadding(12,28,12,28);root.addView(total);
        root.addView(label("MENU JENIS RETRIBUSI",16,NAVY,true));
        for(String cat:CATS)root.addView(btn(cat,()->category(cat)));
        root.addView(btn("CETAK LAPORAN PDF",()->new AlertDialog.Builder(this).setMessage("Fitur PDF sedang disiapkan.").setPositiveButton("OK",null).show()));
        root.addView(btn("BACKUP & PEMULIHAN DATA",()->new AlertDialog.Builder(this).setMessage("Fitur backup sedang disiapkan.").setPositiveButton("OK",null).show()));
        root.addView(label("UPT Pengelolaan Transportasi Dinas Perhubungan dan Lingkungan Hidup",12,Color.GRAY,false));
    }
    private void category(String cat){
        base(cat);root.addView(label("TOTAL TAHUN 2026: "+rp(sum(cat)),20,BLUE,true));
        root.addView(btn("TAMBAH TRANSAKSI",()->add(cat)));
        root.addView(label("RIWAYAT TRANSAKSI",16,NAVY,true));
        Cursor c=helper.getReadableDatabase().rawQuery("SELECT id,date,amount,note FROM tx WHERE category=? ORDER BY date DESC",new String[]{cat});
        while(c.moveToNext()){
            long id=c.getLong(0),amount=c.getLong(2);String date=c.getString(1),note=c.getString(3);
            root.addView(btn(date+" • "+rp(amount)+(note.isEmpty()?"":"\n"+note),()->edit(cat,id,date,amount,note)));
        }c.close();root.addView(btn("KEMBALI KE DASHBOARD",this::dashboard));
    }
    private void add(String cat){
        EditText date=new EditText(this);date.setHint("Tanggal YYYY-MM-DD (2026)");
        EditText amount=new EditText(this);amount.setHint("Nominal Rupiah");amount.setInputType(2);
        EditText note=new EditText(this);note.setHint("Catatan opsional");
        LinearLayout box=new LinearLayout(this);box.setOrientation(1);box.addView(date);box.addView(amount);box.addView(note);
        new AlertDialog.Builder(this).setTitle("Tambah transaksi").setView(box).setNegativeButton("Batal",null).setPositiveButton("Simpan",(d,w)->{
            try{String day=date.getText().toString().trim();long val=Long.parseLong(amount.getText().toString().trim());
                if(!day.matches("2026-\\d{2}-\\d{2}")||val<0)throw new Exception();
                ContentValues v=new ContentValues();v.put("date",day);v.put("category",cat);v.put("amount",val);v.put("note",note.getText().toString().trim());
                helper.getWritableDatabase().insertOrThrow("tx",null,v);category(cat);
            }catch(Exception e){new AlertDialog.Builder(this).setMessage("Tanggal/nominal tidak sah atau transaksi tanggal tersebut sudah ada.").setPositiveButton("OK",null).show();}
        }).show();
    }
    private void edit(String cat,long id,String date,long old,String oldNote){
        EditText amount=new EditText(this);amount.setInputType(2);amount.setText(String.valueOf(old));
        EditText note=new EditText(this);note.setText(oldNote);note.setHint("Catatan");
        LinearLayout box=new LinearLayout(this);box.setOrientation(1);box.addView(label("Tanggal: "+date,14,NAVY,true));box.addView(amount);box.addView(note);
        new AlertDialog.Builder(this).setTitle("Edit transaksi").setView(box).setNegativeButton("Tutup",null)
        .setNeutralButton("Hapus",(d,w)->new AlertDialog.Builder(this).setMessage("Hapus transaksi ini?").setNegativeButton("Batal",null).setPositiveButton("Hapus",(x,y)->{helper.getWritableDatabase().delete("tx","id=?",new String[]{String.valueOf(id)});category(cat);}).show())
        .setPositiveButton("Simpan",(d,w)->{try{ContentValues v=new ContentValues();v.put("amount",Long.parseLong(amount.getText().toString().trim()));v.put("note",note.getText().toString().trim());helper.getWritableDatabase().update("tx",v,"id=?",new String[]{String.valueOf(id)});category(cat);}catch(Exception e){new AlertDialog.Builder(this).setMessage("Nominal tidak sah.").setPositiveButton("OK",null).show();}}).show();
    }
    private class DB extends SQLiteOpenHelper{
        DB(){super(MainActivity.this,"retribusi.db",null,1);}
        public void onCreate(SQLiteDatabase d){d.execSQL("CREATE TABLE tx(id INTEGER PRIMARY KEY AUTOINCREMENT,date TEXT NOT NULL,category TEXT NOT NULL,amount INTEGER NOT NULL,note TEXT,UNIQUE(date,category))");}
        public void onUpgrade(SQLiteDatabase d,int a,int b){}
    }
}
