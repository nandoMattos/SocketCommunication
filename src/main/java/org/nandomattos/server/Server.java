package org.nandomattos.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.nandomattos.entity.Categoria;
import org.nandomattos.entity.User;
import org.nandomattos.mapper.UserMapper;
import org.nandomattos.model.dto.UserDTO;
import org.nandomattos.model.request.*;
import org.nandomattos.model.response.*;
import org.nandomattos.repository.CategoriaRepository;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;
//TODO: localizarCategoria e excluirCategoria
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
                System.out.println ("Mensagem do Cliente:");
                System.out.println (inputLine);
                String operation = extrairOperacao(inputLine);

                if (operation == null) {
                    enviarJsonCliente(
                            ErrorResponse.builder()
                                .status(401)
                                .mensagem("Operacao nao encontrada")
                                .build(),
                            out);
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
                    case "listarUsuarios": {
                        handleListarUsuarios(inputLine, out);
                        break;
                    }
                    case "localizarUsuario": {
                        handleLocalizarUsuario(inputLine, out);
                        break;
                    }
                    case "editarUsuario": {
                        handleEditarUsuario(inputLine, out);
                        break;
                    }
                    case "excluirUsuario": {
                        handleExcluirUsuario(inputLine, out);
                        break;
                    }
                    case "salvarCategoria": {
                        handleSalvarCategoria(inputLine, out);
                        break;
                    }
                    case "litarCategorias": {
                        handleListarCategorias(inputLine, out);
                        break;
                    }
                    default: {
                        enviarJsonCliente(
                                ErrorResponse.builder()
                                    .status(401)
                                    .mensagem("Operacao nao encontrada")
                                    .build(),
                                out);
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
        String operacao = "login";

        // Json inválido
        if(loginRequest == null) {
            enviarJsonCliente(
                    ErrorResponse.builder()
                        .status(401)
                        .mensagem("Não foi possível ler o json recebido.")
                        .build(),
                    out);
            return;
        }

        // Campos inválidos
        if(!Validation.camposValidos(loginRequest.getRa(), "A", loginRequest.getSenha())){
            enviarJsonCliente(
                    ErrorResponseOperacao.builder()
                        .status(401)
                        .operacao(operacao)
                        .mensagem("Os campos recebidos nao sao validos.")
                        .build(),
                    out);
            return;
        }

        // Busca o usuário
        User user = UserRepository.findByRa(loginRequest.getRa());

        // Credenciais incorrentas
        if (user == null || !user.getSenha().equals(loginRequest.getSenha())) {
            enviarJsonCliente(
                    ErrorResponseOperacao.builder()
                        .status(401)
                        .operacao(operacao)
                        .mensagem("Credenciais incorretas.")
                        .build(),
                    out);
            return;
        }

        // Atualiza logado = True
        user.setLogado(true);
        UserRepository.update(user);

        enviarJsonCliente(
                LoginSucessResponse.builder()
                    .status(200)
                    .token(user.getRa())
                    .build(),
                out);
    }

    private void handleLogout(String json, PrintWriter out) {
        LogoutRequest logoutRequest = JsonConverter.deserialize(json, LogoutRequest.class);

        if(logoutRequest == null) {
            enviarJsonCliente(
                    ErrorResponse.builder()
                        .status(401)
                        .mensagem("Não foi possível ler o json recebido.")
                        .build(),
                    out);
            return;
        }

        User user = UserRepository.findByRa(logoutRequest.getToken());
        if(user == null){
            enviarJsonCliente(
                    LogoutSucessResponse.builder()
                            .status(200)
                            .build(),
                    out);
            return;
        }

        user.setLogado(false);
        UserRepository.update(user);

        enviarJsonCliente(
                LogoutSucessResponse.builder()
                    .status(200)
                    .build(),
                out);
    }

    private void handleCadatro(String json, PrintWriter out) {
        CadastroUsuarioRequest cadastroUsuarioRequest = JsonConverter.deserialize(json, CadastroUsuarioRequest.class);
        String operacao = "cadastrarUsuario";

        // Json inválido
        if(cadastroUsuarioRequest == null) {
            enviarJsonCliente(
                    ErrorResponseOperacao.builder()
                        .status(401)
                        .operacao(operacao)
                        .mensagem("Não foi possível ler o json recebido.")
                        .build(),
                    out);
            return;
        }

        // Campos inválidos
        if(!Validation.camposValidos(cadastroUsuarioRequest.getRa(), cadastroUsuarioRequest.getNome(), cadastroUsuarioRequest.getSenha())){
            enviarJsonCliente(
                    ErrorResponseOperacao.builder()
                        .status(401)
                        .operacao(operacao)
                        .mensagem("Os campos recebidos nao sao validos.")
                        .build(),
                    out);
            return;
        }


        // RA já cadastrado
        User user = UserRepository.findByRa(cadastroUsuarioRequest.getRa());

        if (user != null) {
            enviarJsonCliente(
                    ErrorResponseOperacao.builder()
                        .status(401)
                        .operacao(operacao)
                        .mensagem("Não foi cadastrar pois o usuario informado ja existe")
                        .build(),
                    out);
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

        enviarJsonCliente(
                ErrorResponseOperacao.builder()
                    .status(201)
                    .operacao(operacao)
                    .mensagem("Cadastro realizado com sucesso.")
                    .build(),
                out);
    }

    private void handleListarUsuarios(String json, PrintWriter out){
        ListarUsuariosRequest listarUsuariosRequest = JsonConverter.deserialize(json, ListarUsuariosRequest.class);
        String operacao = "cadastrarUsuario";
        // Json inválido
        if(listarUsuariosRequest == null) {
            enviarJsonCliente(
                    ErrorResponseOperacao.builder()
                            .status(401)
                            .operacao(operacao)
                            .mensagem("Não foi possível ler o json recebido.")
                            .build(),
                    out);
            return;
        }

        // Usuário não autorizado
        if (!Validation.userEhAdm(listarUsuariosRequest.getToken())){
            enviarJsonCliente(
                    ErrorResponseOperacao.builder()
                            .status(401)
                            .operacao(operacao)
                            .mensagem("Acesso não autorizado")
                            .build(),
                    out
            );
            return;
        }
        List<User> userList = UserRepository.findAll();

        enviarJsonCliente(new ListarUsuariosResponse(UserMapper.listEntityToDto(userList)), out);
    }

    private void handleLocalizarUsuario(String json, PrintWriter out){
        LocalizarUsuarioRequest localizarUsuarioRequest = JsonConverter.deserialize(json, LocalizarUsuarioRequest.class);
        String operacao = "localizarUsuario";
        // Json inválido
        if(localizarUsuarioRequest == null) {
            enviarJsonCliente(
                    ErrorResponseOperacao.builder()
                            .status(401)
                            .operacao(operacao)
                            .mensagem("Não foi possível ler o json recebido.")
                            .build(),
                    out);
            return;
        }

        // Usuário não autorizado
        if (!Validation.userEhAdm(localizarUsuarioRequest.getToken()) && !Objects.equals(localizarUsuarioRequest.getRa(), localizarUsuarioRequest.getToken())){
            enviarJsonCliente(
                    ErrorResponseOperacao.builder()
                            .status(401)
                            .operacao(operacao)
                            .mensagem("Acesso não autorizado")
                            .build(),
                    out
            );
            return;
        }

        // Usuário não encontrado
        User user = UserRepository.findByRa(localizarUsuarioRequest.getRa());
        if(user == null) {
            enviarJsonCliente(
                    ErrorResponseOperacao.builder()
                            .status(401)
                            .operacao(operacao)
                            .mensagem("Usuário não encontrado")
                            .build(),
                    out
            );
            return;
        }

        enviarJsonCliente(new LocalizarUsuarioResponse(UserMapper.entityToDto(user)), out);
    }

    private void handleEditarUsuario(String json, PrintWriter out) {
        EditarUsuarioRequest editarUsuarioRequest = JsonConverter.deserialize(json, EditarUsuarioRequest.class);
        String operacao = "editarUsuario";

        // Json inválido
        if(editarUsuarioRequest == null) {
            enviarJsonCliente(
                    ErrorResponseOperacao.builder()
                            .status(401)
                            .operacao(operacao)
                            .mensagem("Não foi possível ler o json recebido.")
                            .build(),
                    out);
            return;
        }

        UserDTO userDTO = editarUsuarioRequest.getUsuario();

        // Campos inválidos
        if(!Validation.camposValidos(userDTO.getRa(), userDTO.getNome(), userDTO.getSenha())){
            enviarJsonCliente(
                    ErrorResponseOperacao.builder()
                            .status(401)
                            .operacao(operacao)
                            .mensagem("Os campos recebidos nao sao validos.")
                            .build(),
                    out);
            return;
        }

        // Usuário não autorizado
        if (!Validation.userEhAdm(editarUsuarioRequest.getToken()) && !Objects.equals(userDTO.getRa(), editarUsuarioRequest.getToken())){
            enviarJsonCliente(
                    ErrorResponseOperacao.builder()
                            .status(401)
                            .operacao(operacao)
                            .mensagem("Acesso não autorizado")
                            .build(),
                    out
            );
            return;
        }

        // Usuário não encontrado
        User user = UserRepository.findByRa(userDTO.getRa());
        if(user == null) {
            enviarJsonCliente(
                    ErrorResponseOperacao.builder()
                            .status(401)
                            .operacao(operacao)
                            .mensagem("Usuário não encontrado")
                            .build(),
                    out
            );
            return;
        }

        UserRepository.update(UserMapper.dtoToEntity(editarUsuarioRequest.getUsuario(), user.getAdmin(), user.getLogado()));
        enviarJsonCliente(
                ErrorResponseOperacao.builder()
                        .status(201)
                        .operacao(operacao)
                        .mensagem("Edição realizada com sucesso.")
                        .build(),
                out
        );
    }

    private void handleExcluirUsuario(String json, PrintWriter out) {
        ExcluirUsuarioRequest excluirUsuarioRequest = JsonConverter.deserialize(json, ExcluirUsuarioRequest.class);
        String operacao = "excluirUsuario";

        // Json inválido
        if(excluirUsuarioRequest == null) {
            enviarJsonCliente(
                    ErrorResponseOperacao.builder()
                            .status(401)
                            .operacao(operacao)
                            .mensagem("Não foi possível ler o json recebido.")
                            .build(),
                    out);
            return;
        }

        // Usuário não autorizado
        if (!Validation.userEhAdm(excluirUsuarioRequest.getToken())){
            enviarJsonCliente(
                    ErrorResponseOperacao.builder()
                            .status(401)
                            .operacao(operacao)
                            .mensagem("Acesso não autorizado")
                            .build(),
                    out
            );
            return;
        }

        // Usuário não encontrado
        User user = UserRepository.findByRa(excluirUsuarioRequest.getRa());
        if(user == null) {
            enviarJsonCliente(
                    ErrorResponseOperacao.builder()
                            .status(401)
                            .operacao(operacao)
                            .mensagem("Usuário não encontrado")
                            .build(),
                    out
            );
            return;
        }

        UserRepository.deleteByRa(user.getRa());
        enviarJsonCliente(
                ErrorResponseOperacao.builder()
                        .status(201)
                        .operacao(operacao)
                        .mensagem("Exclusão realizada com sucesso.")
                        .build(),
                out
        );
    }

    private void handleSalvarCategoria(String json, PrintWriter out) {
        SalvarCategoriaRequest salvarCategoriaRequest = JsonConverter.deserialize(json, SalvarCategoriaRequest.class);
        String operacao = "salvarCategoria";

        // Json inválido
        if(salvarCategoriaRequest == null) {
            enviarJsonCliente(
                    ErrorResponseOperacao.builder()
                            .status(401)
                            .operacao(operacao)
                            .mensagem("Não foi possível ler o json recebido.")
                            .build(),
                    out);
            return;
        }

        // Acesso não autorizado
        if(!Validation.userEhAdm(salvarCategoriaRequest.getToken())){
            enviarJsonCliente(
                    ErrorResponseOperacao.builder()
                            .status(401)
                            .operacao(operacao)
                            .mensagem("Acesso não autorizado")
                            .build(),
                    out
            );
            return;
        }

        Categoria categoria = CategoriaRepository.findById(salvarCategoriaRequest.getCategoria().getId());
        if(categoria == null) {
            CategoriaRepository.save(salvarCategoriaRequest.getCategoria());
        } else {
            CategoriaRepository.update(salvarCategoriaRequest.getCategoria());
        }

        enviarJsonCliente(
                ErrorResponseOperacao.builder()
                        .status(201)
                        .operacao(operacao)
                        .mensagem("Categoria salva com sucesso.")
                        .build(),
                out
        );
    }

    private void handleListarCategorias(String json, PrintWriter out) {
        ListarCategoriasRequest listarCategoriasRequest = JsonConverter.deserialize(json, ListarCategoriasRequest.class);
        String operacao = "listarCategorias";

        // Json inválido
        if(listarCategoriasRequest == null) {
            enviarJsonCliente(
                    ErrorResponseOperacao.builder()
                            .status(401)
                            .operacao(operacao)
                            .mensagem("Não foi possível ler o json recebido.")
                            .build(),
                    out);
            return;
        }

        enviarJsonCliente(new ListarCategoriasResponse(CategoriaRepository.findAll()), out);
    }
    private static void enviarJsonCliente(Object obj, PrintWriter out) {
        String json = JsonConverter.serialize(obj);
        System.out.println("Enviando JSON para o cliente:");
        System.out.println(json);
        out.println(json);
    }
}