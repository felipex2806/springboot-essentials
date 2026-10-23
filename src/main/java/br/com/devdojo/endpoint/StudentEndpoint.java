
package br.com.devdojo.endpoint;

// Importa a classe responsável por representar erros personalizados

// Importa a exceção personalizada para quando um aluno não é encontrado
import br.com.devdojo.error.ResourceNotFoundException;

// Importa a entidade Student, que representa um aluno
import br.com.devdojo.model.Student;

// Importa o repositório responsável pelas operações com o banco de dados
import br.com.devdojo.repository.StudentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


// @RestController indica que esta classe recebe requisições HTTP
// e retorna dados diretamente, normalmente em formato JSON.
@RestController

// Define o endereço base de todos os endpoints desta classe.
// Exemplo: http://localhost:8080/students
@RequestMapping("students")
public class StudentEndpoint {

    // Declara o repositório que será utilizado para acessar os alunos.
    // final significa que a referência será atribuída uma única vez,
    // neste caso, por meio do construtor.
    private final StudentRepository studentDAO;

    // @Autowired permite que o Spring injete automaticamente
    // uma implementação de StudentRepository neste construtor.
    // Esse processo é chamado de injeção de dependência.
    @Autowired
    public StudentEndpoint(StudentRepository studentDAO) {
        this.studentDAO = studentDAO;
    }

    // GET /students
    // Lista todos os alunos cadastrados no banco de dados.
    @GetMapping
    public ResponseEntity<?> listAll() {

        // findAll() busca todos os registros.
        // HttpStatus.OK representa o status HTTP 200.
        return new ResponseEntity<>(studentDAO.findAll(), HttpStatus.OK);
    }

    // GET /students/{id}
    // Busca um aluno pelo ID recebido na URL.
    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable("id") Long id) {

        // Verifica se existe um aluno com esse ID.
        verifyIfStudentExists(id);

        // Busca o aluno pelo ID.
        // orElse(null) retorna null se nenhum aluno for encontrado.
        // Como já verificamos a existência acima, esse null não deveria ocorrer.
        Student student = studentDAO.findById(id).orElse(null);

        // Retorna o aluno com status HTTP 200 (OK).
        return new ResponseEntity<>(student, HttpStatus.OK);
    }

    // GET /students/findByName/{name}
    // Busca alunos cujo nome contenha o texto informado,
    // ignorando diferenças entre letras maiúsculas e minúsculas.
    @GetMapping(path = "/findByName/{name}")
    public ResponseEntity<?> findStudentByName(@PathVariable String name) {

        // O Spring Data JPA interpreta o nome do método do repositório
        // e executa a consulta correspondente no banco de dados.
        return new ResponseEntity<>(
                studentDAO.findByNameIgnoreCaseContaining(name),
                HttpStatus.OK
        );
    }

    // POST /students
    // Cadastra um novo aluno.
    @PostMapping
    public ResponseEntity<?> save(@RequestBody Student student) {

        // @RequestBody converte o JSON recebido no corpo da requisição
        // para um objeto Java da classe Student.
        // save() salva o aluno no banco de dados.
        // HttpStatus.CREATED representa o status HTTP 201.
        return new ResponseEntity<>(studentDAO.save(student), HttpStatus.CREATED);
    }

    // DELETE /students/{id}
    // Exclui um aluno pelo ID.
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        // Confere se o aluno existe antes de tentar excluí-lo.
        verifyIfStudentExists(id);

        // Exclui o registro correspondente ao ID informado.
        studentDAO.deleteById(id);

        // Retorna HTTP 200 (OK), sem conteúdo no corpo da resposta.
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // PUT /students
    // Atualiza os dados de um aluno existente.
    @PutMapping
    public ResponseEntity<?> update(@RequestBody Student student) {

        // Verifica se o aluno informado possui um ID existente.
        verifyIfStudentExists(student.getId());

        // Salva o objeto.
        // Como o ID já existe, o JPA normalmente atualiza o registro.
        studentDAO.save(student);

        // Retorna o objeto recebido com status HTTP 200 (OK).
        return new ResponseEntity<>(student, HttpStatus.OK);
    }

    // Método auxiliar: verifica se existe um aluno com o ID informado.
    // Se não existir, deve lançar uma exceção personalizada.
    private void verifyIfStudentExists(Long id) {

        // ATENÇÃO: esta condição está incorreta!
        // studentDAO é a dependência do repositório, não o aluno pesquisado.
        // Como ela foi injetada pelo Spring, normalmente não será null.
        if (studentDAO == null)
            throw new ResourceNotFoundException(
                    "Student Not Found for id: " + id
            );
    }
}
