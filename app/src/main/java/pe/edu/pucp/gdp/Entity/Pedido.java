package pe.edu.pucp.gdp.Entity;

import java.util.ArrayList;

public class Pedido {

    private String id;
    private String numeroPedido;
    private ArrayList<Carrito> carritoArrayList;
    private String estado;
    private User user;
    private String precio;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumeroPedido() {
        return numeroPedido;
    }

    public void setNumeroPedido(String numeroPedido) {
        this.numeroPedido = numeroPedido;
    }

    public ArrayList<Carrito> getCarritoArrayList() {
        return carritoArrayList;
    }

    public void setCarritoArrayList(ArrayList<Carrito> carritoArrayList) {
        this.carritoArrayList = carritoArrayList;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }
}
