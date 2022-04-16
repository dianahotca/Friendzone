package repository.file;

import domain.Entity;
import domain.User;
import domain.validators.Validator;
import repository.memory.UserMemoryRepository;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractUserFileRepository extends UserMemoryRepository {

    private java.lang.String fileName;

    public AbstractUserFileRepository(java.lang.String fileName, Validator<User> validator) {
        super(validator);
        this.fileName = fileName;
        loadData();
    }

    /**
     * Creates an entity as a string
     * @param entity - an entity
     * @return String - the entity as string created
     * */
    protected abstract java.lang.String createEntityAsString(User entity);

    /**
     * Creates an entity over a list of strings
     * @param attributes - a list of strings
     * @return E - the entity created
     * */
    protected abstract User extractEntity(List<java.lang.String> attributes);

    /**
     * Writes an entity to file
     * @param entity - an entity
     * */
    protected void writeToFile(User entity){
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName, true))){
            bufferedWriter.write(createEntityAsString(entity));
            bufferedWriter.newLine();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes a list of entities to file
     * @param users - a list of entities
     * */
    protected void writeToFileAll(Iterable<User> users){
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName, false))){
            for(User entity:users) {
                bufferedWriter.write(createEntityAsString(entity));
                bufferedWriter.newLine();
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read from file, creates the entities and saves them in memory
     * */
    private void loadData(){
        Path path = Paths.get(fileName);
        try{
            List<java.lang.String> lines = Files.readAllLines(path);
            lines.forEach((line) -> {
                User entity = extractEntity(Arrays.asList(line.split(";")));
                super.save(entity);
            });
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void save(User entity){
        super.save(entity);
        writeToFile(entity);
    }

    @Override
    public void delete(String id){
        super.delete(id);
        Iterable<User> users = super.getAllEntities();
        writeToFileAll(users);
    }
}
