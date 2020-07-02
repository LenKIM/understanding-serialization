/*
 * Created by joenggyu0@gmail.com on 7/3/20
 * Github : http://github.com/lenkim
 */

package c1.effectivejava;

import java.io.*;
import java.util.Date;

public class MutablePeriod {

    public Period period;
    public Date start;
    public Date end;

    public MutablePeriod() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);

            out.writeObject(new Period(new Date(), new Date())); // 변경되지 않을 것이라 믿을 수 있다.

            /**
             * 악의적 이전객체 참조, 즉 내부 Date 필드로의 참조를 추가.
             */
            byte[] ref = {0x71, 0, 0x7e, 0, 5};
            bos.write(ref);
            ref[4] = 4;
            bos.write(ref);

            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));

            period = (Period) in.readObject();
            start = (Date) in.readObject();
            end = (Date) in.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
