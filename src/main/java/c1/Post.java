/*
 * Created by joenggyu0@gmail.com on 6/25/20
 * Github : http://github.com/lenkim
 */

package c1;

import java.io.Serializable;

public class Post implements Serializable {

    Contents contents;

    public Post(String name, Contents contents) {
        this.contents = contents;
    }

    @Override
    public String toString() {
        return "Post{" +
                ", contents=" + contents +
                '}';
    }
}
