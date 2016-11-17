package com.example.mislugares;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    public AdaptadorLugares adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {

            FileInputStream fis = getApplicationContext().openFileInput("datos.dat");
            ObjectInputStream is = new ObjectInputStream(fis);
            Lugares.vectorLugares = (List<Lugar>) is.readObject();
            is.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
            Lugares.vectorLugares = Lugares.ejemploLugares();
        }


        setContentView (R.layout.activity_main2);
        adaptador = new AdaptadorLugares(this);
        ListView listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(adaptador);
        listView.setOnItemClickListener(this);


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.config) {
            return true;
        }
        if (id == R.id.acercaDe) {
            lanzarAcercaDe(null);
            return true;
        }
        if (id == R.id.menu_buscar) {
            lanzarVistaLugar(null);
            return true;
        }

        if (id == R.id.nuevo) {
            Intent i = new Intent(this, EdicionLugar.class);
            i.putExtra("id", -1);
            startActivity(i);

        }
        return super.onOptionsItemSelected(item);

    }

    public void lanzarAcercaDe(View view) {
        Intent i = new Intent(this, AcercaDe.class);
        startActivity(i);
    }

    public void lanzarVistaLugar(View view) {
        final EditText entrada = new EditText(this);
        entrada.setText("0");
        new AlertDialog.Builder(this)
                .setTitle("Selección de lugar")
                .setMessage("Indica su id:")
                .setView(entrada)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        long id = Long.parseLong(entrada.getText().toString());
                        Intent i = new Intent(MainActivity.this, VistaLugar.class);

                        i.putExtra("id", id);
                        startActivity(i);

                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View vista, int posicion, long id) {
        Intent i = new Intent(this, VistaLugar.class);
        i.putExtra("id", id);
        Log.i("estic a on item click","valor de i"+i);
        startActivity(i);

    }

    @Override
    public void onRestart()
    {
        super.onRestart();//actualitza dades
        finish();
        startActivity(getIntent());
    }

//    startActivity(new Intent(getBaseContext(), SWADMain.class)
//            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
//    finish();

//    protected void onDestroy() {
//        super.onDestroy();
//        try {
//            FileOutputStream fos = getApplicationContext().openFileOutput("datos.dat", Context.MODE_PRIVATE);
//            ObjectOutputStream os = new ObjectOutputStream(fos);
//            os.writeObject(Lugares.vectorLugares);
//            os.close();
//            fos.close();
//        } catch (Exception e) {
//        }

   // }
}




