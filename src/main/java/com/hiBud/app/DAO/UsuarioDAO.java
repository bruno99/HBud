package com.hiBud.app.DAO;

import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hiBud.app.Constantes;
import com.hiBud.app.Firebase.Usuario;
import com.hiBud.app.Logic.LUsuario;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class UsuarioDAO {

    private static UsuarioDAO usuarioDAO;
    private DatabaseReference referenceUsuarios;
    private StorageReference referenceFotoDePerfil;

    private UsuarioDAO() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        referenceUsuarios = database.getReference(Constantes.NODO_USUARIOS);
        referenceFotoDePerfil = storage.getReference("Fotos/FotoPerfil/" + getKeyUsuario());
    }

    public static UsuarioDAO getInstancia() {
        if (usuarioDAO == null) usuarioDAO = new UsuarioDAO();
        return usuarioDAO;
    }

    public String getKeyUsuario() {
        return FirebaseAuth.getInstance().getUid();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public long fechaDeCreacionLong() {
        return Objects.requireNonNull(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getMetadata()).getCreationTimestamp();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public long fechaDeUltimaVezQueSeLogeoLong() {
        return Objects.requireNonNull(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getMetadata()).getLastSignInTimestamp();
    }

    public void obtenerInformacionDeUsuarioPorLLave(final String key, final IDevolverUsuario iDevolverUsuario) {
        referenceUsuarios.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                LUsuario lUsuario = new LUsuario(key, usuario);
                iDevolverUsuario.devolverUsuario(lUsuario);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                iDevolverUsuario.devolverError(databaseError.getMessage());
            }
        });

    }

    public void subirFotoUri(Uri uri, final IDevolverUrlFoto iDevolverUrlFoto) {
        String nombreFoto = "";
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("SSS.ss-mm-hh-dd-MM-yyyy", Locale.getDefault());
        nombreFoto = simpleDateFormat.format(date);
        final StorageReference fotoReferencia = referenceFotoDePerfil.child(nombreFoto);
        fotoReferencia.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }
                return fotoReferencia.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri uri = task.getResult();
                    assert uri != null;
                    iDevolverUrlFoto.devolerUrlString(uri.toString());
                }
            }
        });
    }

    public interface IDevolverUsuario {
        public void devolverUsuario(LUsuario lUsuario);

        public void devolverError(String error);
    }

    public interface IDevolverUrlFoto {
        public void devolerUrlString(String url);
    }

}