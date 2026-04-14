package Sockets;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Uso: Cliente <host> <PORT_SERVIDOR> <PARAULA_CLAU_CLIENT>");
            return;
        }

        String host = args[0];                               // Dirección del servidor (ej: localhost)
        int PORT_SERVIDOR = Integer.parseInt(args[1]);       // Puerto al que conectarse
        String PARAULA_CLAU_CLIENT = args[2];                // Palabra clave para cerrar la conexión

        System.out.println("> Client chat to port " + PORT_SERVIDOR);

        // A diferencia del servidor, el cliente crea directamente un Socket
        // indicando host y puerto — no necesita ServerSocket ni accept()
        try (Socket socket = new Socket(host, PORT_SERVIDOR)) {
            System.out.println("> Inicializing client... OK");

            // Flujo de entrada: lee los mensajes que llegan del servidor
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // Flujo de salida: envía mensajes al servidor (autoFlush=true para enviar al instante)
            PrintWriter salida = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            // Scanner para leer lo que escribe el usuario del cliente por consola
            Scanner teclado = new Scanner(System.in);

            System.out.println("> Inicializing chat... OK");

            boolean actiu = true;
            while (actiu) {
                // El cliente siempre envía primero — espera que el usuario escriba
                String missatge = teclado.nextLine();
                salida.println(missatge);  // Envía el mensaje al servidor
                System.out.println("#Enviar al servidor: " + missatge);

                // Comprueba si el cliente ha escrito la palabra clave de cierre
                if (missatge.equalsIgnoreCase(PARAULA_CLAU_CLIENT)) {
                    System.out.println("> Client keyword detected!");
                    actiu = false;
                } else {
                    // readLine() bloquea hasta que el servidor envía su respuesta
                    String resposta = entrada.readLine();
                    System.out.println("#Rebut del servidor: " + resposta);

                    // Comprueba si el servidor ha enviado la palabra clave de cierre
                    if (resposta.equalsIgnoreCase(PARAULA_CLAU_CLIENT)) {
                        System.out.println("> Client keyword detected!");
                        actiu = false;
                    }
                }
            }

            // Cierre ordenado de todos los recursos
            System.out.println("> Closing chat... OK");
            teclado.close();
            entrada.close();
            salida.close();
            // El socket se cierra solo por el try-with-resources
            System.out.println("> Closing client... OK");
            System.out.println("> Bye!");

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}