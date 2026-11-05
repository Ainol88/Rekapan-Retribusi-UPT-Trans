package id.reltv.retribusi;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends Activity {
    static final String[] CATS = {
        "JASA PARKIR","JASA PELABUHAN SRI SIANTAN",
        "JASA PELABUHAN PADANG MELANG (JEMAJA)","JASA PELABUHAN ANTANG",
        "JASA PELABUHAN KAMPUNG BARU","JASA PELABUHAN RORO MATAK",
        "JASA TAMBAT PELABUHAN RORO MATAK"
    };
    SQLiteDatabase db;
    LinearLayout root;
    final NumberFormat rupiah = NumberFormat.getCurrencyInstance(new Locale("id","ID"));

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        db = openOrCreateDatabase("retribusi_2026.db", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS transaksi(id INTEGER PRIMARY KEY AUTOINCREMENT,tanggal TEXT NOT NULL,kategori INTEGER NOT NULL,nominal INTEGER NOT NULL,catatan TEXT,UNIQUE(tanggal,kategori))");
        showDashboard();
    }
    TextView text(String s,int size,int color) {
        TextView t=new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color); t.setPadding(16,12,16,12); return t;
    }
    Button button(String s, LinearLayout box, Runnable r) {
        Button b=new Button(this); b.setText(s); box.addView(b); b.setOnClickListener(v->r.run()); return b;
    }
    void base(String title) {
        root=new LinearLayout(this); root.setOrientation(1); root.setPadding(16,16,16,16); root.setBackgroundColor(Color.rgb(239,246,252));
        ScrollView sc=new ScrollView(this); sc.addView(root); setContentView(sc);
        TextView h=text(title,20,Color.rgb(13,55,92)); h.setGravity(Gravity.CENTER); root.addView(h);
        root.addView(text("UPT PENGELOLAAN TRANSPORTASI DINAS PERHUBUNGAN DAN LINGKUNGAN HIDUP",12,Color.DKGRAY));
    }
    long total(String where,String[] args) {
        Cursor c=db.rawQuery("SELECT COALESCE(SUM(nominal),0) FROM transaksi "+where,args);
        long n=0; if(c.moveToFirst()) n=c.getLong(0); c.close(); return n;
    }
    String money(long n) { return rupiah.format(n); }
    void showDashboard() {
        base("REKAPAN RETRIBUSI JASA KEPELABUHANAN TAHUN 2026");
        TextView summary=text("TOTAL PENDAPATAN GABUNGAN 7 RETRIBUSI\n"+money(total("WHERE tanggal LIKE '2026-%'",new String[]{}))+"\nTahun 2026",22,Color.WHITE);
        summary.setGravity(Gravity.CENTER); summary.setBackgroundColor(Color.rgb(20,91,145)); root.addView(summary);
        for(int i=0;i<CATS.length;i++){ final int k=i; button(CATS[i],root,()->showCategory(k)); }
        button("CETAK LAPORAN PDF",root,()->new AlertDialog.Builder(this).setMessage("Fungsi laporan PDF akan ditambahkan pada tahap berikutnya.").setPositiveButton("OK",null).show());
        button("BACKUP & PEMULIHAN DATA",root,()->new AlertDialog.Builder(this).setMessage("Fungsi backup dan pemulihan akan ditambahkan pada tahap berikutnya.").setPositiveButton("OK",null).show());
        root.addView(text("UPT Trans – Melayani dengan Profesional",12,Color.GRAY));
    }
    void showCategory(int k) {
        base(CATS[k]);
        button("TAMBAH TRANSAKSI",root,()->editTransaction(k,null));
        root.addView(text("REKAP HARIAN / BULANAN / TAHUNAN",16,Color.BLACK));
        root.addView(text("Total tahun 2026: "+money(total("WHERE kategori=? AND tanggal LIKE '2026-%'",new String[]{""+k})),18,Color.rgb(13,75,120)));
        for(int m=1;m<=12;m++){
            String mm=String.format(Locale.US,"%02d",m);
            root.addView(text(mm+" — "+money(total("WHERE kategori=? AND tanggal LIKE ?",new String[]{""+k,"2026-"+mm+"-%"})),14,Color.DKGRAY));
        }
        root.addView(text("RIWAYAT TRANSAKSI",16,Color.BLACK));
        Cursor c=db.rawQuery("SELECT id,tanggal,nominal,COALESCE(catatan,'') FROM transaksi WHERE kategori=? ORDER BY tanggal DESC",new String[]{""+k});
        while(c.moveToNext()){
            int id=c.getInt(0); String date=c.getString(1); long amount=c.getLong(2); String note=c.getString(3);
            LinearLayout item=new LinearLayout(this); item.setOrientation(1); item.setPadding(4,8,4,8);
            item.addView(text(date+"  "+money(amount)+"\n"+note,14,Color.DKGRAY));
            button("UBAH",item,()->editTransaction(k,id)); button("HAPUS",item,()->new AlertDialog.Builder(this).setMessage("Hapus transaksi ini?").setNegativeButton("BATAL",null).setPositiveButton("HAPUS",(d,w)->{db.delete("transaksi","id=?",new String[]{""+id});showCategory(k);}).show());
            root.addView(item);
        }
        c.close();
        button("KEMBALI KE DASHBOARD",root,()->showDashboard());
    }
    void editTransaction(int k,Integer id) {
        LinearLayout form=new LinearLayout(this); form.setOrientation(1);
        EditText date=new EditText(this); date.setHint("Tanggal (YYYY-MM-DD), tahun 2026"); form.addView(date);
        EditText amount=new EditText(this); amount.setHint("Nominal Rupiah"); amount.setInputType(2); form.addView(amount);
        EditText note=new EditText(this); note.setHint("Catatan (opsional)"); form.addView(note);
        if(id!=null){Cursor c=db.rawQuery("SELECT tanggal,nominal,COALESCE(catatan,'') FROM transaksi WHERE id=?",new String[]{""+id});if(c.moveToFirst()){date.setText(c.getString(0));amount.setText(""+c.getLong(1));note.setText(c.getString(2));}c.close();}
        new AlertDialog.Builder(this).setTitle(id==null?"Transaksi Baru":"Ubah Transaksi").setView(form).setNegativeButton("BATAL",null).setPositiveButton("SIMPAN",(d,w)->{
            String dt=date.getText().toString().trim(), am=amount.getText().toString().trim();
            if(!dt.matches("2026-(0[1-9]|1[0-2])-([0-2][0-9]|3[01])")||am.isEmpty()){new AlertDialog.Builder(this).setMessage("Periksa tanggal dan nominal.").setPositiveButton("OK",null).show();return;}
            ContentValues v=new ContentValues();v.put("tanggal",dt);v.put("kategori",k);v.put("nominal",Long.parseLong(am));v.put("catatan",note.getText().toString());
            try{if(id==null)db.insertOrThrow("transaksi",null,v);else db.update("transaksi",v,"id=?",new String[]{""+id});showCategory(k);}
            catch(Exception e){new AlertDialog.Builder(this).setMessage("Transaksi kategori ini pada tanggal tersebut sudah ada. Ubah transaksi sedia ada.").setPositiveButton("OK",null).show();}
        }).show();
    }
}
