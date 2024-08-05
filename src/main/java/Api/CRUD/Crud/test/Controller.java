package Api.CRUD.Crud.test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class Controller {

    @PersistenceContext
    private EntityManager entityManager;

    @PostMapping
    @Transactional
    public ResponseEntity<String> createUsers (@RequestBody Users users) {
        List<Users> existingUsers = entityManager.createQuery(
                "SELECT u FROM Users u WHERE u.name = :name", Users.class)
                .setParameter("name", users.getName())
                .getResultList();

        if (!existingUsers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(users.getName() + "' já existe.");
        }

        entityManager.persist(users);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Usuário criado");
    }

    @GetMapping
    public List<Users> Userslisten() {
        return entityManager.createQuery("from Users", Users.class).getResultList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Users> searchUsers(@PathVariable Long id){
        Users users = entityManager.find(Users.class, id);
        if (users != null){
            return ResponseEntity.ok(users);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Users> attUsers(@PathVariable Long id, @RequestBody Users userDetails) {
        Users user = entityManager.find(Users.class, id);
        if (user != null) {
            user.setName(userDetails.getName());
            user.setAge(userDetails.getAge());
            entityManager.merge(user);
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteUser (@PathVariable Long id) {
        Users users = entityManager.find(Users.class, id);
        if (users != null){
            entityManager.remove(users);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
