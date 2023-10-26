package Practica;

import java.util.List;
import java.util.concurrent.*;

public class Race extends Thread {
    //Lista de los tramos, solo se usa para leer
    private List<Racetrack> circuit = new CopyOnWriteArrayList<Racetrack>();
    //Informacion de los vehiculos
    private ConcurrentHashMap<String,Vehicle> vehiclesInfo = new ConcurrentHashMap<>();

    //CountDown para avisar al juez cuando los vehiculos terminen
    private CountDownLatch end;
    //CountDown para recibir la señal de salida
    private CountDownLatch signal;
    //CountDown para avisar al juez de que los vehiculos estan listos
    private CountDownLatch ready;
    //Numero de vehiculos que habra en la carrera
    private int nvehicles;
    //Numero de tramos que tendra el circuito
    private int nracetracks;
    //Executor que ejecutara los vehiculos cada 5 segundos
    private ScheduledExecutorService executor;

    //Score
    private Score score;

    public Race(int nvehicles, int nracetracks, CountDownLatch count, CountDownLatch count1, CountDownLatch count2){
        this.nvehicles = nvehicles;
        this.nracetracks = nracetracks;
        this.end = count;
        this.ready = count2;
        this.signal = count1;
        this.score = new Score(this.vehiclesInfo);


    }


    @Override
    public void run() {
        try {
            //Creacion de tramos
            for (int i = 0; i < this.nracetracks; i++){
                Racetrack racetrack = new Racetrack("tramo "+i);
                this.circuit.add(racetrack);
            }

            System.out.println("Race: Circuito generado");

            //Creacion de vehiculos
            for (int i = 0; i < this.nvehicles; i++){
                Vehicle vehicle = new Vehicle("Coche "+i,this.vehiclesInfo,this.circuit, this.end);
                //this.vehiclesInfo.putIfAbsent(vehicle.getName(),vehicle);
                this.ready.countDown();
            }

            //Esperando la señal de salida del juez
            this.signal.await();

            System.out.println("Race: El juez ha dado la señal!");

            this.executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
            for (Vehicle vehicle : vehiclesInfo.values()) {
                this.executor.scheduleAtFixedRate(vehicle, 0,5, TimeUnit.SECONDS);
            }
            this.executor.scheduleAtFixedRate(score, 0,1, TimeUnit.SECONDS);

        } catch (InterruptedException ignore) {
            //e.printStackTrace();
            System.out.println("Race: Carrera interrumpida");
            System.out.println("Race: Terminando con el executor...");
            this.interruptRace();
        }

    }

    public void end(){
        this.executor.shutdown();
    }

    public void interruptRace(){
        try {
            System.out.println("Se ha interrumpido la carrera");
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
            System.out.println("Resultados de la carrera");
            score.showScore();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void results(){
        this.score.showFinalScore();
    }


}
