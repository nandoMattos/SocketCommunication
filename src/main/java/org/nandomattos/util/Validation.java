package org.nandomattos.util;

import org.nandomattos.entity.User;
import org.nandomattos.repository.UserRepository;

import java.util.regex.Pattern;

public class Validation {

    public static boolean camposValidos(String ra, String nome, String senha){

        // RA
        String regexRA = "^[0-9]{7}$";
        boolean raValido = Pattern.matches(regexRA, ra);

        // Nome
        String regexNome = "^[A-Z ]{1,50}$";
        boolean nomeValido = Pattern.matches(regexNome, nome);

        // Senha
        String regexSenha = "^[A-Za-z]{8,20}$";
        boolean senhaValida = Pattern.matches(regexSenha, senha);

        return raValido && nomeValido && senhaValida;
    }

    public static boolean userEhAdm(String ra) {
        User user = UserRepository.findByRa(ra);

        return user != null && user.getAdmin();
    }
}
