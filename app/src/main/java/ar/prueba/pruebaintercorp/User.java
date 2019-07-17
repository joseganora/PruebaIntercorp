package ar.prueba.pruebaintercorp;

import com.google.firebase.auth.FirebaseUser;

public  class User {
    private static FirebaseUser user;
    private static String nombre,apellido, fechaNacimiento;
    private static int edad;

    public static FirebaseUser getUser() {
        return user;
    }

    public static void setUser(FirebaseUser user) {
        User.user = user;
    }

    public static String getNombre() {
        return nombre;
    }

    public static void setNombre(String nombre) {
        User.nombre = nombre;
    }

    public static String getApellido() {
        return apellido;
    }

    public static void setApellido(String apellido) {
        User.apellido = apellido;
    }

    public static int getEdad() {
        return edad;
    }

    public static void setEdad(int edad) {
        User.edad = edad;
    }

    public static String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public static void setFechaNacimiento(String fechaNacimiento) {
        User.fechaNacimiento = fechaNacimiento;
    }



    public static void limpiarAll() {
        user=null;
        nombre=null;
        fechaNacimiento=null;;
        apellido=null;
        edad=0;
    }
}
