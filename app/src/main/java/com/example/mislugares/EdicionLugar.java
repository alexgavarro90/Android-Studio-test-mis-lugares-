package com.example.mislugares;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

/**
 * Created by usuario1 on 19/10/2016.
 */

public class EdicionLugar extends AppCompatActivity {

    private EditText nombre;
    private Spinner tipo;
    private EditText direccion;
    private EditText telefono;
    private EditText url;
    private EditText comentario;

    private long id;
    private Lugar lugar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.edicion_lugar);

        tipo = (Spinner) findViewById(R.id.tipo);

        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, TipoLugar.getNombres());
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipo.setAdapter(adaptador);
        //tipo.setSelection(lugar.getTipo().ordinal());
        nombre = (EditText) findViewById(R.id.nombre);
        direccion = (EditText) findViewById(R.id.direccion);
        telefono = (EditText) findViewById(R.id.telefono);
        url = (EditText) findViewById(R.id.url);
        comentario = (EditText) findViewById(R.id.comentario);

        id =getIntent().getIntExtra("id", 0);

        Log.i("valor de id","control fuera if" + id);

        if (id != -1) {

            Log.i("valor de id","control if no es menos 1" + id);

            lugar = Lugares.vectorLugares.get((int) id);

            nombre.setText(lugar.getNombre());
            //ImageView logo_tipo=(ImageView) findViewById(R.id.logo_tipo);
            //logo_tipo.setImageResource(lugar.getTipo().getRecurso());

            direccion.setText(lugar.getDireccion());

            telefono.setText(Integer.toString(lugar.getTelefono()));

            url.setText(lugar.getUrl());

            comentario.setText(lugar.getComentario());
            //TextView fecha=(TextView) findViewById(R.id.fecha);

            String tipo1 = lugar.getTipo().getTexto();
            int i;
            for (i = 0; i < TipoLugar.getNombres().length && !tipo1.equals(TipoLugar.getNombres()[i]); i++) {
                tipo.setSelection(i);
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editar, menu);
        return true;

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.guardar) {

            if (this.id == -1)crearLugar();
            else modificarLugar();


            try {

                FileOutputStream fos = getApplicationContext().openFileOutput("datos.dat", Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(Lugares.vectorLugares);
                os.close();
                fos.close();
                Log.i("si","guarda");

        } catch (Exception e) {

                Log.i("no","guarda");
        }

            finish();

            return true;
    }
        if (id == R.id.cancelar) {

            finish();
            return true;

        }
                return super.onOptionsItemSelected(item);
    }

    public void modificarLugar(){

        lugar.setNombre(nombre.getText().toString());
        lugar.setTipo(TipoLugar.values()[tipo.getSelectedItemPosition()]);
        lugar.setDireccion(direccion.getText().toString());
        try {
            lugar.setTelefono(Integer.parseInt(telefono.getText().toString()));
        }

        catch (Exception e){}
        lugar.setUrl(url.getText().toString());
        lugar.setComentario(comentario.getText().toString());
    }

    public void crearLugar(){

        Lugar lugar=new Lugar();

        lugar.setNombre(nombre.getText().toString());
        lugar.setTipo(TipoLugar.values()[tipo.getSelectedItemPosition()]);
        lugar.setDireccion(direccion.getText().toString());
        try {
            lugar.setTelefono(Integer.parseInt(telefono.getText().toString()));
        }
        catch (Exception e){}
        lugar.setUrl(url.getText().toString());
        lugar.setComentario(comentario.getText().toString());

        Lugares.vectorLugares.add(lugar);


    }
}
