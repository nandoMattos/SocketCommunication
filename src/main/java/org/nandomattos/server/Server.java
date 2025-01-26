package org.nandomattos.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.nandomattos.entity.User;
import org.nandomattos.model.request.CadastroUsuarioRequest;
import org.nandomattos.model.request.LoginRequest;
import org.nandomattos.model.request.LogoutRequest;
import org.nandomattos.model.response.ErrorResponse;
import org.nandomattos.model.response.ErrorResponseOperacao;
import org.nandomattos.model.response.LoginSucessResponse;
import org.nandomattos.model.response.LogoutSucessResponse;
import org.nandomattos.repository.UserRepository;
import org.nandomattos.util.JsonConverter;
import org.nandomattos.util.Validation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Map;

public class Server extends Thread {
    protected static boolean serverContinue = true;
    protected Socket clientSocket;

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        int port = 20000;

        try {
            serverSocket = new ServerSocket(port);
            System.out.println ("Server running on port: " + port);
            try {
                while (serverContinue) {
                    serverSocket.setSoTimeout(10000);
//                    System.out.println ("Waiting for Connection");
                    try {
                        new Server (serverSocket.accept());
                    }
                    catch (SocketTimeoutException ste) {
//                        System.out.println ("Timeout Occurred");
                    }
                }
            }
            catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
        }
        catch (IOException e) {
            System.err.println("Could not listen on port: 10008.");
            System.exit(1);
        }
        finally {
            try {
                System.out.println ("Closing Server Connection Socket");
                serverSocket.close();
            }
            catch (IOException e) {
                System.err.println("Could not close port: 10008.");
                System.exit(1);
            }
        }
    }

    private Server (Socket clientSoc) {
        clientSocket = clientSoc;
        start();
    }

    public void run() {
        System.out.println ("Nova conexão");

        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader( clientSocket.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                System.out.println ("Mensagem recebida: " + inputLine);
                String operation = extrairOperacao(inputLine);

                if (operation == null) {
                    out.println(JsonConverter.serialize(ErrorResponse.builder()
                            .status(401)
                            .mensagem("Operacao nao encontrada")
                            .build()));
                    continue;
                }

                switch (operation) {
                    case "login": {
                        handleLogin(inputLine, out);
                        break;
                    }
                    case "logout": {
                        handleLogout(inputLine, out);
                        break;
                    }
                    case "cadastrarUsuario": {
                        handleCadatro(inputLine, out);
                        break;
                    }
                    default: {
                        out.println(JsonConverter.serialize(ErrorResponse.builder()
                                .status(401)
                                .mensagem("Operacao nao encontrada")
                                .build()));
                    }
                }
            }

            out.close();
            in.close();
            clientSocket.close();
        }
        catch (IOException e) {
            System.err.println("Problem with Communication Server");
            System.exit(1);
        }
    }

    private String extrairOperacao(String inputLine) {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> jsonMap = objectMapper.readValue(inputLine, Map.class);
            return (String) jsonMap.get("operacao");
        } catch (Exception e) {
            return null;
        }
    }

    private void handleLogin(String json, PrintWriter out) {

        LoginRequest loginRequest = JsonConverter.deserialize(json, LoginRequest.class);

        // Json inválido
        if(loginRequest == null) {
            out.println(JsonConverter.serialize(ErrorResponse.builder()
                    .status(401)
                    .mensagem("Não foi possível ler o json recebido.")
                    .build()));
            return;
        }

        // Campos inválidos
        if(!Validation.camposValidos(loginRequest.getRa(), "A", loginRequest.getSenha())){
            out.println(JsonConverter.serialize(ErrorResponseOperacao.builder()
                    .status(401)
                    .operacao("login")
                    .mensagem("Os campos recebidos nao sao validos.")
                    .build()));
            return;
        }

        // Busca o usuário
        User user = UserRepository.findByRa(loginRequest.getRa());

        // Credenciais incorrentas
        if (user == null || !user.getSenha().equals(loginRequest.getSenha())) {
            out.println(JsonConverter.serialize(ErrorResponseOperacao.builder()
                    .status(401)
                    .operacao("login")
                    .mensagem("Credenciais incorretas.")
                    .build()));
            return;
        }

        // Atualiza logado = True
        user.setLogado(true);
        UserRepository.update(user);

        out.println(JsonConverter.serialize(LoginSucessResponse.builder()
                .status(200)
                .token(user.getRa())
                .build()));
    }

    private void handleLogout(String json, PrintWriter out) {
        LogoutRequest logoutRequest = JsonConverter.deserialize(json, LogoutRequest.class);

        if(logoutRequest == null) {
            out.println(JsonConverter.serialize(ErrorResponse.builder()
                    .status(401)
                    .mensagem("Não foi possível ler o json recebido.")
                    .build()));
            return;
        }

        User user = UserRepository.findByRa(logoutRequest.getToken());

        user.setLogado(false);
        UserRepository.update(user);

        out.println(JsonConverter.serialize(LogoutSucessResponse.builder()
                .status(200)
                .build()));
    }

    private void handleCadatro(String json, PrintWriter out) {
        CadastroUsuarioRequest cadastroUsuarioRequest = JsonConverter.deserialize(json, CadastroUsuarioRequest.class);

        // Json inválido
        if(cadastroUsuarioRequest == null) {
            out.println(JsonConverter.serialize(ErrorResponseOperacao.builder()
                    .status(401)
                    .operacao("cadastrarUsuario")
                    .mensagem("Não foi possível ler o json recebido.")
                    .build()));
            return;
        }

        // Campos inválidos
        if(!Validation.camposValidos(cadastroUsuarioRequest.getRa(), cadastroUsuarioRequest.getNome(), cadastroUsuarioRequest.getSenha())){
            out.println(JsonConverter.serialize(ErrorResponseOperacao.builder()
                    .status(401)
                    .operacao("cadastrarUsuario")
                    .mensagem("Os campos recebidos nao sao validos.")
                    .build()));
            return;
        }


        // RA já cadastrado
        User user = UserRepository.findByRa(cadastroUsuarioRequest.getRa());

        if (user != null) {
            out.println(JsonConverter.serialize(ErrorResponseOperacao.builder()
                    .status(401)
                    .operacao("cadastrarUsuario")
                    .mensagem("Não foi cadastrar pois o usuario informado ja existe")
                    .build()));
            return;
        }

        User novoUser = User.builder()
                .ra(cadastroUsuarioRequest.getRa())
                .nome(cadastroUsuarioRequest.getNome())
                .senha(cadastroUsuarioRequest.getSenha())
                .admin(false)
                .logado(false)
                .build();

        UserRepository.save(novoUser);
        out.println(JsonConverter.serialize(ErrorResponseOperacao.builder()
                .status(201)
                .operacao("cadastrarUsuario")
                .mensagem("Cadastro realizado com sucesso.")
                .build()));
    }

}