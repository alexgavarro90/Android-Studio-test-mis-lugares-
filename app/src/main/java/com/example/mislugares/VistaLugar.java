package com.example.mislugares;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.DateFormat;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

/**
 * Created by usuario1 on 14/10/2016.
 */

public class VistaLugar extends AppCompatActivity {

    int id;
    private Lugar lugar;

    private Uri uriFoto;

    private ImageView imageView;

    //noves variables
    private SharedPreferences settings;
    SharedPreferences.Editor editor;
    private String KEY_foto ="foto";

    final static int RESULTADO_EDITAR=1;
    final static int RESULTADO_GALERIA=2;
    final static int RESULTADO_FOTO=3;



    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.vista_lugar);

        //Bundle extras =getIntent().getExtras();

        //id=extras.getLong("id",-1);

        id = (int) getIntent().getLongExtra("id",0);

        lugar=Lugares.elemento((int)id);

        KEY_foto +=id;

        imageView=(ImageView)findViewById(R.id.foto);
        actualizarVistas();

        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        editor = settings.edit();

        String foto = settings.getString(KEY_foto, null);


        if(foto!=null){

            imageView.setImageBitmap(StringToBitmap(foto));
        }




        RatingBar valoracion=(RatingBar) findViewById(R.id.valoracion);
        valoracion.setRating(lugar.getValoracion());
        valoracion.setOnRatingBarChangeListener(
                new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float valor, boolean fromUser) {

                        lugar.setValoracion(valor);
                    }

                });

    }

    public Bitmap StringToBitmap(String s){

        try{

            byte[] encodeByte = Base64.decode(s, Base64.DEFAULT);

            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);

        }catch (NullPointerException e){

            return null;


        }catch (OutOfMemoryError e){

            return null;

        }


    }




    public void galeria(View view){
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent,RESULTADO_GALERIA);
    }

    public void actualizarVistas(){
        TextView nombre=(TextView) findViewById(R.id.nombre);
        nombre.setText(lugar.getNombre());
        ImageView logo_tipo=(ImageView) findViewById(R.id.logo_tipo);
        logo_tipo.setImageResource(lugar.getTipo().getRecurso());
        TextView tipo=(TextView) findViewById(R.id.tipo);
        tipo.setText(lugar.getTipo().getTexto());
        TextView direccion =(TextView) findViewById(R.id.direccion);
        direccion.setText(lugar.getDireccion());
        TextView telefono=(TextView) findViewById(R.id.telefono);
        telefono.setText(Integer.toString(lugar.getTelefono()));
        TextView url =(TextView) findViewById(R.id.url);
        url.setText(lugar.getUrl());
        TextView comentario=(TextView) findViewById(R.id.comentario);
        comentario.setText(lugar.getComentario());
        TextView fecha=(TextView) findViewById(R.id.fecha);

        ponerFoto(imageView,lugar.getFoto());
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode==RESULTADO_EDITAR){

            actualizarVistas();
            findViewById(R.id.scrollView).invalidate();
        }
        else if(requestCode==RESULTADO_GALERIA && resultCode== Activity.RESULT_OK){

//            lugar.setFoto(data.getDataString());
//            ponerFoto(imageView,lugar.getFoto());
            try{
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                final Bitmap img = Bitmap.createScaledBitmap(selectedImage, 500,500,false);
                imageView.setImageBitmap(img);

                //save bimap to sharedPreferences
                String base64img = bitmaptoBase64(img);
                editor.putString(KEY_foto, base64img);
                editor.commit();


            }catch (FileNotFoundException e){}
        }

        else if(requestCode==RESULTADO_FOTO&&resultCode==Activity.RESULT_OK&&lugar!=null&&uriFoto!=null){

            lugar.setFoto(uriFoto.toString());
            ponerFoto(imageView,lugar.getFoto());

        }
    }
    private String bitmaptoBase64 (Bitmap img) {


        try {


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            img.compress(Bitmap.CompressFormat.PNG,100,baos);
            byte[] b =baos.toByteArray();
            String temp = Base64.encodeToString(b, Base64.DEFAULT);
            return temp;

        } catch (NullPointerException e) {

            return null;
        } catch (OutOfMemoryError e) {


        }

        return null;
    }


    //metode per cridar fotos desde galeria
    protected void ponerFoto(ImageView imageView, String uri){

        if (uri!=null){
//            imageView.setImageBitmap(reduceBitmap(this,uri,1024,1024));
            imageView.setImageURI(Uri.parse(uri));
        }
        else {
            imageView.setImageBitmap(null);
        }

    }

    //metode eliminar foto
    public void eliminarFoto(View view){
        lugar.setFoto(null);
        ponerFoto(imageView,null);
    }

//    public static Bitmap reduceBitmap(Context contexto,String uri, int maxAncho,int maxAlto){
//
//        try {
//            final BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;
//            BitmapFactory.decodeStream(contexto.getContentResolver().openInputStream(Uri.parse(uri)), null, options);
//            options.inSampleSize = (int) Math.max(Math.ceil(options.outWidth / maxAncho),
//                    Math.ceil(options.outHeight / maxAlto));
//            options.inJustDecodeBounds = false;
//            return BitmapFactory.decodeStream(contexto.getContentResolver().openInputStream(Uri.parse(uri)), null, options);
//
//        }
//        catch (FileNotFoundException e){
//            Toast.makeText(contexto,"Fichero/recurso no encontrado",Toast.LENGTH_LONG).show();
//            e.printStackTrace();
//            return null;
//        }
//    }

    //metode per cdridar fotos desde camera
    public void tomarFoto(View view){
        Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
        uriFoto=Uri.fromFile(new File(Environment.getExternalStorageDirectory() + File.separator
                + "img_" + (System.currentTimeMillis()/1000)+ ".jpg"));
        intent.putExtra(MediaStore.EXTRA_OUTPUT,uriFoto);
        startActivityForResult(intent,RESULTADO_FOTO);
    }





    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.vista_lugar,menu);
        return true;

    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.accion_compartir:
                Intent i=new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT,lugar.getNombre()+" - "+lugar.getUrl());
                startActivity(i);
                return true;
            case R.id.accion_llegar:
                verMapa(null);
                return true;
            case R.id.accion_editar:
                Intent intent= new Intent(VistaLugar.this,EdicionLugar.class);
                intent.putExtra("id",id);
                startActivityForResult(intent,RESULTADO_EDITAR);

                Log.i("valor de id","control" + id);
                return true;
            case R.id.accion_borrar:

                new AlertDialog.Builder(this)
                        .setTitle("¿Seguro que quieres eliminar este lugar?")
                        .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int whichButton){
                                Lugares.borrar((int)id);
                                finish();
                            }

                        })
                        .setNegativeButton("Cancelar",null)
                        .show();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void llamadaTelefono(View view){
        startActivity(new Intent(Intent.ACTION_DIAL,Uri.parse("tel:" + lugar.getTelefono())));
    }
    public void pgWeb(View view){
        startActivity(new Intent(Intent.ACTION_VIEW,

                Uri.parse(lugar.getUrl())));
    }

    public void verMapa(View view){
        Uri uri;
        double lat=lugar.getPosicion().getLatitud();
        double lon=lugar.getPosicion().getLongitud();
        if(lat!=0||lon!=0){
            uri=Uri.parse("geo:" + lat +"," + lon);
        }
        else {
            uri=Uri.parse("geo:0,0?q=" + lugar.getDireccion());
        }
        Intent intent=new Intent(Intent.ACTION_VIEW,uri);
        startActivity(intent);
    }




}
