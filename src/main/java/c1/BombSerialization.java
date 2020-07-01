/*
 * Created by joenggyu0@gmail.com on 6/29/20
 * Github : http://github.com/lenkim
 */

package c1;

import java.util.HashSet;
import java.util.Set;

import static c1.Util.deserialize;
import static c1.Util.serialize;

public class BombSerialization {
    static byte[] bomb() {
        Set<Object> root = new HashSet<>();
        Set<Object> s1 = root;
        Set<Object> s2 = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            Set<Object> t1 = new HashSet<>();
            Set<Object> t2 = new HashSet<>();
            t1.add("foo"); // t1을 t2와 다르게 만든다.
            s1.add(t1);
            s1.add(t2);
            s2.add(t1);
            s2.add(t2);
            s1 = t1;
            s2 = t2;
        }
        return serialize(root); // 이 메서드는 effectivejava.chapter12.Util 클래스에 정의되어 있다.
    }

    public static void main(String[] args) {
        System.out.println(bomb().length);
        deserialize(bomb());
    }
}
