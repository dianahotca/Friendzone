package repository.file;

import domain.Friendship;
import domain.Tuple;
import domain.validators.Validator;
import repository.memory.FriendshipMemoryRepository;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractFriendshipFileRepository extends FriendshipMemoryRepository {

    private String fileName;
    public AbstractFriendshipFileRepository(String fileName,Validator<Friendship> validator) {
        super(validator);
        this.fileName = fileName;
        loadData();
    }

    /**
     * Creates an entity as a string
     * @param entity - an entity
     * @return String - the entity as string created
     * */
    protected abstract String createEntityAsString(Friendship entity);

    /**
     * Creates an entity over a list of strings
     * @param atributes - a list of strings
     * @return Friendship - the entity created
     * */
    protected abstract Friendship extractEntity(List<String> atributes);

    /**
     * Writes an entity to file
     * @param entity - an entity
     * */
    protected void writeToFile(Friendship entity){
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
    protected void writeToFileAll(Iterable<Friendship> users){
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName, false))){
            for(Friendship entity:users) {
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
            List<String> lines = Files.readAllLines(path);
            lines.forEach((line) -> {
                Friendship entity = extractEntity(Arrays.asList(line.split(";")));
                super.save(entity);
            });
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void save(Friendship entity){
        super.save(entity);
        Iterable<Friendship> users = super.getAllEntities();
        writeToFileAll(users);
    }

    @Override
    public void delete(Tuple<String,String> id){
        super.delete(id);
        Iterable<Friendship> users = super.getAllEntities();
        writeToFileAll(users);
    }

}
