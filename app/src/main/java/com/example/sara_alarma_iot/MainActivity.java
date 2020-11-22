package com.example.sara_alarma_iot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button btn_estado;
    TextView txt_estado, txt_temperatura;
    ListView lv_historial;
    Boolean estado;


    private List<String> listHistorial = new ArrayList<String>();
    ArrayAdapter<String>  historialArrayAdapter;

    // Se importan implementan las dependdencias de firebase en el module del gradle y se importan
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Llamado a los métodos de acción
        inicializarFirebase();
        enlazar();

        btn_estado.setBackgroundColor(Color.BLACK);

        capturarEstadoActual();
        capturarTemperatura();
        cambiarEstado();
        listarHisotrial();

    }

    //Captura el estado actual del alarma desde la base de datos de firebase
    private void  capturarEstadoActual(){

        databaseReference.child("Estado").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                estado = Boolean.parseBoolean(snapshot.getValue().toString());
                if(estado){
                    txt_estado.setText("ACTIVADA");
                    btn_estado.setText("Desactivar");
                }else{
                    btn_estado.setText("Activar");
                    txt_estado.setText("DESACTIVADA");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    ///Captura la temperatura que este almacenada en la db y dependiendo de en que se encuentre se le asigna un color 
    private void capturarTemperatura(){
        databaseReference.child("Temperatura").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double temperatura = Double.parseDouble(snapshot.getValue().toString());
                txt_temperatura.setText("Temperatura: "+temperatura+"°C");

                if(temperatura >= 50){
                    txt_temperatura.setBackgroundColor(0xffEC1935);
                }else if(temperatura >=40){
                    txt_temperatura.setBackgroundColor(0xffECBB19);
                }else {
                    txt_temperatura.setBackgroundColor(0xff629549);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        // firebaseDatabase.setPersistenceEnabled(true); Carga datos sin necesidad de estar conectado a internet, cuando detecta conexion los sube (En apps sencillas)
        databaseReference = firebaseDatabase.getReference();
    }

    /// Cambia el estado del alarma a ACTIVADO / DESACTIVADO en la base de datos de firebase
    private void cambiarEstado() {
        btn_estado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(estado){
                    databaseReference.child("Estado").setValue(false);

                }else{
                    databaseReference.child("Estado").setValue(true);
                }
            }
        });
    }
    
    //Lista el historial de las alertas,trayendo la informacion en la base de datos de firabse 
    private void listarHisotrial(){
        databaseReference.child("Historial").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listHistorial.clear();

                for (DataSnapshot objSnapshot : snapshot.getChildren()){
                    String alerta = objSnapshot.getValue().toString();
                    listHistorial.add(alerta);
                    System.out.println(alerta);


                    historialArrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, listHistorial);
                    lv_historial.setAdapter(historialArrayAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        
    }


    /// Crea el enlace con los componentes del diseño
    private void enlazar(){
        btn_estado = findViewById(R.id.btn_estado);
        txt_estado = findViewById(R.id.txt_estado);
        lv_historial = findViewById(R.id.lv_historial);
        txt_temperatura = findViewById(R.id.txt_temperatura);
    }


}