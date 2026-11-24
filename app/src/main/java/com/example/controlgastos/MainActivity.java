package com.example.controlgastos;

import android.content.Intent;
import android.os.Bundle;
import android.app.AlertDialog;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.ListenerRegistration;
import java.util.ArrayList;
import java.util.List;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private GastoRepository repository;
    private EditText etTitulo, etCategoria, etMonto, etFecha;
    private ArrayAdapter<String> adapter;
    private final List<Gasto> gastos = new ArrayList<>();
    private ListenerRegistration gastosListener;
    private String gastoSeleccionadoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        repository = new GastoRepository();
        TextView tvUsuario = findViewById(R.id.tvUsuarioConectado);
        tvUsuario.setText("Hola: " + user.getEmail());

        etTitulo = findViewById(R.id.etTitulo);
        etCategoria = findViewById(R.id.etCategoria);
        etMonto = findViewById(R.id.etMonto);
        etFecha = findViewById(R.id.etFecha);
        ListView lvGastos = findViewById(R.id.lvGastos);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        lvGastos.setAdapter(adapter);

        findViewById(R.id.btnGuardar).setOnClickListener(v -> guardarGasto());
        findViewById(R.id.btnActualizar).setOnClickListener(v -> actualizarGasto());
        lvGastos.setOnItemClickListener((parent, view, position, id) -> seleccionarGasto(position));
        lvGastos.setOnItemLongClickListener((parent, view, position, id) -> {
            confirmarEliminacion(position);
            return true;
        });
        escucharGastos(user.getUid());

        Button btnSalir = findViewById(R.id.btnCerrarSesion);
        btnSalir.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void escucharGastos(String uid) {
        gastosListener = repository.getCollection()
                .whereEqualTo("uid", uid)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        mostrarMensaje("No se pudieron cargar los gastos: " + error.getLocalizedMessage());
                        return;
                    }
                    if (snapshot == null) return;
                    gastos.clear();
                    gastos.addAll(snapshot.toObjects(Gasto.class));
                    adapter.clear();
                    for (Gasto gasto : gastos) {
                        adapter.add(gasto.getFecha() + " · " + gasto.getTitulo() + " (" + gasto.getCategoria() + ") — "
                                + String.format(java.util.Locale.getDefault(), "%.2f", gasto.getMonto()));
                    }
                    adapter.notifyDataSetChanged();
                    if (gastoSeleccionadoId != null && gastos.stream().noneMatch(g -> gastoSeleccionadoId.equals(g.getId()))) {
                        limpiarFormulario();
                    }
                });
    }

    private Gasto leerFormulario() {
        String titulo = etTitulo.getText().toString().trim();
        String categoria = etCategoria.getText().toString().trim();
        String montoTexto = etMonto.getText().toString().trim().replace(',', '.');
        String fecha = etFecha.getText().toString().trim();
        if (TextUtils.isEmpty(titulo) || TextUtils.isEmpty(categoria) || TextUtils.isEmpty(montoTexto) || TextUtils.isEmpty(fecha)) {
            mostrarMensaje("Completa descripción, categoría, monto y fecha");
            return null;
        }
        final double monto;
        try {
            monto = Double.parseDouble(montoTexto);
        } catch (NumberFormatException e) {
            mostrarMensaje("Ingresa un monto válido");
            return null;
        }
        if (!Double.isFinite(monto) || monto <= 0) {
            mostrarMensaje("El monto debe ser mayor que cero");
            return null;
        }
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return null;
        return new Gasto(null, titulo, categoria, monto, fecha, user.getUid());
    }

    private void guardarGasto() {
        Gasto gasto = leerFormulario();
        if (gasto == null) return;
        gasto.setId(repository.generarId());
        repository.guardarGasto(gasto)
                .addOnSuccessListener(unused -> {
                    limpiarFormulario();
                    mostrarMensaje("Gasto guardado");
                })
                .addOnFailureListener(e -> mostrarMensaje("No se pudo guardar: " + e.getLocalizedMessage()));
    }

    private void seleccionarGasto(int position) {
        Gasto gasto = gastos.get(position);
        gastoSeleccionadoId = gasto.getId();
        etTitulo.setText(gasto.getTitulo());
        etCategoria.setText(gasto.getCategoria());
        etMonto.setText(String.valueOf(gasto.getMonto()));
        etFecha.setText(gasto.getFecha());
    }

    private void actualizarGasto() {
        if (gastoSeleccionadoId == null) {
            mostrarMensaje("Selecciona un gasto de la lista para actualizarlo");
            return;
        }
        Gasto gasto = leerFormulario();
        if (gasto == null) return;
        gasto.setId(gastoSeleccionadoId);
        repository.guardarGasto(gasto)
                .addOnSuccessListener(unused -> {
                    limpiarFormulario();
                    mostrarMensaje("Gasto actualizado");
                })
                .addOnFailureListener(e -> mostrarMensaje("No se pudo actualizar: " + e.getLocalizedMessage()));
    }

    private void confirmarEliminacion(int position) {
        Gasto gasto = gastos.get(position);
        new AlertDialog.Builder(this)
                .setTitle("Eliminar gasto")
                .setMessage("¿Eliminar ‘" + gasto.getTitulo() + "’?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Eliminar", (dialog, which) -> repository.eliminarGasto(gasto.getId())
                        .addOnSuccessListener(unused -> {
                            if (gasto.getId().equals(gastoSeleccionadoId)) limpiarFormulario();
                            mostrarMensaje("Gasto eliminado");
                        })
                        .addOnFailureListener(e -> mostrarMensaje("No se pudo eliminar: " + e.getLocalizedMessage())))
                .show();
    }

    private void limpiarFormulario() {
        gastoSeleccionadoId = null;
        etTitulo.setText("");
        etCategoria.setText("");
        etMonto.setText("");
        etFecha.setText("");
    }

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        if (gastosListener != null) gastosListener.remove();
        super.onDestroy();
    }
}
