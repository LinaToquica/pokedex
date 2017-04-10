package lina.pokeapp;

/**
 * Created by LINA on 09/04/2017.
 */

public class Pokemon {

    private String nombre,imagen;
    private  int id,gender_rate;

    public Pokemon() {
    }

    public Pokemon(String nombre, String imagen, int id, int gender_rate) {
        this.nombre = nombre;
        this.imagen = imagen;
        this.id = id;
        this.gender_rate = gender_rate;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGender_rate() {
        return gender_rate;
    }

    public void setGender_rate(int gender_rate) {
        this.gender_rate = gender_rate;
    }
}
