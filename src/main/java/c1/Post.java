/*
 * Created by joenggyu0@gmail.com on 6/25/20
 * Github : http://github.com/lenkim
 */

package c1;

import java.io.Serializable;

public class Post implements Serializable {

    int id;
    String name;
    Contents contents;

    public Post(int id, String name, Contents contents) {
        this.id = id;
        this.name = name;
        this.contents = contents;
    }


    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", contents=" + contents +
                '}';
    }
}
