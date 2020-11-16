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

    String estado;


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
                estado = snapshot.getValue().toString();
                txt_estado.setText(estado);
                if(estado.equals("Activada")){
                    btn_estado.setText("Desactivar");
                }else{
                    btn_estado.setText("Activar");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void capturarTemperatura(){
        databaseReference.child("Temperatura").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int temperatura = Integer.parseInt(snapshot.getValue().toString());
                txt_temperatura.setText("Temperatura: "+temperatura+"°C");

                if(temperatura >= 50){
                    txt_temperatura.setBackgroundColor(Color.RED);
                }else if(temperatura >=40){
                    txt_temperatura.setBackgroundColor(Color.YELLOW);
                }else {
                    txt_temperatura.setBackgroundColor(Color.GREEN);
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
                if(estado.equals("Activada")){
                    databaseReference.child("Estado").setValue("Desactivada");

                }else{
                    databaseReference.child("Estado").setValue("Activada");
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