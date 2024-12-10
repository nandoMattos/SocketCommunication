import org.junit.jupiter.api.Test;
import org.nandomattos.entity.User;
import org.nandomattos.repository.UserRepository;

import java.util.List;

public class TestHibernate {

    @Test
    public void findAll() {
        UserRepository userRepository = new UserRepository();
        List<User> users = userRepository.findAll();
        for(User user : users) {
            System.out.println(user.getUserId());
            System.out.println(user.getRa());
            System.out.println(user.getSenha());
        }
    }

    @Test
    public void create() {
        UserRepository userRepository = new UserRepository();
        User user = new User();
        user.setRa("2258005");
        user.setSenha("123456");

        userRepository.save(user);
    }

    @Test
    public void findById() {
        UserRepository userRepository = new UserRepository();
        Long userId = 1L;
        User user = userRepository.findById(userId);
        System.out.println(user);
    }

    @Test
    public void findByRa() {
        UserRepository userRepository = new UserRepository();
        String ra = "2258005";
        User user = userRepository.findByRa(ra);
        System.out.println(user);
    }
}
