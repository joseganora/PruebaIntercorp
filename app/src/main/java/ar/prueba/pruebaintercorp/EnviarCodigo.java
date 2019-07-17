package ar.prueba.pruebaintercorp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class EnviarCodigo extends AppCompatActivity {
    private String TAG="verificacion_telefonica";
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId,phoneNumber;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;
    private boolean yaEnviado;
    private EditText txt_code;
    private TextView txt_numero_tel;
    private LinearLayout lyt_cargando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enviar_codigo);
        mAuth = FirebaseAuth.getInstance();
        setCallback();
        yaEnviado=false;
        Bundle extras = getIntent().getExtras();
        txt_code=findViewById(R.id.txt_code);
        txt_numero_tel=findViewById(R.id.txt_numero_telefonico);
        lyt_cargando=findViewById(R.id.lyt_cargando_enviar_code);

        if(extras!=null){
            phoneNumber=extras.getString("numero");
            txt_numero_tel.setText(phoneNumber);
            sendSMS(null);
        }
    }
    private void setCallback(){
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);
                signInWithPhoneAuthCredential(credential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Error de credenciales
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // El sms no fue enviado
                    // ...
                }
                lyt_cargando.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent:" + verificationId);
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };
    }
    public void sendSMS(View v){
        if(v==null){
            PhoneAuthProvider phoneAuth=PhoneAuthProvider.getInstance();

            phoneAuth.verifyPhoneNumber(
                    phoneNumber,        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    this,               // Activity (for callback binding)
                    mCallbacks);
        }else{
            resendVerificationCode(phoneNumber);
        }

    }

    private void resendVerificationCode(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks,
                mResendToken);
    }
    public void sendCode(View v) {
        String code=txt_code.getText().toString();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
        lyt_cargando.setVisibility(View.VISIBLE);
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            cargarUser(user);

                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(EnviarCodigo.this, "Codigo invalido", Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(EnviarCodigo.this, "Error al autentificar", Toast.LENGTH_LONG).show();
                            }
                            lyt_cargando.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }
    private void cargarUser(FirebaseUser user) {
        User.setUser(user);

        DatabaseReference myRef = Database.getDB().getReference("usuarios/"+user.getUid());

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    User.setNombre(dataSnapshot.child("nombre").getValue().toString());
                    User.setApellido(dataSnapshot.child("apellido").getValue().toString());
                    User.setEdad(dataSnapshot.child("edad").getValue(Integer.class));
                    User.setFechaNacimiento(dataSnapshot.child("fecha_nacimiento").getValue().toString());

                }
                lyt_cargando.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(EnviarCodigo.this, CargarDatos.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                lyt_cargando.setVisibility(View.INVISIBLE);
            }
        });
    }
}
