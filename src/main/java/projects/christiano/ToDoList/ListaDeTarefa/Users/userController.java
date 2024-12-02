package projects.christiano.ToDoList.ListaDeTarefa.Users;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")

public class userController {
    @Autowired
    private IUserRepository userRepository;

    @PostMapping({"/",""})
    public ResponseEntity<?> create(@RequestBody UserModel userModel) {
        try {
            var user =
                    this.userRepository.findByUsername(userModel.getUsername());
            if (user != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nome de usuário já criado.");
            } else {
                var passwordHash =
                        BCrypt.withDefaults().hashToString(12, userModel.getPassword().toCharArray());
                userModel.setPassword(passwordHash);
                var userCreated =
                        this.userRepository.save(userModel);
                return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);
            }
        } catch (DataAccessException e) {
            // Erros relacionados ao banco de dados (conexão, restrições, etc.)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao acessar o banco de dados: " + e.getMessage());
        } catch (Exception e) {
            // Qualquer outro erro inesperado
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro inesperado: " + e.getMessage());
        }
    }
}
