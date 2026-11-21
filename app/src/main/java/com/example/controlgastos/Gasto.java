package com.example.controlgastos;

public class Gasto {
    private String id;
    private String titulo;
    private String categoria;
    private double monto;
    private String fecha;
    private String uid;

    public Gasto() {
    }

    public Gasto(String id, String titulo, String categoria, double monto, String fecha, String uid) {
        this.id = id;
        this.titulo = titulo;
        this.categoria = categoria;
        this.monto = monto;
        this.fecha = fecha;
        this.uid = uid;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
}