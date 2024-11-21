package co.edu.unipiloto.myappcash.models;

public class UserModel {
    String nombre;
    String correo;
    String usuario;
    String contrasena;

    public UserModel() {
    }

    public UserModel(String nombre, String correo, String usuario, String contrasena) {
        this.nombre = nombre;
        this.correo = correo;
        this.usuario = usuario;
        this.contrasena = contrasena;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}
