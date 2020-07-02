/*
 * Created by joenggyu0@gmail.com on 7/3/20
 * Github : http://github.com/lenkim
 */

package c1.effectivejava;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

public class MainC {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Period p = new Period(new Date(2020, Calendar.JUNE, 1), new Date(2020, Calendar.JUNE, 3));
        System.out.println("처음:" + p.hashCode());
        System.out.println("처음:" + p.start().getYear());
        byte[] serialize = Util.serialize(p);
        String s = Base64.getEncoder().encodeToString(serialize); //rO0ABXNyACpjMS5lZmZlY3RpdmVqYXZhLlBlcmlvZCRTZXJpYWxpemF0aW9uUHJveHkDP68cyaRBZQIAAkwAA2VuZHQAEExqYXZhL3V0aWwvRGF0ZTtMAAVzdGFydHEAfgABeHBzcgAOamF2YS51dGlsLkRhdGVoaoEBS1l0GQMAAHhwdwgAADf6jWu9gHhzcQB

        byte[] aa = Base64.getDecoder().decode(s);
        try (ByteArrayInputStream bais = new ByteArrayInputStream(aa)) {
            try (ObjectInputStream ois = new ObjectInputStream(bais)) {
                Object objectPost = ois.readObject();
                Period period = (Period) objectPost;
                System.out.println("역직렬화 후:" + period.hashCode());
                System.out.println("역직렬화 후:" + period.start().getYear());
            }
        }
    }
}
