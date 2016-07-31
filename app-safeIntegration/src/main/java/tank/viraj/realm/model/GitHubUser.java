package tank.viraj.realm.model;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.Getter;

/**
 * Created by Viraj Tank, 18-06-2016.
 */
@Getter
public class GitHubUser extends RealmObject implements Serializable {
    @PrimaryKey
    private String login;
    private int id;
    private String avatar_url;

    public GitHubUser() {
    }

    public GitHubUser(int id, String login, String avatar_url) {
        this.id = id;
        this.login = login;
        this.avatar_url = avatar_url;
    }
}