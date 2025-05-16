import kareltherobot.*;
import java.awt.Color;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class MetroMedellin extends Robot implements Directions {
    public MetroMedellin(int street, int avenue, Direction direction, int beepers, Color color) {
        super(street, avenue, direction, beepers, color);
    }

    private static final Lock lock = new ReentrantLock();
    private static final Condition condition = lock.newCondition();
    private static boolean[] estaciones = new boolean[28];
    static {
        for (int i = 0; i < estaciones.length; i++) {
            estaciones[i] = true;
        }
    }
    private static boolean[] estacionesSalida = {true, true, true};

    public static void esperar(int estacion) {
        lock.lock();
        try {
            while (!estaciones[estacion]) {
                System.out.println("Esperando que se active la estación " + estacion + ".");
                condition.await();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    public static void esperarSalida(int estacion) {
        lock.lock();
        try {
            while (!estacionesSalida[estacion]) {
                System.out.println("Esperando que se active la estación " + estacion + ".");
                condition.await();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    public static void activar(int estacion) {
        lock.lock();
        try {
            System.out.println("Activando la estación " + estacion + ".");
            estaciones[estacion] = true;
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public static void activarSalida(int estacion) {
        lock.lock();
        try {
            System.out.println("Activando la estación " + estacion + ".");
            estacionesSalida[estacion] = true;
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public static void desactivar(int estacion) {
        lock.lock();
        try {
            System.out.println("Desactivando la estación " + estacion + ".");
            estaciones[estacion] = false;
        } finally {
            lock.unlock();
        }
    }

    public static void desactivarSalida(int estacion) {
        lock.lock();
        try {
            System.out.println("Desactivando la estación " + estacion + ".");
            estacionesSalida[estacion] = false;
        } finally {
            lock.unlock();
        }
    }

    public void esperarEstacion(int x) {
        esperar(x); //
        wait(250);
        move();
        desactivar(x); //
        pickBeeper();
        wait(3000);
        putBeeper();
        activar(x); //
    }

    public void turnRight() {
        turnLeft();
        turnLeft();
        turnLeft();
    }

    public static void wait(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public void salir(int x) {
        if (x == 1) {
            moveUntilWall();
            turnLeft();
            x = 2;
        }
        if (x == 2) {
            moveUntilWall();
            turnLeft();
            x = 3;
        }
        if (x == 3) {
            moveUntilWall();
            turnRight();
            x = 4;
        }
        if (x == 4) {
            moveUntilWall();
            turnLeft();
            move();
        }
    }

    public void moveUntilWall() {
        while (frontIsClear()) {
            move();
        }
    }

    public void startEstrella(int x) {
        move();
        turnRight();
        moveUntilWall();
        turnRight();
        move();
        turnLeft();
        moveUntilWall();
        turnRight();
        moveUntilWall();
        turnLeft();
        moveUntilWall();
        turnRight();
        moveUntilWall();
        turnLeft();

        for (int i = 0; i < 5; i++) {
            move();
        }

        turnLeft();
        moveUntilWall();
        turnRight();
        moveUntilWall();
        turnRight();
        moveUntilWall();
        turnLeft();

        Runnable[] camino = new Runnable[] {
            () -> move(),                            // x == 1
            () -> move(),                            // x == 2
            () -> move(),                            // x == 3
            () -> move(),                            // x == 4
            () -> move(),                            // x == 5
            () -> {
                move();
                turnRight();
            },                                        // x == 6
            () -> {
                move();
                turnLeft();
            },                                        // x == 7
            () -> move(),                            // x == 8
            () -> move(),                            // x == 9
            () -> {
                move();
                turnRight();
            },                                        // x == 10
            () -> move(),                            // x == 11
            () -> {
                move();
                turnLeft();
            },                                        // x == 12
            () -> {
                move();
                turnLeft();
            },                                        // x == 13
            () -> move()                             // x == 14
        };

        for (int i = 0; i < x && i < camino.length; i++) {
            camino[i].run();
        }
    }   

    public void setupEstrella(int x) {
        Runnable[] camino = new Runnable[] {
            () -> {
                move();
            },                            // x == 1
            () -> {
                move();
            },   
            () -> {
                move();
            },   
            () -> {
                move();
            },   
            () -> {
                move();
            },   
            () -> {
                move();
                turnRight();
            },   
            () -> {
                move();
                turnLeft();
            },   
            () -> {
                move();
            },   
            () -> {
                move();
            },   
            () -> {
                move();
                turnRight();
            },   
            () -> {
                move();
            },   
            () -> {
                move();
                turnLeft();
            },   
            () -> {
                move();
                turnLeft();
            },   
            () -> {
                wait(1000);
                esperarSalida(0);
                move();  // x == 14
            },   
        };

        for (int i = x - 1; i < camino.length; i++) {
        camino[i].run();
        }
    }

    public void startNiquia(int x) {
        moveUntilWall();    
        turnLeft();
        move();

        Runnable[] camino = new Runnable[] {
            () -> move(), // x == 1
            () -> {
                turnRight();
                move();
            }, // x == 2
            () -> move(), // x == 3
            () -> move(), // x == 4
            () -> {
                turnLeft();
                move();
            }, // x == 5
            () -> {
                turnLeft();
                move();
            } // x == 6
        };

        for (int i = 0; i < x && i < camino.length; i++) {
            camino[i].run();
        }
    }

    public void startSanJavier(int x) {
        move();
        turnRight();
        moveUntilWall();
        turnRight();
        move();
        turnLeft();
        moveUntilWall();
        turnRight();
        moveUntilWall();
        turnLeft();
        moveUntilWall();
        turnRight();
        moveUntilWall();
        turnLeft();

        for (int i = 0; i < 9; i++) {
            move();
        }

        turnRight();
        moveUntilWall();
        turnRight();
        move();
        turnLeft();

        Runnable[] camino = new Runnable[] {
            () -> move(), // x == 1
            () -> move(), // x == 2
            () -> move(), // x == 3
            () -> move(), // x == 4
            () -> {
                move();
                turnRight();
            }, // x == 5
            () -> move(), // x == 6
            () -> {
                move();
                turnLeft();
            }, // x == 7
            () -> {
                move();
                turnLeft();
            }, // x == 8
            () -> move() // x == 9
        };

        for (int i = 0; i < x && i < camino.length; i++) {
            camino[i].run();
        }
    }
    
    public void lineaA1() {
        pickBeeper();
        wait(3000);
        putBeeper();
        move();
        move();
        move();
        turnLeft();
        move();
        move();
        move();
        move();
        pickBeeper();
        wait(3000);
        putBeeper();
        move();
        move();
        turnRight();
        move();
        turnLeft();
        move();
        move();
        pickBeeper();
        wait(3000);
        putBeeper();
        move();
        turnRight();
        move();
        move();
        turnLeft();
        move();
        move();
        pickBeeper();
        wait(3000);
        putBeeper();
        move();
        turnRight();
        move();
        move();
        turnLeft();
        move();
        move();
        move();
        pickBeeper();
        wait(3000);
        putBeeper();
        move();
        move();
        turnLeft();
        move();
        move();
        move();
        pickBeeper();
        wait(3000);
        putBeeper();
        move();
        move();
        turnRight();
        move();
        move();
        pickBeeper();
        wait(3000);
        putBeeper();
        move();
        move();
        pickBeeper();
        wait(3000);
        putBeeper();
        move();
        move();
        pickBeeper();
        wait(3000);
        putBeeper();
        move();
        turnRight();
        move();
        pickBeeper();
        wait(3000);
        putBeeper();
        move();
        move();
        turnLeft();
        move();
        move();
        pickBeeper();
        wait(3000);
        putBeeper();
        move();
        move();
        move();
        pickBeeper();
        wait(3000);
        putBeeper();
        move();
        turnRight();
        move();
        turnLeft();
        move();
        move();
        pickBeeper();
        wait(3000);
        putBeeper();
        move();
        turnRight();
        move();
        pickBeeper();
        wait(3000);
        putBeeper();
    }

    public void lineaA2() {
        desactivarSalida(0); //
        pickBeeper();
        wait(3000);
        putBeeper();
        activarSalida(0); //
        move();
        move();
        turnLeft();
        move();
        esperarEstacion(0); //
        move();
        turnRight();
        move();
        turnLeft();
        move();
        esperarEstacion(1); //
        move();
        move();
        esperarEstacion(2); //
        move();
        turnRight();
        esperarEstacion(3); //
        move();
        move();
        turnLeft();
        move();
        esperarEstacion(4); //
        move();
        esperarEstacion(5); //
        move();
        esperarEstacion(6); //
        move();
        move();
        move();
        turnLeft();
        move();
        move();
        esperarEstacion(7); //
        move();
        move();
        turnRight();
        esperarEstacion(8); //
        move();
        move();
        turnRight();
        move();
        move();
        turnLeft();
        move();
        esperarEstacion(9); //
        move();
        turnRight();
        move();
        move();
        turnLeft();
        move();
        esperarEstacion(10); //
        move();
        turnRight();
        move();
        turnLeft();
        move();
        move();
        esperarEstacion(11); //
        move();
        move();
        move();
        turnRight();
        move();
        move();
        move();
        turnLeft();
        move();
        turnLeft();
        esperarEstacion(12); //
    }

    public void lineaB() {
        pickBeeper();
        wait(3000);
        putBeeper();
        move();
        move();
        turnLeft();
        move();
        move();
        move();
        move();
        pickBeeper();
        wait(3000);
        putBeeper();
        move();
        turnRight();
        move();
        turnLeft();
        move();
        move();
        move();
        pickBeeper();
        wait(3000);
        putBeeper();
        move();
        move();
        move();
        pickBeeper();
        wait(3000);
        putBeeper();
        move();
        move();
        turnLeft();
        move();
        turnRight();
        move();
        pickBeeper();
        wait(3000);
        putBeeper();
        turnLeft();
        turnLeft();
        move();
        move();
        move();
        pickBeeper();
        wait(3000);
        putBeeper();
        move();
        move();
        move();
        pickBeeper();
        wait(3000);
        putBeeper();
        move();
        move();
        turnRight();
        move();
        turnLeft();
        move();
        move();
        pickBeeper();
        wait(3000);
        putBeeper();
        move();
        move();
        move();
        turnRight();
        move();
        move();
        turnLeft();
        move();
        turnLeft();
        move();
    }

    public static void main(String[] args) {
        // Carga del mundo y configuración de la ventana
        World.readWorld("MetroMed.kwld");
        World.setVisible(true);
        //World.setDelay(15); // VELOCIDAD VERDADERA
        World.setDelay(1); // VELOCIDAD TEST


        MetroMedellin[] robotsLineaA = {
            new MetroMedellin(32, 15, East, 0, Color.blue),
            new MetroMedellin(32, 14, South, 0, Color.blue),
            new MetroMedellin(33, 14, South, 0, Color.blue),
            new MetroMedellin(34, 14, South, 0, Color.blue),
            new MetroMedellin(34, 13, East, 0, Color.blue),
            new MetroMedellin(34, 12, East, 0, Color.blue),
            new MetroMedellin(34, 11, East, 0, Color.blue),
            new MetroMedellin(34, 10, East, 0, Color.blue),
            new MetroMedellin(34, 9, East, 0, Color.blue),
            new MetroMedellin(34, 8, East, 0, Color.blue),
            new MetroMedellin(34, 7, East, 0, Color.blue),
            new MetroMedellin(34, 6, East, 0, Color.blue),
            new MetroMedellin(34, 5, East, 0, Color.blue),
            new MetroMedellin(34, 4, East, 0, Color.blue),
            new MetroMedellin(34, 3, East, 0, Color.blue),
            new MetroMedellin(34, 2, East, 0, Color.blue),
            new MetroMedellin(34, 1, East, 0, Color.blue),
            new MetroMedellin(35, 1, West, 0, Color.blue),
            new MetroMedellin(35, 2, West, 0, Color.blue),
            new MetroMedellin(35, 3, West, 0, Color.blue),
            new MetroMedellin(35, 4, West, 0, Color.blue),
            new MetroMedellin(35, 5, West, 0, Color.blue)
        };
        MetroMedellin[] robotsLineaB = {
            new MetroMedellin(35, 6, West, 0, Color.green),
            new MetroMedellin(35, 7, West, 0, Color.green),
            new MetroMedellin(35, 8, West, 0, Color.green),
            new MetroMedellin(35, 9, West, 0, Color.green),
            new MetroMedellin(35, 10, West, 0, Color.green),
            new MetroMedellin(35, 11, West, 0, Color.green),
            new MetroMedellin(35, 12, West, 0, Color.green),
            new MetroMedellin(35, 13, West, 0, Color.green),
            new MetroMedellin(35, 14, West, 0, Color.green),
            new MetroMedellin(35, 15, West, 0, Color.green)
        };

        List<Thread> threads = new ArrayList<>();
        Thread thread1 = new inicioNiquiaA(robotsLineaA[0], 6);
        thread1.start();
        threads.add(thread1);
        
        for (int i = 1; i < 4; i++) {
            int number = 6 - i;
            Thread thread = new inicioNiquiaB(robotsLineaA[i], number);
            //wait(1000);
            thread.start();
            threads.add(thread);
        }
        for (int i = 4; i < 7; i++) {
            int number = 6 - i;
            Thread thread = new inicioNiquiaC(robotsLineaA[i], number);
            //wait(1000);
            thread.start();
            threads.add(thread);
        }
        for (int i =7 ; i < 17; i++) {
            int number = 21 - i;
            Thread thread = new inicioEstrellaA(robotsLineaA[i], number);
            //wait(1000);
            thread.start();
            threads.add(thread);
        }
        for (int i = 17; i < 22; i++) {
            int number = 21 - i;
            Thread thread = new inicioEstrellaB(robotsLineaA[i], number);
            //wait(1000);
            thread.start();
            threads.add(thread);
        }
        for (int i = 0; i < 10; i++) {
            int number = 9 - i;
            Thread thread = new inicioSanJavierA(robotsLineaB[i], number);
            //wait(1000);
            thread.start();
            threads.add(thread);
        }
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        World.setDelay(15);
        System.out.println("Presione enter para iniciar el movimiento de las lineas a las 4:20");
        System.out.flush();
        Scanner am430 = new Scanner(System.in);
        String placeholder = am430.nextLine();

        Thread thread2 = new setupEstrella(robotsLineaA);
        thread2.start();
    }
}


class inicioNiquiaA extends Thread {
    private MetroMedellin robot;
    private int number;

    public inicioNiquiaA(MetroMedellin robot, int x) {
        this.robot = robot;
        this.number = x;
    }

    @Override
    public void run() {
        robot.salir(5);
        robot.startNiquia(number);
    }
}

class inicioNiquiaB extends Thread {
    private MetroMedellin robot;
    private int number;

    public inicioNiquiaB(MetroMedellin robot, int x) {
        this.robot = robot;
        this.number = x;
    }

    @Override
    public void run() {
        robot.salir(4);
        robot.startNiquia(number);
    }
}

class inicioNiquiaC extends Thread {
    private MetroMedellin robot;
    private int number;

    public inicioNiquiaC(MetroMedellin robot, int x) {
        this.robot = robot;
        this.number = x;
    }

    @Override
    public void run() {
        robot.salir(3);
        robot.startNiquia(number);
    }
}

class inicioEstrellaA extends Thread {
    private MetroMedellin robot;
    private int number;

    public inicioEstrellaA(MetroMedellin robot, int x) {
        this.robot = robot;
        this.number = x;
    }

    @Override
    public void run() {
        robot.salir(3);
        robot.startEstrella(number);
    }
}

class inicioEstrellaB extends Thread {
    private MetroMedellin robot;
    private int number;

    public inicioEstrellaB(MetroMedellin robot, int x) {
        this.robot = robot;
        this.number = x;
    }

    @Override
    public void run() {
        robot.salir(1);
        robot.startEstrella(number);
    }
}

class inicioSanJavierA extends Thread {
    private MetroMedellin robot;
    private int number;

    public inicioSanJavierA(MetroMedellin robot, int x) {
        this.robot = robot;
        this.number = x;
    }

    @Override
    public void run() {
        robot.salir(1);
        robot.startSanJavier(number);
    }
}

class lineaA1 extends Thread {
    private MetroMedellin robot;

    public lineaA1(MetroMedellin robot) {
        this.robot = robot;
    }

    @Override
    public void run() {
        robot.lineaA1();
    }
}

class setupEstrella extends Thread {
    private MetroMedellin[] robots;

    public setupEstrella(MetroMedellin[] robots) {
        this.robots = robots;
    }

    @Override
    public void run() {
        for (int i = 0; i<15; i++) {
            int robot = i + 7;
            int number = 15 - i;
            robots[robot].setupEstrella(number);
            Thread thread = new lineaA2(robots[robot], number);
            thread.start();
        }
    }
}

class lineaA2 extends Thread {
    private MetroMedellin robot;

    public lineaA2(MetroMedellin robot, int number) {
        this.robot = robot;
    }

    @Override
    public void run() {
        robot.lineaA2();
    }
}

class lineaB extends Thread {
    private MetroMedellin robot;

    public lineaB(MetroMedellin robot) {
        this.robot = robot;
    }

    @Override
    public void run() {
        robot.lineaB();
    }
}