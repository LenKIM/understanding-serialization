/*
 * Created by joenggyu0@gmail.com on 6/25/20
 * Github : http://github.com/lenkim
 */

package c1;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Base64;

public class Main {

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        Post post = new Post("Len", new Contents(LocalDateTime.now(), "내용"));

        byte[] serializationPost;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(post);
                // serializationPost => 직렬화된 Post
                serializationPost = baos.toByteArray();

            }
        }

        String x = Base64.getEncoder().encodeToString(serializationPost);
        System.out.println(x);
// rO0ABXNyAAdjMS5Qb3N0Vg1eItIkVN4CAAJMAAhjb250ZW50c3QADUxjMS9Db250ZW50cztMAARuYW1ldAASTGphdmEvbGFuZy9TdHJpbmc7eHBzcgALYzEuQ29udGVudHNvXOGIyc9XLgIAAkwACGNvbnRlbnRzcQB
//        https://docs.oracle.com/javase/6/docs/platform/serialization/spec/class.html#4100
        //직렬화된 데이터를 역직렬화
        String base64Post = x;

        byte[] serializedPost = Base64.getDecoder().decode(base64Post);
        try (ByteArrayInputStream bais = new ByteArrayInputStream(serializedPost)) {
            try (ObjectInputStream ois = new ObjectInputStream(bais)) {
                Object objectPost = ois.readObject();
                Post post1 = (Post) objectPost;
                System.out.println(post1);
            }
        }

    }
}
