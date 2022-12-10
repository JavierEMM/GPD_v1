package pe.edu.pucp.gdp.Entity;

import java.io.Serializable;

public class Carrito implements Serializable{

    private String id;
    private String cantidad;
    private Productos productos;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Productos getProductos() {
        return productos;
    }

    public void setProductos(Productos productos) {
        this.productos = productos;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }
}
