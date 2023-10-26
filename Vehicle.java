package Practica;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class Vehicle implements Runnable, Comparable<Vehicle>, Cloneable {
    //Nombre del vehiculo
    private String name;
    //Velocidad del vehiculo
    private int speed;
    //Tipo de motor
    private String motor;
    //Combustible del vehiculo
    private int fuel;
    //Tramo en el que se encuentra el vehiculo
    private String racetrack;
    //Tipo de tramo
    private String typeRacetrack;
    //Distancia total recorrida por el vehiculo
    private int distanceTotal;
    //Distancia de la vuelta extra
    private int distanceExtra = 0;
    //Distancia recorrida del tramo actual
    private int distanceRacetrack;
    //ID del tramo actual
    private int currentRacetrack;
    //Boxes
    private boolean boxes;
    //Simulaciones en boxes
    private int simulations;

    //Circuito que recorre el vehiculo
    private List<Racetrack> circuit;
    //Los vehiculos que participan
    private ConcurrentHashMap<String,Vehicle> vehicles;

    //CountDown latch para avisar cuando termina el vehiculo
    private CountDownLatch endCountDown;

    //Vuelta extra
    private boolean extra = false;
    //Fin de la carrera
    private boolean end = false;

    //Longitud del circuito
    private int distanceCircuit = 0;
    //Posicion final
    private int finalPosition = 100;
    private boolean gotFinalPosition = false;
    //Posicion actual
    private int currentPosition = 0;


    public Vehicle(String name, ConcurrentHashMap<String, Vehicle> vehicles, List<Racetrack> circuit, CountDownLatch count1){
        Random random = new Random();
        this.name = name;
        this.speed = random.nextInt(80 - 60 + 1) + 60;
        this.fuel = random.nextInt(100 - 70 + 1) + 70;
        this.distanceRacetrack = 0;
        this.currentRacetrack = 0;
        this.distanceTotal = 0;
        this.circuit = circuit;
        this.racetrack = this.circuit.get(currentRacetrack).getName();

        this.endCountDown = count1;

        this.boxes = false;

        boolean motorA = random.nextBoolean();
        if(motorA){
            this.motor = "Motor A";
        }else{
            this.motor = "Motor B";
        }

        for (int i = 0; i < this.circuit.size();i++){
            this.distanceCircuit += this.circuit.get(i).getKm();
        }

        this.vehicles = vehicles;
        this.vehicles.putIfAbsent(this.getName(),this);

    }




    @Override
    public void run() {
            //El coche esta compitiendo
            if(!this.isExtra() && this.getFuel()>0){

                this.boxes = false;
                //Se calcula la velocidad en funcion del motor y del tipo de tramo
                int finalSpeed = calcularVelocidad();
                //Se suman los km recorridos en el tramo actual
                this.distanceRacetrack += finalSpeed;
                //Se suman los km recorridos en esta simulacion al total
                this.distanceTotal += finalSpeed;

                //Si los km recorridos superan la distancia del tramo actual y/o de mas tramos se actualiza el tramo actual
                while (this.currentRacetrack < this.circuit.size() && this.distanceRacetrack >= this.circuit.get(currentRacetrack).getKm()){
                    //Km recorridos del tramo
                    this.distanceRacetrack -= this.circuit.get(currentRacetrack).getKm();
                    this.currentRacetrack++;

                }

                //Se actualiza el tramo actual
                if(this.currentRacetrack < this.circuit.size()){
                    this.racetrack = this.circuit.get(currentRacetrack).getName();
                    this.typeRacetrack = this.circuit.get(currentRacetrack).getType();
                }else{
                    this.racetrack = "final del circuito";
                    this.typeRacetrack = "Fin";
                }
                //Se comprueba si se ha pasado la linea de meta
                if(this.distanceTotal>=this.distanceCircuit){
                    //El vehiculo empezara la vuelta extra
                    this.extra = true;
                }

            }

            //El coche se va a boxes
            if(this.fuel<=0 && !this.extra){
                //El coche esta en boxes 2 simulaciones
                this.boxes = true;
                this.simulations++;
                if(this.simulations==3){
                    Random random = new Random();
                    this.fuel = random.nextInt(80 - 60 + 1) + 60;
                    this.simulations = 0;
                }
            }

            //El coche esta dando la vuelta extra
            if(this.extra && !this.end){
               this.boxes = false;
               this.distanceExtra += this.speed;
                if(this.distanceExtra>this.distanceCircuit){
                    this.boxes = true;
                    this.end = true;
                    this.endCountDown.countDown();
                }
            }

    }

    public int calcularVelocidad(){
        int finalSpeed = this.speed;
        if(this.currentRacetrack < this.circuit.size()){
            if(this.getMotor().equals("Motor A") && this.circuit.get(this.currentRacetrack).getType().equals("Recta")){
                finalSpeed += 30;
                this.fuel -= 20;
            }else if(this.getMotor().equals("Motor B") && this.circuit.get(this.currentRacetrack).getType().equals("Recta")){
                finalSpeed +=10;
                this.fuel -=10;
            }else if(this.getMotor().equals("Motor A") && this.circuit.get(this.currentRacetrack).getType().equals("Curva")){
                finalSpeed +=5;
                this.fuel -= 15;
            }else{
                finalSpeed +=20;
                this.fuel -=25;
            }
        }
        return finalSpeed;
    }

    public int getFuel() {
        return fuel;
    }

    public String getName() {
        return name;
    }

    public String getRacetrack() {
        return racetrack ;
    }

    public String getNextRacetrack(){
        String nextRacetrack = null;
        if(currentRacetrack < this.circuit.size()-1){
            nextRacetrack =  this.circuit.get(currentRacetrack +1).getName();
        }else{
            nextRacetrack =  "fin del circuito";
        }
        return nextRacetrack;
    }

    public String getTypeRacetrack(){
        return typeRacetrack;
    }

    public int getSpeed() {
        return speed;
    }

    public int getDistance(){
        return distanceTotal;
    }

    public String getMotor() {
        return motor;
    }

    public boolean isBoxes() {
        return boxes;
    }

    public boolean isExtra() {
        return extra;
    }

    public boolean isEnd() {
        return end;
    }

    public int getDistanceLeft(){
        return this.distanceCircuit - this.getDistance();
    }

    public int getFinalPosition() {
        return finalPosition;
    }

    public boolean isGotFinalPosition() {
        return gotFinalPosition;
    }

    public void setFinalPosition(int finalPosition) {
        this.finalPosition = finalPosition;

    }

    public void setGotFinalPosition(boolean gotFinalPosition) {
        this.gotFinalPosition = gotFinalPosition;
    }

    public void setCurrentPosition(int currentPosition){
        this.currentPosition = currentPosition;
    }

    public int getCurrentPosition(){
        return currentPosition;
    }

    public int compareTo(Vehicle v2) {
        if(this.getFinalPosition() != 100 || v2.getFinalPosition() != 100){
            return Integer.compare( this.getFinalPosition(),v2.getFinalPosition());
        }else{
            return Integer.compare( v2.getDistance(), this.getDistance());
        }
    }

    public Vehicle clone(){
        Vehicle obj=null;
        try{
            obj= (Vehicle) super.clone();
        }catch(CloneNotSupportedException ex){
            System.out.println(" no se puede duplicar");
        }
        return obj;
    }

}
