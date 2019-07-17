package ar.prueba.pruebaintercorp;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class CargarDatos extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText txt_apellido,txt_nombre,txt_edad,txt_fecha;
    private Button btn_enviar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cargar_datos);
        mAuth = FirebaseAuth.getInstance();
        txt_apellido=findViewById(R.id.txt_apellido);
        txt_nombre=findViewById(R.id.txt_nombre);
        txt_edad=findViewById(R.id.txt_edad);
        txt_fecha=findViewById(R.id.txt_fecha_nacimiento);
        btn_enviar=findViewById(R.id.btn_enviar_datos);
        if(User.getUser()==null){
            FirebaseUser currentUser = mAuth.getCurrentUser();
            User.setUser(currentUser);
            cargarUser();
        }else{
            actualizarUI();
        }
        txt_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
    }
    public void salir(View v){
        mAuth.signOut();
        User.limpiarAll();
        Intent i=new Intent(this,MainActivity.class);
        startActivity(i);
        finish();
    }
    private void actualizarUI(){
        txt_apellido.setText(User.getApellido());
        txt_nombre.setText(User.getNombre());
        if(User.getEdad()==0)
            txt_edad.setText("");
            else txt_edad.setText(User.getEdad()+"");
        txt_fecha.setText(User.getFechaNacimiento());

    }
    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because january is zero
                final String selectedDate = day + "/" + (month+1) + "/" + year;
                txt_fecha.setText(selectedDate);
            }
        });
        newFragment.setSoloFechaPasada(true);
        newFragment.show(this.getSupportFragmentManager(), "datePicker");
    }
    private void cargarUser() {
        DatabaseReference myRef = Database.getDB().getReference("usuarios/"+User.getUser().getUid());
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    User.setNombre(dataSnapshot.child("nombre").getValue().toString());
                    User.setApellido(dataSnapshot.child("apellido").getValue().toString());
                    User.setEdad(dataSnapshot.child("edad").getValue(Integer.class));
                    User.setFechaNacimiento(dataSnapshot.child("fecha_nacimiento").getValue().toString());
                    actualizarUI();
                    btn_enviar.setText("Actualizar Mis Datos");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(CargarDatos.this, "Error al conectarse a la base de datos", Toast.LENGTH_LONG).show();
            }
        });
    }
    public void sendData( View v){
        User.setApellido(txt_nombre.getText().toString());
        User.setNombre(txt_apellido.getText().toString());
        User.setEdad(Integer.parseInt(txt_edad.getText().toString()));
        User.setFechaNacimiento(txt_fecha.getText().toString());
        DatabaseReference myRef = Database.getDB().getReference("usuarios/"+User.getUser().getUid());
        myRef.child("nombre").setValue(User.getNombre());
        myRef.child("apellido").setValue(User.getApellido());
        myRef.child("edad").setValue(User.getEdad());
        myRef.child("fecha_nacimiento").setValue(User.getFechaNacimiento(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                alert_info("Datos cargados exitosamente");

            }
        });

    }
    private void alert_info(String msj) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msj)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        // Create the AlertDialog object and return it
        builder.create().show();
    }
}
