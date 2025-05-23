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
    private static boolean[] estaciones = new boolean[50];
    private static boolean salida = false;
    private static int llenado = 0;
    private static boolean setupSanAntonio = false;
    private static boolean setupSanJavier = false;
    private static boolean saliendo = false;
    static {
        for (int i = 0; i < estaciones.length; i++) {
            estaciones[i] = true;
        }
    }
    private static boolean[] estacionesSalida = {true, true, true, true};

    public boolean isCondicionActiva() {
        return salida;
    }

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

    public static void esperarVia() {
        lock.lock();
        try {
            while (!estaciones[46] && !estaciones[47] && !estaciones[49]) {
                System.out.println("Esperando que se libere la via.");
                condition.await();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
            desactivar(49);
            desactivar(46);
            desactivar(47);
        }
    }

    public static void esperarViaA1() {
        lock.lock();
        try {
            while (!estaciones[46] && !estaciones[47]) {
                System.out.println("Esperando que se libere la via.");
                condition.await();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    public static void esperarSanAntonio() {
        lock.lock();
        try {
            while (!setupSanAntonio) {
                System.out.println("Esperando que termine el setup de San Antonio.");
                condition.await();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    public static void esperarSanJavier() {
        lock.lock();
        try {
            while (!setupSanJavier) {
                System.out.println("Esperando que termine el setup de San Javier.");
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

    public void entrar() {
        int num = 32 - llenado;
        llenado++;
        moveUntilWall();
        turnLeft();
        move();
        move();
        turnRight();
        move();
        turnRight();
        move();
        Runnable[] camino = new Runnable[] {
            () -> {
                move();
                turnLeft();
            },
            () -> move(),
            () -> move(),
            () -> move(),
            () -> move(),
            () -> move(),
            () -> move(),
            () -> move(),
            () -> move(),
            () -> move(),
            () -> move(),
            () -> move(),
            () -> move(),
            () -> move(),
            () -> {
                move();
                turnLeft();
            },
            () -> {
                move();
                turnLeft();
            },
            () -> move(),
            () -> move(),
            () -> move(),
            () -> move(),
            () -> move(),
            () -> move(),
            () -> move(),
            () -> move(),
            () -> move(),
            () -> move(),
            () -> move(),
            () -> move(),
            () -> {
                move();
                turnRight();
            },
            () -> move(),
            () -> {
                move();
                turnLeft();
            },
            () -> move(), // x == 32
        };

        for (int i = 0; i < num && i < camino.length; i++) {
            camino[i].run();
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
                wait(250);
                move();  // x == 10
                wait(1000);
            },   
        };

        for (int i = x - 1; i < camino.length; i++) {
        camino[i].run();
        }
    }

    public void setupNiquia(int x) {
        Runnable[] camino = new Runnable[] {
            () -> move(),   
            () -> move(), 
            () -> move(), 
            () -> move(), 
            () -> move(), 
            () -> {
                turnRight();
                move();
            },   
            () -> move(), 
            () -> move(),  
            () -> {
                turnLeft();
                move();
            },   
            () -> {
                turnLeft();
                wait(1000);
                esperarSalida(1);
                wait(250);
                move();  // x == 10
                wait(1000);
            },   
        };

        for (int i = x - 1; i < camino.length; i++) {
        camino[i].run();
        }
    }

    public void setupSanJavier(int x) {
        Runnable[] camino = new Runnable[] {
            () -> {
                move();
                setupSanJavier = true;
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
                esperarSalida(2);
                wait(250);
                move();
            },
        };

        for (int i = x - 1; i < camino.length; i++) {
        camino[i].run();
        }
    }

    public void setupSanAntonio(int x) {
        Runnable[] camino = new Runnable[] {
            () -> {
                move();
                setupSanAntonio = true;
            },   
            () -> move(), 
            () -> {
                move();
            },
            () -> {
                turnLeft();
                esperarSalida(3);
                wait(250);
                move();
                turnRight();
                move();
                turnLeft();
                turnLeft();
            },   
        };

        for (int i = x - 1; i < camino.length; i++) {
        camino[i].run();
        }
    }

    public void startNiquiaA(int x) {
        moveUntilWall();    
        turnLeft();
        move();

        Runnable[] camino = new Runnable[] {
            () -> move(),
            () -> {
                turnRight();
                move();
            },
            () -> move(),
            () -> move(),
            () -> {
                turnLeft();
                move();
            },
            () -> {
                turnLeft();
                move();
            }
        };

        for (int i = 0; i < x && i < camino.length; i++) {
            camino[i].run();
        }
    }

    public void startNiquiaB(int x) {
        startEstrella(14);
        moveUntilWall();
        turnLeft();
        moveUntilWall();
        turnRight();
        move();
        turnLeft();
        moveUntilWall();
        turnRight();
        moveUntilWall();
        turnLeft();
        moveUntilWall();
        turnLeft();
        moveUntilWall();
        turnRight();
        moveUntilWall();
        turnRight();
        moveUntilWall();
        turnLeft();
        moveUntilWall();
        turnRight();
        moveUntilWall();
        turnLeft();
        moveUntilWall();
        turnRight();
        move();
        turnLeft();
        Runnable[] camino = new Runnable[] {
            () -> move(),
            () -> move(),
            () -> move(),
            () -> move(), // x = 4
        };
        
        for (int i = 0; i < x && i < camino.length; i++) {
            camino[i].run();
        }
    }

    public void startSanJavierA(int x) {
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

    public void startSanJavierB(int x) {
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
        moveUntilWall();
        turnRight();
        moveUntilWall();
        turnLeft();
        move();
        turnLeft();
        moveUntilWall();
        turnLeft();
        moveUntilWall();
        turnRight();
        move();
        turnLeft();
        for (int i=0; i<4; i++) {
            move();
        }

        Runnable[] camino = new Runnable[] {
            () -> move(), // x == 1
            () -> move(), // x == 2
            () -> move(), // x == 3
            () -> move(), // x == 4
            () -> {
                turnLeft();
                move();
                turnRight();
                move();
                turnLeft();
                turnLeft();
            }
        };

        for (int i = 0; i < x && i < camino.length; i++) {
            camino[i].run();
        }
    }
    
    public void lineaA1() {
        desactivarSalida(1); //
        pickBeeper();
        wait(3000);
        putBeeper();
        activarSalida(1); //
        move();
        move();
        move();
        turnLeft();
        move();
        move();
        move();
        esperarEstacion(13); //
        move();
        move();
        turnRight();
        move();
        turnLeft();
        move();
        esperarEstacion(14); //
        move();
        turnRight();
        move();
        move();
        turnLeft();
        move();
        esperarEstacion(15); //
        move();
        turnRight();
        move();
        move();
        turnLeft();
        move();
        move();
        esperar(16);
        wait(250);
        desactivar(16); //
        move();
        pickBeeper();
        wait(3000);
        putBeeper();
        esperar(45);
        wait(250);
        activar(16); //
        desactivar(45); //
        move();
        esperar(46);
        wait(250);
        activar(45); //
        desactivar(46);
        move();
        turnLeft();
        esperar(47);
        wait(250);
        activar(46);
        desactivar(47);
        move();
        activar(47);
        move();
        esperarEstacion(17); //
        move();
        move();
        turnRight();
        move();
        esperarEstacion(18); //
        move();
        esperarEstacion(19); //
        move();
        esperarEstacion(20); //
        move();
        turnRight();
        esperarEstacion(21); //
        move();
        move();
        turnLeft();
        move();
        esperarEstacion(22); //
        move();
        move();
        esperarEstacion(23); //
        move();
        turnRight();
        move();
        turnLeft();
        move();
        esperarEstacion(24); //
        move();
        turnRight();
        esperarEstacion(25); //
        move();
        turnLeft();
        move();
        turnLeft();
        esperarSalida(0); //
        wait(250);
        move();
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
        esperar(7);
        wait(250);
        desactivar(7); //
        move();
        pickBeeper();
        wait(3000);
        putBeeper();
        esperar(48);
        wait(250);
        desactivar(48); //
        activar(7); //
        move();
        wait(250);
        esperar(49);
        wait(500);
        esperar(49);
        desactivar(49);
        activar(48);
        move();
        turnRight();
        esperarEstacion(8); //
        activar(49);
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
        esperarEstacion(12); //
        move();
        turnLeft();
        move();
        turnLeft();
        esperarSalida(1); //
        wait(250);
        move();
    }

    public int lineaB2() {
        desactivarSalida(2); //
        pickBeeper();
        wait(3000);
        putBeeper();
        activarSalida(2); //
        move();
        move();
        turnLeft();
        move();
        move();
        move();
        esperarEstacion(26); //
        move();
        turnRight();
        move();
        turnLeft();
        move();
        move();
        esperarEstacion(27); //
        esperarSanAntonio(); //
        esperar(34); //
        wait(250);
        desactivar(34); //
        move();
        esperar(35); //
        wait(250);
        activar(34); //
        desactivar(35); //
        move();
        esperar(28); //
        if (!salida) {
            wait(250);
            desactivar(28);
            activar(35);
            move();
            pickBeeper();
            wait(3000);
            putBeeper();
        } else {
            desactivar(28); //
            activar(35);
            move();
        }
        esperar(36);
        wait(250);
        activar(28);
        desactivar(36);
        move();
        esperar(37);
        wait(250);
        activar(36);
        desactivar(37);
        move();
        turnLeft();
        esperar(38);
        if (!salida) {
            esperarSalida(3);
            activar(37);
            desactivarSalida(3);
            wait(250);
            move();
            turnRight();
            move();
            turnLeft();
            turnLeft();
            return 1;
        } else {
            esperarSalida(3);
            wait(350);
            activar(37);
            desactivar(38);
            move();
            turnLeft();
            esperar(39);
            wait(250);
            activar(38);
            desactivar(39);
            move();
            esperar(40);
            wait(250);
            activar(39);
            desactivar(40);
            move();
            esperar(41);
            wait(250);
            activar(40);
            desactivar(41);
            move();
            turnRight();
            esperar(42);
            wait(250);
            desactivar(42);
            activar(41);
            move();
            esperar(43);
            wait(250);
            activar(42);
            desactivar(43);
            move();
            esperar(44);
            wait(250);
            activar(43);
            desactivar(44);
            move();
            wait(3000);
            esperarVia();
            lineaAB();
            return 2;
        }
    }

    public void lineaB1() {
        desactivarSalida(3); //
        pickBeeper();
        wait(3000);
        putBeeper();
        esperar(38);
        wait(250);
        activarSalida(3); //
        desactivar(38);
        move();
        esperar(39);
        activar(38);
        desactivar(39);
        move();
        esperarEstacion(29); //
        activar(39);
        esperar(41);
        wait(250);
        activar(40);
        desactivar(41);
        move();
        activar(41);
        move();
        esperarEstacion(30); //
        move();
        move();
        turnRight();
        move();
        turnLeft();
        move();
        esperarEstacion(31); //
        esperarSanJavier(); //
        moveUntilWall();
        turnRight();
        esperarEstacion(32); //
        move();
        turnLeft();
        move();
        turnLeft();
        esperarSalida(2); //
        wait(250);
        move();  
    }

    public void lineaAB() {
        desactivar(49);
        desactivar(46);
        desactivar(47);
        desactivar(46);
        wait(250);
        move();
        turnRight();
        desactivar(47);
        wait(250);
        move();
        activar(46);
        turnLeft();
        desactivar(49);
        wait(1000);
        desactivar(49);
        move();
        desactivar(49);
        activar(47);
        esperarEstacion(8); //
        activar(44);
        activar(49);
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
        esperarEstacion(12); //
        move();
        turnLeft();
        move();
        turnLeft();
        esperarSalida(1); //
        wait(250);
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
        for (int i = 4; i < 11; i++) {
            int number = 6 - i;
            Thread thread = new inicioNiquiaC(robotsLineaA[i], number);
            //wait(1000);
            thread.start();
            threads.add(thread);
        }
        for (int i = 11 ; i < 17; i++) {
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
            int number = i;
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
        String placeholder1 = am430.nextLine();

        Thread thread2 = new setupEstrella(robotsLineaA);
        Thread thread3 = new setupNiquia(robotsLineaA);
        Thread thread4 = new setupSanAntonio(robotsLineaB);
        Thread thread5 = new setupSanJavier(robotsLineaB);
        thread2.start();
        thread3.start();
        thread4.start();
        wait(3500);
        thread5.start();
        System.out.println("Presione enter para iniciar el movimiento de las lineas a las 4:20");
        System.out.flush();
        Scanner pm11 = new Scanner(System.in);
        String placeholder2 = pm11.nextLine();
        salida = true;
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
        robot.startNiquiaA(number);
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
        robot.startNiquiaA(number);
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
        if (number < 0) {
            robot.startNiquiaB(number + 5);
        } else {
            robot.startNiquiaA(number);
        }
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
        robot.startEstrella(number+4);
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
        robot.startEstrella(number+4);
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
        if (number < 5) {
            robot.startSanJavierB(5-number);
        } else {
            robot.startSanJavierA(9-(number-5));
        }
    }
}

class setupEstrella extends Thread {
    private MetroMedellin[] robots;

    public setupEstrella(MetroMedellin[] robots) {
        this.robots = robots;
    }

    @Override
    public void run() {
        for (int i = 0; i<11; i++) {
            int robot = i + 11;
            int number = 11 - i;
            robots[robot].setupEstrella(number);
            Thread thread = new lineaA(robots[robot], false);
            thread.start();
        }
    }
}

class setupNiquia extends Thread {
    private MetroMedellin[] robots;

    public setupNiquia(MetroMedellin[] robots) {
        this.robots = robots;
    }

    @Override
    public void run() {
        for (int i = 0; i<11; i++) {
            int robot = i;
            int number = 11 - i;
            robots[robot].setupNiquia(number);
            Thread thread = new lineaA(robots[robot], true);
            thread.start();
        }
    }
}

class setupSanJavier extends Thread {
    private MetroMedellin[] robots;

    public setupSanJavier(MetroMedellin[] robots) {
        this.robots = robots;
    }

    @Override
    public void run() {
        for (int i = 0; i<5; i++) {
            int robot = i + 5;
            int number = 5 - i;
            robots[robot].setupSanJavier(number);
            Thread thread = new lineaB(robots[robot], false);
            thread.start();
        }
    }
}

class setupSanAntonio extends Thread {
    private MetroMedellin[] robots;

    public setupSanAntonio(MetroMedellin[] robots) {
        this.robots = robots;
    }

    @Override
    public void run() {
        for (int i = 0; i<5; i++) {
            int robot = i;
            int number = 5 - i;
            robots[robot].setupSanAntonio(number);
            Thread thread = new lineaB(robots[robot], true);
            thread.start();
        }
    }
}

class lineaA extends Thread {
    private MetroMedellin robot;
    private boolean Niquia = false;

    public lineaA(MetroMedellin robot, boolean Niquia) {
        this.robot = robot;
        this.Niquia = Niquia;
    }

    @Override
    public void run() {
        while (!(robot.isCondicionActiva())) {
            if (!Niquia) {
                robot.lineaA2();
                Niquia = true;
            }
            if (!(robot.isCondicionActiva())) {
                robot.lineaA1();
                Niquia = false;
            }
        }
        if (!Niquia) {
            robot.lineaA2();
        }
        robot.entrar();
    }
}

class lineaB extends Thread {
    private MetroMedellin robot;
    private boolean SanAntonio;
    private int num = 1;

    public lineaB(MetroMedellin robot, boolean SanAntonio) {
        this.robot = robot;
        this.SanAntonio = SanAntonio;
    }

    @Override
    public void run() {
        while (!(robot.isCondicionActiva())) {
            if (!SanAntonio) {
                num = robot.lineaB2();
                SanAntonio = true;
            }
            if (SanAntonio && num == 1) {
                robot.lineaB1();
                SanAntonio = false;
                if (robot.isCondicionActiva()) {
                    robot.lineaB2();
                }
            }
        }
        robot.entrar();
    }
}
