package repository.file;

import domain.Friendship;
import domain.Tuple;
import domain.validators.Validator;

import java.util.List;

public class FriendshipFileRepository extends AbstractFriendshipFileRepository {

        public FriendshipFileRepository(String fileName, Validator<Friendship> validator) {
            super(fileName, validator);
        }

        @Override
        protected String createEntityAsString(Friendship entity) {
            return entity.getId() +";"+ entity.getUserEmails().getLeft() + ";" + entity.getUserEmails().getRight() + ";" + entity.getDate();
        }

        @Override
        protected Friendship extractEntity(List<String> atributes) {
            Friendship friendship = new Friendship(new Tuple<>(atributes.get(0), atributes.get(1)), atributes.get(2));
            return friendship;
        }


}


