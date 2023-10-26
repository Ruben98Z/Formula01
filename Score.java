package Practica;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Score implements Runnable {
    private ConcurrentHashMap<String,Vehicle> vehicles;
    private int finalPostion = 1;
    public Score(ConcurrentHashMap<String,Vehicle> vehicles){
        this.vehicles = vehicles;
    }

    public void showScore(){
        int position = 1;
        List<Vehicle> sorted = sortVehicles();


        System.out.println("-------------------------- Score ---------------------------------");

        for(Vehicle vehicle:sorted){
                Vehicle vehicle1 = vehicles.get(vehicle.getName());
                System.out.println("---Posicion "+position+" : "+vehicle.getName()+"---");
                System.out.println("Tipo de motor: "+vehicle.getMotor()+", velocidad por defecto: "+vehicle.getSpeed());
                if(!vehicle.isExtra()){
                    System.out.println("Tramo en el que se encuentra: "+vehicle.getRacetrack()+", tipo: "+vehicle.getTypeRacetrack());
                    System.out.println("Siguiente tramo: "+vehicle.getNextRacetrack());
                    System.out.println("Distancia restante hasta la meta: "+vehicle.getDistanceLeft());
                    System.out.println("Distancia recorrida en total: "+vehicle.getDistance());
                }else if(vehicle.isEnd()){
                    System.out.println("Ha terminado la vuelta extra");
                }else{
                    System.out.println("Esta dando la vuelta extra");
                }

                if(vehicle.isBoxes()){
                    System.out.println("Se encuentra en boxes");
                }else{
                    System.out.println("Combustible restante: "+vehicle.getFuel());
                }
                if(vehicle.getDistanceLeft() <= 0){
                    if(!vehicle.isGotFinalPosition()){
                        vehicle1.setFinalPosition(finalPostion);
                        finalPostion++;
                        vehicle1.setGotFinalPosition(true);
                    }
                }
                if (vehicle.isGotFinalPosition()){
                    System.out.println("Ha acabado en la posicion: "+vehicle.getFinalPosition());
                }
                vehicle1.setCurrentPosition(position);
                position++;


        }

        System.out.println("-------------------------------------------------------------------");

    }

    public void showFinalScore(){
        int posicion = 1;
        List<Vehicle> sorted = sortVehicles();
        System.out.println("-------------------------- Score ---------------------------------");
        for(Vehicle vehicle:sorted){
                System.out.println("---Poscion "+posicion+" : "+vehicle.getName()+"---");
                System.out.println("Distancia total recorrida : "+vehicle.getDistance());
                if(vehicle.isBoxes()){
                    System.out.println("Se encuentra en boxes");
                }else{
                    System.out.println("Combustible restante: "+vehicle.getFuel());
                }
                posicion++;

        }
        System.out.println("-------------------------------------------------------------------");
    }


    public List <Vehicle> sortVehicles(){
        List<Vehicle> copy = new ArrayList<>();
        for(Vehicle vehicle:vehicles.values()){
            copy.add(vehicle.clone());
        }
        List<Vehicle> sorted = copy
                .stream()
                .sorted()
                .collect(Collectors.toList());


        return sorted;
    }



    @Override
    public void run() {
        showScore();
    }
}
