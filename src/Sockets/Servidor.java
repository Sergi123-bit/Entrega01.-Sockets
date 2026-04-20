package Sockets;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Servidor {
    // Si el número de argumentos es menor a 2, detente
    public static void main(String[] args) {
        // args.length nos dice cuántas palabras hemos escrito
        if (args.length < 2) {
            System.out.println("Uso: Servidor <PORT_SERVIDOR> <PARAULA_CLAU_SERVIDOR>");
            return;
        }

        int PORT_SERVIDOR = Integer.parseInt(args[0]);       // Puerto donde escuchará el servidor
        String PARAULA_CLAU_SERVIDOR = args[1];              // Palabra clave para cerrar la conexión

        System.out.println("> Server chat at port " + PORT_SERVIDOR);

        // ServerSocket escucha conexiones entrantes en el puerto indicado
        try (ServerSocket serverSocket = new ServerSocket(PORT_SERVIDOR)) {
            System.out.println("> Inicializing server... OK");

            // accept() bloquea la ejecución hasta que un cliente se conecta
            Socket socket = serverSocket.accept();
            System.out.println("> Connection from client... OK");

            // Flujo de entrada: lee los mensajes que llegan del cliente
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // Flujo de salida: envía mensajes al cliente (autoFlush=true para enviar al instante)
            PrintWriter salida = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            // Scanner para leer lo que escribe el usuario del servidor por consola
            Scanner teclado = new Scanner(System.in);

            System.out.println("> Inicializing chat... OK");

            boolean actiu = true;
            while (actiu) {
                // readLine() bloquea hasta que el cliente envía un mensaje
                String missatgeClient = entrada.readLine();
                System.out.println("#Rebut del client: " + missatgeClient);

                // Comprueba si el cliente ha enviado la palabra clave de cierre
                if (missatgeClient.equalsIgnoreCase(PARAULA_CLAU_SERVIDOR)) {
                    System.out.println("> Client keyword detected!");
                    actiu = false;
                } else {
                    // Espera a que el servidor escriba su respuesta por consola
                    String resposta = teclado.nextLine();
                    salida.println(resposta);  // Envía la respuesta al cliente
                    System.out.println("#Enviar al client: " + resposta);

                    // Comprueba si el servidor ha escrito la palabra clave de cierre
                    if (resposta.equalsIgnoreCase(PARAULA_CLAU_SERVIDOR)) {
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
            socket.close();  // El ServerSocket se cierra solo por el try-with-resources
            System.out.println("> Closing server... OK");
            System.out.println("> Bye!");

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
