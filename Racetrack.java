package Practica;

import java.util.Random;

public class Racetrack {
    //Longitud del tramo
    private int km;
    //Nombre del tramo
    private String name;
    //Tipo de tramo
    private String type;

    public Racetrack(String name){
        Random random = new Random();
        this.name = name;
        this.km = random.nextInt(150 - 100 + 1) + 100;
        boolean recta = random.nextBoolean();
        if(recta){
            this.type = "Recta";
        }else{
            this.type = "Curva";
        }
    }

    public int getKm() {
        return km;
    }

    public String getName() {
        return name;
    }

    public String getType(){
        return type;
    }
}
