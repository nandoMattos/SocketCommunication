import org.junit.jupiter.api.Test;
import org.nandomattos.entity.User;
import org.nandomattos.repository.UserRepository;

import java.util.List;

public class TestHibernate {

    @Test
    public void findAll() {
        List<User> users = UserRepository.findAll();
        for(User user : users) {
            System.out.println(user.getRa());
            System.out.println(user.getSenha());
        }
    }

    @Test
    public void create() {
        User user = new User();
        user.setRa("2258005");
        user.setSenha("123456");

        UserRepository.save(user);
    }

    @Test
    public void findById() {
        String ra = "2258005";
        User user = UserRepository.findByRa(ra);
        System.out.println(user);
    }

    @Test
    public void findByRa() {
        String ra = "2258005";
        User user = UserRepository.findByRa(ra);
        System.out.println(user);
    }
}
