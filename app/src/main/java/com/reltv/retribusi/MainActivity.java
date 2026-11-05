package com.reltv.retribusi;

import android.app.*;
import android.os.Bundle;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.graphics.*;
import android.net.Uri;
import android.view.*;
import android.widget.*;
import android.provider.OpenableColumns;
import org.json.*;
import java.io.*;
import java.text.*;
import java.util.*;

public class MainActivity extends Activity {
 private static final String[] CATS={"JASA PARKIR","JASA PELABUHAN SRI SIANTAN","JASA PELABUHAN PADANG MELANG (JEMAJA)","JASA PELABUHAN ANTANG","JASA PELABUHAN KAMPUNG BARU","JASA PELABUHAN RORO MATAK","JASA TAMBAT PELABUHAN RORO MATAK"};
 private static final int NAVY=0xff063b80, BLUE=0xff0874cf, PALE=0xffe8f3ff, GREEN=0xff079b45;
 private DB helper; private LinearLayout root; private String activeCat;
 private final NumberFormat money=NumberFormat.getNumberInstance(new Locale("id","ID"));
 private final SimpleDateFormat iso=new SimpleDateFormat("yyyy-MM-dd",Locale.US);
 private int dp(float n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}
 public void onCreate(Bundle b){super.onCreate(b);getWindow().setStatusBarColor(NAVY);helper=new DB();dashboard();}
 private String rp(long n){return "Rp "+money.format(n);}
 private TextView text(String s,int size,int color,boolean bold){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);if(bold)t.setTypeface(null,1);t.setPadding(dp(6),dp(7),dp(6),dp(7));return t;}
 private void base(String title){ScrollView sc=new ScrollView(this);root=new LinearLayout(this);root.setOrientation(1);root.setPadding(dp(14),dp(12),dp(14),dp(24));sc.addView(root);sc.setBackgroundColor(0xffedf5ff);setContentView(sc);TextView h=text(title,19,Color.WHITE,true);h.setGravity(17);h.setBackgroundColor(NAVY);h.setPadding(dp(12),dp(20),dp(12),dp(20));root.addView(h);}
 private Button button(String s,Runnable r){Button b=new Button(this);b.setText(s);b.setAllCaps(false);b.setOnClickListener(v->r.run());root.addView(b);return b;}
 private EditText field(String hint,int type){EditText e=new EditText(this);e.setSingleLine(true);e.setHint(hint);e.setInputType(type);return e;}
 private long sum(String cat,String from,String to){SQLiteDatabase d=helper.getReadableDatabase();String q="SELECT COALESCE(SUM(amount),0) FROM tx WHERE date>=? AND date<=?"+(cat==null?"":" AND category=?");Cursor c=d.rawQuery(q,cat==null?new String[]{from,to}:new String[]{from,to,cat});c.moveToFirst();long n=c.getLong(0);c.close();return n;}
 private String yearStart(){return "2026-01-01";} private String yearEnd(){return "2026-12-31";}
 private void dashboard(){
  ScrollView sc=new ScrollView(this);
  LinearLayout page=new LinearLayout(this);page.setOrientation(1);page.setBackgroundColor(0xffcfe7ff);
  sc.addView(page);setContentView(sc);root=page;
  // Header panel in the approved UPT Trans blue-and-gold style
  LinearLayout head=new LinearLayout(this);head.setOrientation(0);head.setGravity(Gravity.CENTER_VERTICAL);head.setPadding(dp(14),dp(18),dp(14),dp(18));head.setBackgroundColor(0xff064b9b);page.addView(head);
  TextView emblem=text("UPT",25,0xffffd22e,true);emblem.setGravity(Gravity.CENTER);emblem.setBackgroundColor(0xff073b80);emblem.setPadding(dp(12),dp(16),dp(12),dp(16));head.addView(emblem);
  LinearLayout ht=new LinearLayout(this);ht.setOrientation(1);ht.setPadding(dp(12),0,0,0);head.addView(ht,new LinearLayout.LayoutParams(0,-2,1));
  TextView h1=text("REKAPAN RETRIBUSI",21,Color.WHITE,true);h1.setPadding(0,0,0,0);ht.addView(h1);
  TextView h2=text("JASA KEPELABUHANAN",19,0xffffd22e,true);h2.setPadding(0,0,0,0);ht.addView(h2);
  TextView h3=text("UPT TRANS TAHUN 2026",17,Color.WHITE,true);h3.setPadding(0,0,0,0);ht.addView(h3);
  TextView banner=text("⚓   PELAYANAN KEPELABUHANAN UPT TRANS",15,Color.WHITE,true);banner.setGravity(Gravity.CENTER);banner.setBackgroundColor(0xff1686c8);banner.setPadding(dp(10),dp(24),dp(10),dp(24));page.addView(banner);
  LinearLayout card=new LinearLayout(this);card.setOrientation(1);card.setGravity(Gravity.CENTER);card.setPadding(dp(12),dp(15),dp(12),dp(15));card.setBackgroundColor(Color.WHITE);
  LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,-2);cp.setMargins(dp(14),dp(12),dp(14),dp(12));page.addView(card,cp);
  TextView label=text("TOTAL PENDAPATAN GABUNGAN 7 RETRIBUSI",17,NAVY,true);label.setGravity(Gravity.CENTER);card.addView(label);
  TextView amount=text(rp(sum(null,yearStart(),yearEnd())),31,0xff07843c,true);amount.setGravity(Gravity.CENTER);card.addView(amount);
  TextView yr=text("TAHUN 2026",16,Color.WHITE,true);yr.setGravity(Gravity.CENTER);yr.setBackgroundColor(0xff079b45);card.addView(yr);
  TextView menu=text("MENU JASA RETRIBUSI",18,NAVY,true);page.addView(menu);
  int[] colors={0xff0874cf,0xff13a653,0xffff9800,0xff813ac8,0xff08a9c8,0xff8eae13,0xff5146df};
  for(int i=0;i<CATS.length;i++){
   LinearLayout row=new LinearLayout(this);row.setOrientation(0);row.setGravity(Gravity.CENTER_VERTICAL);row.setPadding(dp(10),dp(7),dp(10),dp(7));row.setBackgroundColor(Color.WHITE);
   LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(-1,-2);rp.setMargins(dp(14),dp(4),dp(14),dp(4));page.addView(row,rp);
   TextView ico=text(i==0?"🚘":i==6?"⚓":"船",23,Color.WHITE,true);ico.setGravity(Gravity.CENTER);ico.setBackgroundColor(colors[i]);ico.setWidth(dp(66));ico.setHeight(dp(58));row.addView(ico);
   TextView name=text(CATS[i],15,NAVY,true);name.setGravity(Gravity.CENTER_VERTICAL);name.setPadding(dp(12),dp(8),dp(4),dp(8));row.addView(name,new LinearLayout.LayoutParams(0,-2,1));
   TextView go=text("❯",20,BLUE,true);row.addView(go);
   row.setOnClickListener(v->category(CATS[i]));
  }
  LinearLayout actions=new LinearLayout(this);actions.setOrientation(0);actions.setPadding(dp(10),dp(8),dp(10),dp(8));page.addView(actions);
  Button pdf=new Button(this);pdf.setText("▣  Cetak Laporan PDF");pdf.setTextColor(Color.WHITE);pdf.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xff0759b4));pdf.setOnClickListener(v->makePdf());actions.addView(pdf,new LinearLayout.LayoutParams(0,dp(70),1));
  Button backup=new Button(this);backup.setText("▤  Backup & Pemulihan Data");backup.setTextColor(Color.WHITE);backup.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xff079b45));backup.setOnClickListener(v->backupMenu());LinearLayout.LayoutParams bp=new LinearLayout.LayoutParams(0,dp(70),1);bp.setMargins(dp(8),0,0,0);actions.addView(backup,bp);
  TextView foot=text("UPT Trans – Melayani dengan Profesional",15,Color.WHITE,true);foot.setGravity(Gravity.CENTER);foot.setBackgroundColor(0xff064b9b);foot.setPadding(dp(8),dp(15),dp(8),dp(15));page.addView(foot);
 }
 private void category(String cat){
  activeCat=cat;base(cat);
  root.addView(text("TOTAL TAHUN 2026: "+rp(sum(cat,yearStart(),yearEnd())),19,NAVY,true));
  button("＋ TAMBAH TRANSAKSI",()->add(cat));
  root.addView(text("REKAPAN",17,NAVY,true));
  button("HARIAN",()->showRecap(cat,0));button("MINGGUAN (ISNIN–AHAD)",()->showRecap(cat,1));button("BULANAN",()->showRecap(cat,2));button("TAHUNAN",()->showRecap(cat,3));
  root.addView(text("RIWAYAT TRANSAKSI",17,NAVY,true));
  Cursor c=helper.getReadableDatabase().rawQuery("SELECT id,date,first_no,last_no,qty,price,amount,note FROM tx WHERE category=? ORDER BY date DESC",new String[]{cat});
  while(c.moveToNext()){long id=c.getLong(0),amount=c.getLong(6);String date=c.getString(1),desc=date+" • "+c.getLong(4)+" tiket • "+rp(amount)+(c.getString(7).isEmpty()?"":"\n"+c.getString(7));button(desc,()->edit(cat,id));}c.close();
  button("KEMBALI KE DASHBOARD",this::dashboard);
 }
 private void showRecap(String cat,int mode){
  base("REKAP "+(mode==0?"HARIAN":mode==1?"MINGGUAN":mode==2?"BULANAN":"TAHUNAN")+"\n"+cat);
  String sql=mode==0?"SELECT date,SUM(amount) FROM tx WHERE category=? GROUP BY date ORDER BY date":mode==1?"SELECT strftime('%Y-%W',date),MIN(date),MAX(date),SUM(amount) FROM tx WHERE category=? GROUP BY strftime('%Y-%W',date) ORDER BY 1":mode==2?"SELECT strftime('%m',date),SUM(amount) FROM tx WHERE category=? GROUP BY strftime('%m',date) ORDER BY 1":"SELECT '2026',SUM(amount) FROM tx WHERE category=? AND date LIKE '2026-%'";
  Cursor c=helper.getReadableDatabase().rawQuery(sql,new String[]{cat});
  while(c.moveToNext()){String label;if(mode==1)label="Minggu "+c.getString(0)+" ("+c.getString(1)+" – "+c.getString(2)+")";else if(mode==2)label=monthName(Integer.parseInt(c.getString(0)));else label=c.getString(0);root.addView(text(label+"    "+rp(c.getLong(mode==1?3:1)),16,NAVY,true));}
  c.close();root.addView(text("Jumlah: "+rp(sum(cat,yearStart(),yearEnd())),18,GREEN,true));button("KEMBALI",()->category(cat));
 }
 private String monthName(int m){return new java.text.DateFormatSymbols(new Locale("id","ID")).getMonths()[m-1];}
 private void add(String cat){
  LinearLayout box=new LinearLayout(this);box.setOrientation(1);
  EditText date=field("Tanggal transaksi",1);date.setFocusable(false);date.setText(iso.format(new Date()));date.setOnClickListener(v->{Calendar cal=Calendar.getInstance();new DatePickerDialog(this,(view,y,m,d)->date.setText(String.format(Locale.US,"%04d-%02d-%02d",y,m+1,d)),cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show();});
  EditText first=field("Nomor seri tiket awal",2),last=field("Nomor seri tiket akhir",2),price=field("Harga per tiket (Rp1.000–Rp10.000)",2),qty=field("Jumlah tiket (otomatis)",2),amount=field("Nominal retribusi (otomatis)",2),note=field("Catatan (opsional)",1);
  qty.setFocusable(false);amount.setFocusable(false);
  int saved=helper.price(cat);price.setText(String.valueOf(saved==0?1000:saved));
  Runnable calc=()->{try{long a=Long.parseLong(first.getText().toString()),b=Long.parseLong(last.getText().toString()),p=Long.parseLong(price.getText().toString());long q=b-a+1;qty.setText(q>0?String.valueOf(q):"");amount.setText(q>0?String.valueOf(q*p):"");}catch(Exception e){qty.setText("");amount.setText("");}};
  first.addTextChangedListener(watch(calc));last.addTextChangedListener(watch(calc));price.addTextChangedListener(watch(calc));
  addField(box,"TANGGAL TRANSAKSI",date);addField(box,"NOMOR SERI TIKET AWAL",first);addField(box,"NOMOR SERI TIKET AKHIR",last);addField(box,"HARGA PER TIKET",price);addField(box,"JUMLAH TIKET",qty);addField(box,"NOMINAL RETRIBUSI",amount);addField(box,"CATATAN",note);
  new AlertDialog.Builder(this).setTitle("Tambah Transaksi").setView(box).setNegativeButton("Batal",null).setPositiveButton("Simpan",(d,w)->{try{long a=Long.parseLong(first.getText().toString()),b=Long.parseLong(last.getText().toString()),p=Long.parseLong(price.getText().toString()),q=b-a+1;if(q<1||p<1000||p>10000)throw new Exception();ContentValues v=new ContentValues();v.put("date",date.getText().toString());v.put("category",cat);v.put("first_no",a);v.put("last_no",b);v.put("qty",q);v.put("price",p);v.put("amount",q*p);v.put("note",note.getText().toString());helper.getWritableDatabase().insertOrThrow("tx",null,v);helper.savePrice(cat,(int)p);category(cat);}catch(Exception e){toast("Data tidak sah, rentang harga Rp1.000–Rp10.000 atau transaksi tanggal tersebut sudah ada.");}}).show();
 }
 private android.text.TextWatcher watch(Runnable r){return new android.text.TextWatcher(){public void beforeTextChanged(CharSequence s,int st,int c,int a){}public void onTextChanged(CharSequence s,int st,int before,int count){r.run();}public void afterTextChanged(android.text.Editable e){}};}
 private void addField(LinearLayout box,String title,EditText e){TextView t=text(title,12,NAVY,true);box.addView(t);box.addView(e);}
 private void edit(String cat,long id){Cursor c=helper.getReadableDatabase().rawQuery("SELECT date,first_no,last_no,price,note FROM tx WHERE id=?",new String[]{String.valueOf(id)});if(!c.moveToFirst()){c.close();return;}String date0=c.getString(0),note0=c.getString(4);long f=c.getLong(1),l=c.getLong(2),p=c.getLong(3);c.close();EditText price=field("Harga tiket",2),note=field("Catatan",1);price.setText(String.valueOf(p));note.setText(note0);LinearLayout box=new LinearLayout(this);box.setOrientation(1);addField(box,"HARGA PER TIKET",price);addField(box,"CATATAN",note);new AlertDialog.Builder(this).setTitle("Edit "+date0+" • Tiket "+f+"–"+l).setView(box).setNegativeButton("Batal",null).setNeutralButton("Hapus",(d,w)->new AlertDialog.Builder(this).setMessage("Hapus transaksi?").setNegativeButton("Batal",null).setPositiveButton("Hapus",(x,y)->{helper.getWritableDatabase().delete("tx","id=?",new String[]{String.valueOf(id)});category(cat);}).show()).setPositiveButton("Simpan",(d,w)->{try{long np=Long.parseLong(price.getText().toString());if(np<1000||np>10000)throw new Exception();ContentValues v=new ContentValues();v.put("price",np);v.put("amount",(l-f+1)*np);v.put("note",note.getText().toString());helper.getWritableDatabase().update("tx",v,"id=?",new String[]{String.valueOf(id)});helper.savePrice(cat,(int)np);category(cat);}catch(Exception e){toast("Harga mesti Rp1.000 hingga Rp10.000");}}).show();}
 private void makePdf(){try{android.graphics.pdf.PdfDocument doc=new android.graphics.pdf.PdfDocument();android.graphics.pdf.PdfDocument.PageInfo info=new android.graphics.pdf.PdfDocument.PageInfo.Builder(842,595,1).create();android.graphics.pdf.PdfDocument.Page page=doc.startPage(info);Canvas c=page.getCanvas();Paint p=new Paint(3);p.setColor(NAVY);p.setTextSize(19);p.setTypeface(Typeface.create("sans-serif",1));c.drawText("UPT PENGELOLAAN TRANSPORTASI DINAS PERHUBUNGAN DAN LINGKUNGAN HIDUP",24,34,p);p.setTextSize(16);c.drawText("REKAPAN RETRIBUSI JASA KEPELABUHANAN TAHUN 2026",24,62,p);p.setTextSize(10);int y=100;c.drawText("JENIS RETRIBUSI",24,y,p);for(int m=1;m<=12;m++)c.drawText(monthName(m).substring(0,3),235+(m-1)*43,y,p);c.drawText("TOTAL",760,y,p);y+=25;for(String cat:CATS){p.setTextSize(9);c.drawText(cat,24,y,p);long total=0;for(int m=1;m<=12;m++){String st=String.format(Locale.US,"2026-%02d-01",m),en=String.format(Locale.US,"2026-%02d-31",m);long n=sum(cat,st,en);total+=n;c.drawText(money.format(n),235+(m-1)*43,y,p);}c.drawText(money.format(total),760,y,p);y+=35;}c.drawText("JUMLAH KESELURUHAN: "+rp(sum(null,yearStart(),yearEnd())),24,y+15,p);doc.finishPage(page);Intent i=new Intent(Intent.ACTION_CREATE_DOCUMENT);i.setType("application/pdf");i.putExtra(Intent.EXTRA_TITLE,"Rekapan_Retribusi_UPT_Trans_2026.pdf");startActivityForResult(i,10);pendingPdf=doc;}catch(Exception e){toast("Gagal menyediakan PDF");}}
 private android.graphics.pdf.PdfDocument pendingPdf;
 private void backupMenu(){new AlertDialog.Builder(this).setTitle("Backup & Pemulihan Data").setItems(new String[]{"Backup ke JSON","Pulihkan dari JSON"},(d,w)->{if(w==0){Intent i=new Intent(Intent.ACTION_CREATE_DOCUMENT);i.setType("application/json");i.putExtra(Intent.EXTRA_TITLE,"Backup_Retribusi_2026.json");startActivityForResult(i,11);}else{Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.setType("application/json");i.addCategory(Intent.CATEGORY_OPENABLE);startActivityForResult(i,12);}}).show();}
 protected void onActivityResult(int req,int res,Intent data){super.onActivityResult(req,res,data);if(res!=RESULT_OK||data==null)return;try{Uri u=data.getData();if(req==10&&pendingPdf!=null){OutputStream o=getContentResolver().openOutputStream(u);pendingPdf.writeTo(o);o.close();pendingPdf.close();pendingPdf=null;toast("PDF berjaya disimpan");}else if(req==11){JSONObject obj=new JSONObject();JSONArray arr=new JSONArray();Cursor c=helper.getReadableDatabase().rawQuery("SELECT date,category,first_no,last_no,qty,price,amount,note FROM tx",null);while(c.moveToNext()){JSONObject x=new JSONObject();String[] keys={"date","category","first_no","last_no","qty","price","amount","note"};for(int z=0;z<keys.length;z++)x.put(keys[z],c.getString(z));arr.put(x);}c.close();obj.put("transactions",arr);OutputStream o=getContentResolver().openOutputStream(u);o.write(obj.toString(2).getBytes("UTF-8"));o.close();toast("Backup berjaya disimpan");}else if(req==12){new AlertDialog.Builder(this).setMessage("Pemulihan akan menggantikan semua transaksi semasa. Teruskan?").setNegativeButton("Batal",null).setPositiveButton("Pulihkan",(d,w)->restore(u)).show();}}catch(Exception e){toast("Operasi fail gagal: "+e.getMessage());}}
 private void restore(Uri u){try{InputStream in=getContentResolver().openInputStream(u);ByteArrayOutputStream b=new ByteArrayOutputStream();byte[] buf=new byte[8192];int n;while((n=in.read(buf))>0)b.write(buf,0,n);in.close();JSONObject obj=new JSONObject(b.toString("UTF-8"));JSONArray a=obj.getJSONArray("transactions");SQLiteDatabase db=helper.getWritableDatabase();db.beginTransaction();try{db.delete("tx",null,null);for(int i=0;i<a.length();i++){JSONObject x=a.getJSONObject(i);ContentValues v=new ContentValues();String[] keys={"date","category","first_no","last_no","qty","price","amount","note"};for(String k:keys)v.put(k,x.optString(k,""));db.insertOrThrow("tx",null,v);}db.setTransactionSuccessful();}finally{db.endTransaction();}toast("Data berjaya dipulihkan");dashboard();}catch(Exception e){toast("Fail backup tidak sah atau gagal dipulihkan");}}
 private void toast(String s){Toast.makeText(this,s,Toast.LENGTH_LONG).show();}
 private class DB extends SQLiteOpenHelper{
  DB(){super(MainActivity.this,"retribusi.db",null,2);}
  public void onCreate(SQLiteDatabase d){d.execSQL("CREATE TABLE tx(id INTEGER PRIMARY KEY AUTOINCREMENT,date TEXT NOT NULL,category TEXT NOT NULL,first_no INTEGER DEFAULT 0,last_no INTEGER DEFAULT 0,qty INTEGER DEFAULT 1,price INTEGER DEFAULT 0,amount INTEGER NOT NULL,note TEXT,UNIQUE(date,category))");d.execSQL("CREATE TABLE prices(category TEXT PRIMARY KEY,price INTEGER NOT NULL)");}
  public void onUpgrade(SQLiteDatabase d,int old,int now){if(old<2){d.execSQL("ALTER TABLE tx ADD COLUMN first_no INTEGER DEFAULT 0");d.execSQL("ALTER TABLE tx ADD COLUMN last_no INTEGER DEFAULT 0");d.execSQL("ALTER TABLE tx ADD COLUMN qty INTEGER DEFAULT 1");d.execSQL("ALTER TABLE tx ADD COLUMN price INTEGER DEFAULT 0");d.execSQL("CREATE TABLE IF NOT EXISTS prices(category TEXT PRIMARY KEY,price INTEGER NOT NULL)");}}
  int price(String c){Cursor x=getReadableDatabase().rawQuery("SELECT price FROM prices WHERE category=?",new String[]{c});int p=x.moveToFirst()?x.getInt(0):0;x.close();return p;}
  void savePrice(String c,int p){ContentValues v=new ContentValues();v.put("category",c);v.put("price",p);getWritableDatabase().insertWithOnConflict("prices",null,v,SQLiteDatabase.CONFLICT_REPLACE);}
 }
}