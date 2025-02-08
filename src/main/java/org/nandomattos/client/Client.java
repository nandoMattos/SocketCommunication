package org.nandomattos.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.nandomattos.entity.Categoria;
import org.nandomattos.model.dto.UserDTO;
import org.nandomattos.model.request.*;
import org.nandomattos.util.JsonConverter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Objects;

public class Client {
    public static void main(String[] args) throws IOException {

        String serverHostname = "10.20.8.11";
        int port = 20123;

        if (args.length > 0)
            serverHostname = args[0];
        System.out.println("Conectando ao host " + serverHostname + " na porta " + port);

        Socket echoSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            echoSocket = new Socket(serverHostname, port);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Host não encontrado: " + serverHostname);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Não foi possível conectar ao host : " + serverHostname);
            System.exit(1);
        }

        BufferedReader stdIn = new BufferedReader(
                new InputStreamReader(System.in));
        String userInput;

        System.out.println("Conectado ao servidor.");
        String token = null;
        while (true) {
            displayMenu();

            System.out.print("Selecione uma opção: ");
//            System.out.print("token: " + token + "\n");
            userInput = stdIn.readLine();

            switch (userInput) {
                case "1": // Login
                    handleLogin(stdIn, out);
                    break;
                case "2": // Logout
                    if(handleLogout(token, out)) {
                        break;
                    } else {
                        continue;
                    }
                case "3": // Cadastrar Usuário
                    handleCadastro(stdIn, out);
                    break;

                case "4":
                    if(handleListarUsuarios(token, out)){
                        break;
                    } else {
                        continue;
                    }

                case "5":
                    if(handleLocalizarUsuario(token, out, stdIn)){
                        break;
                    } else {
                        continue;
                    }

                case "6":
                    if(handleEditarUsuario(token, out, stdIn)){
                        break;
                    } else {
                        continue;
                    }

                case "7":
                    if(handleExcluirUsuario(token, out, stdIn)){
                        break;
                    } else {
                        continue;
                    }

                case "8":
                    if(handleSalvarCategoria(token, out, stdIn)){
                        break;
                    } else {
                        continue;
                    }

                case "9":
                    if(handleListarCategorias(token, out)){
                        break;
                    } else {
                        continue;
                    }

                default: {
                    System.out.println("Operação inválida.");
                    continue;
                }
            }

            // Wait for and display the server's response
            String serverResponse = in.readLine();
            if (serverResponse != null) {
                System.out.println("Resposta do Servidor: ");
                System.out.println(serverResponse);

                String operacao = extrairValor(serverResponse, "operacao");

                if (operacao == null){
                    operacao = userInput.equals("1") ? "login" : "logout";
                }

                // Ao realizar login, seta automaticamente o token
                if(operacao.equals("login")) {
                    Integer status = Integer.valueOf(Objects.requireNonNull(extrairValor(serverResponse, "status")));

                    if(status.equals(200)){
                        token = extrairValor(serverResponse, "token");
                    }
                }

                // Ao realizar logout, limpa o token
                if(operacao.equals("logout")) {
                    Integer status = Integer.valueOf(Objects.requireNonNull(extrairValor(serverResponse, "status")));

                    if(status.equals(200)){
                        token = null;
                    }
                }

            } else {
                System.out.println("No response from server. Closing connection.");
                closeResources(out, in, stdIn, echoSocket);
                break;
            }
        }
    }

    private static String extrairValor(String json, String chave) {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> jsonMap = objectMapper.readValue(json, Map.class);
            return jsonMap.get(chave).toString();
        } catch (Exception e) {
            return null;
        }
    }

    private static void displayMenu() {
        System.out.println("\n========= Menu =========");
        System.out.println("1. Login");
        System.out.println("2. Logout");
        System.out.println("3. Cadastrar Usuário");
        System.out.println("4. Listar Usuários");
        System.out.println("5. Localizar Usuário");
        System.out.println("6. Editar Usuário");
        System.out.println("7. Excluir Usuário");
        System.out.println("8. Salvar Categoria");
        System.out.println("9. Listar Categorias");
        System.out.println("===============");
    }

    private static void handleLogin(BufferedReader stdIn, PrintWriter out) throws IOException {
        System.out.print("Insira o RA: ");
        String ra = stdIn.readLine();

        System.out.print("Insira a senha: ");
        String senha = stdIn.readLine();

        enviarJsonServidor(new LoginRequest(ra, senha), out);
    }

    public static boolean handleLogout(String token, PrintWriter out) {
        if(token == null) {
            System.out.println("Faça o login antes de fazer logout.");
            return false;
        }

        enviarJsonServidor(new LogoutRequest(token), out);
        return true;
    }

    private static void handleCadastro(BufferedReader stdIn, PrintWriter out) throws IOException {
        System.out.print("Insira o nome: ");
        String nome = stdIn.readLine();

        System.out.print("Insira o RA: ");
        String ra = stdIn.readLine();

        System.out.print("Insira a senha: ");
        String senha = stdIn.readLine();

        out.println(JsonConverter.serialize(new CadastroUsuarioRequest(ra, senha, nome)));
    }

    private static boolean handleListarUsuarios(String token, PrintWriter out) {
        if(token == null) {
            System.out.println("Faça o login antes de solicitar a listagem usuarios.");
            return false;
        }

        enviarJsonServidor(new ListarUsuariosRequest(token), out);
        return true;
    }

    private static boolean handleLocalizarUsuario(String token, PrintWriter out, BufferedReader stdIn) throws IOException {
        if(token == null) {
            System.out.println("Faça o login antes de solicitar a localização de usuarios.");
            return false;
        }

        System.out.print("Insira o RA: ");
        String ra = stdIn.readLine();

        enviarJsonServidor(new LocalizarUsuarioRequest(token, ra), out);
        return true;
    }

    private static boolean handleEditarUsuario(String token, PrintWriter out, BufferedReader stdIn) throws IOException {
        if(token == null) {
            System.out.println("Faça o login antes de solicitar a exclusão de usuario.");
            return false;
        }

        System.out.print("Insira o RA: ");
        String ra = stdIn.readLine();

        System.out.print("Insira o nome: ");
        String nome = stdIn.readLine();

        System.out.print("Insira a senha: ");
        String senha = stdIn.readLine();

        UserDTO userDTO = UserDTO.builder()
                .ra(ra)
                .nome(nome)
                .senha(senha)
                .build();

        enviarJsonServidor(new EditarUsuarioRequest(token, userDTO), out);
        return true;
    }

    private static boolean handleExcluirUsuario(String token, PrintWriter out, BufferedReader stdIn) throws IOException {
        if(token == null) {
            System.out.println("Faça o login antes de solicitar a exclusão de usuario.");
            return false;
        }

        System.out.print("Insira o RA: ");
        String ra = stdIn.readLine();

        enviarJsonServidor(new ExcluirUsuarioRequest(token, ra), out);
        return true;
    }

    private static boolean handleSalvarCategoria(String token, PrintWriter out, BufferedReader stdIn) throws IOException {
        if(token == null) {
            System.out.println("Faça o login antes de solicitar a exclusão de usuario.");
            return false;
        }

        System.out.print("Insira o ID da categoria: ");
        Integer id = Integer.valueOf(stdIn.readLine());

        System.out.print("Insira o nome da categoria: ");
        String nomeCategoria = stdIn.readLine();

        Categoria categoria = Categoria.builder()
                .id(id)
                .nome(nomeCategoria)
                .build();

        enviarJsonServidor(new SalvarCategoriaRequest(token, categoria), out);
        return true;
    }

    private static boolean handleListarCategorias(String token, PrintWriter out) {
        if(token == null) {
            System.out.println("Faça o login antes de solicitar a exclusão de usuario.");
            return false;
        }

        enviarJsonServidor(new ListarCategoriasRequest(token), out);
        return true;
    }

    private static void enviarJsonServidor(Object obj, PrintWriter out) {
        String json = JsonConverter.serialize(obj);
        System.out.println("Enviando JSON para o servidor:");
        System.out.println(json);
        out.println(json);
    }

    private static void closeResources(PrintWriter out, BufferedReader in, BufferedReader stdIn, Socket socket) throws IOException {
        if (out != null) out.close();
        if (in != null) in.close();
        if (stdIn != null) stdIn.close();
        if (socket != null) socket.close();
    }
}
