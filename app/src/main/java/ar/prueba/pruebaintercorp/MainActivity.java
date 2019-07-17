package ar.prueba.pruebaintercorp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {


    private EditText txt_number_phone;
    private TextView txt_codigo;
    private Spinner sp_pais;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){

            Intent i=new Intent(this,CargarDatos.class);
            startActivity(i);
            finish();
        }else{
            txt_number_phone=findViewById(R.id.txt_phone_number);
            sp_pais=findViewById(R.id.sp_pais);
            txt_codigo=findViewById(R.id.txt_codigo_area);
            sp_pais.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    txt_codigo.setText(getResources().getStringArray(R.array.pais_codigo_area)[position]);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

    }
    public void sendNumber(View v){
        String number=txt_number_phone.getText().toString();
        String code=txt_codigo.getText().toString();
        if(validar(number)){
            Intent i=new Intent(this,EnviarCodigo.class);
            i.putExtra("numero",code+number);
            startActivity(i);
        }else{
            txt_number_phone.requestFocus();
        }
    }
    private boolean validar(String number){
        if(number.length()<10){
            Toast.makeText(this, "El numero telefonico debe tener 10 caracteres", Toast.LENGTH_LONG).show();
            return false;
        }else{
            try {
                Long.parseLong(number);
            } catch (NumberFormatException excepcion) {
                Toast.makeText(this, "No incluyas otros caracteres, solo números", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

}
