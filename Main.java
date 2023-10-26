package Practica;


import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class Main {
    public static final int nvehicles = 4;
    public static final int nracetracks = 4;
    public static final int cuentaAtras = 5;
    public static Race race;
    public static RaceJudge judge;

    public static void main(String[] args) {
        interruptRace();
        createRace();
        System.exit(0);
    }


    public static void createRace() {
        CountDownLatch count = new CountDownLatch(nvehicles);
        CountDownLatch signal = new CountDownLatch(cuentaAtras);
        CountDownLatch ready = new CountDownLatch(nvehicles);
        race = new Race(nvehicles,nracetracks,count, signal, ready);
        judge = new RaceJudge(race,count, signal, ready);

        race.start();
        judge.start();

        try {
            judge.join();
            race.join();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void interruptRace(){
        Thread th = new Thread(() -> {
            Scanner sc = new Scanner(System.in);
            String interruptRace = sc.next();
            if (interruptRace!=null){
                judge.interrupt();
            }
            sc.close();
        }, "Hilo de interrupcion");

        th.start();
    }

}
