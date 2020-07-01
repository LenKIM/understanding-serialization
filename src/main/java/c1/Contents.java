/*
 * Created by joenggyu0@gmail.com on 6/25/20
 * Github : http://github.com/lenkim
 */

package c1;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Contents implements Serializable {


    LocalDateTime dateTime;
    String contents;

    public Contents(LocalDateTime dateTime, String contents) {
        this.dateTime = dateTime;
        this.contents = contents;
    }
}
