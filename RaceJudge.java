package Practica;

import java.util.concurrent.*;

public class RaceJudge extends Thread {
    //CountDown para saber cuando los vehiculos han terminado
    private CountDownLatch end;
    //CountDown para saber cuando los vehiculos se han creado
    private CountDownLatch ready;
    //CountDown para dar la señal despues de la cuenta atras
    private CountDownLatch signal;
    //Carrera del juez
    private Race race;

    public RaceJudge(Race race, CountDownLatch count, CountDownLatch count1,CountDownLatch count2){
        this.race = race;
        this.end = count;
        this.signal = count1;
        this.ready = count2;
    }


    @Override
    public void start() {
        try {
            System.out.println("Juez: Esperando a que los coches esten preparados");
            this.ready.await();
            System.out.println("Juez: Coches preparados");
            System.out.println("Juez: Cuenta atras");
            while (this.signal.getCount() > 0){
                System.out.println("Juez: "+this.signal.getCount()+"...");
                this.signal.countDown();
            }

            System.out.println("Juez: Comienza la carrera!");
            this.end.await();
            this.race.end();
            System.out.println("Juez: La carrera ha terminado!");
            this.race.results();

        } catch (InterruptedException ignore) {
            //e.printStackTrace();
            System.out.println("Juez: Terminando...");
        }

    }

    public void interrupt(){
        this.race.interruptRace();
        while (this.end.getCount()>0){
            this.end.countDown();
        }
    }


}
