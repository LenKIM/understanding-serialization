# 직렬화



직렬화를 왜하는걸까?

직렬화를 이해하다보니, 자바의 직렬화는 완전 다른 세계의 이야기인듯한데?

DO에서는 어떻게 사용되는거지?

---

## Index

1. 직렬화란 무엇일까요?
2. 자바에서 직렬화를 어떻게 사용할까요?
   (코드 / 조건)
3. 역직렬화 조건은 무엇일까요?
4. 직렬화를 왜 해야하는걸까요?
5. 자바 직렬화를 언제, 어디서 사용될까요?
6. 그러나 직렬화는 사용하지 말아야 합니다.
7. 크로스-플랫폼 구조화된 데이터 표현(avro / protobuf)을 사용합시다.



---



## 01. 직렬화란 무엇인가?

**serialization** (or **serialisation**) is the process of translating [data structures](https://en.wikipedia.org/wiki/Data_structure) or [object](https://en.wikipedia.org/wiki/Object_(computer_science)) state into a format that can be stored (for example, in a [file](https://en.wikipedia.org/wiki/Computer_file) or memory [buffer](https://en.wikipedia.org/wiki/Data_buffer)) or transmitted (for example, across a [network](https://en.wikipedia.org/wiki/Computer_network) connection link) and reconstructed later (possibly in a different computer environment).

(위키백과)

 직렬화 (또는 직렬화)는 데이터 구조 또는 객체 상태를 저장 (예 : 파일 또는 메모리 버퍼) 하거나 전송 (예 : 네트워크 연결 링크를 통해)하고 나중에 재구성(가능한 경우) 할 수있는 형식으로 변환하는 프로세스입니다. 





(MS Docs)

![Serialization 그래픽](https://tva1.sinaimg.cn/large/007S8ZIlgy1ggczgbxozkg307504xglh.gif)



 개체는 데이터를 전달하는 스트림으로 직렬화됩니다. 스트림에는 버전, 문화권 및 어셈블리 이름과 같은 개체 형식 정보가 포함될 수도 있습니다. 해당 스트림의 개체를 데이터베이스, 파일 또는 메모리에 저장할 수 있습니다.



## 02. 자바에서는 직렬화를 왜 사용하게 되었으며, 어떻게 사용하는 걸까요?



 자바에서 직렬화를 어떻게 사용해야 될까요? 이를 이해하기 위해서 직렬화가 언제 출연되었는지 

 그 역사부터 살펴보자.

> 1997년, 자바에서 처음으로 직렬화 도입되었습니다.  당시 도입시, 프로그래머가 어렵지 않게 **분산 객체**를 만들 수 있다는 구호는 매력적이였지만, 보이지 않는 생성자 와, API와 구현 사이의 모호해진 경계, 잠재적인 정확성 문제, 성능, 보안, 유지보수성 등 그 대가가 컸다. 지지자들은 장점이 이런 위험성을 압도한다고 생각했지만, 지금까지 경험한 바로는 그 반대다.  - 아이템 85 자바 직렬화의 대안을 찾으라 (이펙티브 자바 - 직렬화편)



> tip) 여기서 분산 객체란? 
>
> ![image-20200630105221349](https://tva1.sinaimg.cn/large/007S8ZIlgy1gga2uv5f6lj31390u0tgz.jpg)
>
> 또다른 정의,
>
> *분산 컴퓨팅 기술이 객체 지향과 접목되어 하나의 프로세서나 컴퓨터에서 실행되는 객체가 다른 프로세서나 컴퓨터에서 객체와 통신이 가능 하도록 하는 기술이 분산 객체 기술.*



네, 직렬화가 필요하게 된 원인은 `분산 객체`에 있습니다. 분산 객체를 실행시키고 싶었던 시도가 오늘날 직렬화를 만들게 된 계기가 되었습니다.



아래는 직렬화/역직렬화를 사용하는 간단한 예제입니다.

```java
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

```



실제로 객체는 아래의 구현을 띄웁니다.



```java
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
```

```java
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
```



와 같이 `implements Serializable` 해주는 것만으로도 손쉽게 직렬화/역직렬화을 할 수 있습니다.



## 03. 역직렬화 조건



 역직렬화의 조건이란 무엇을 말하는 걸까요? 특정 객체를 직렬화 했다면- 역직렬화를 하기 위해서는 아래와 같은 행위를 해줘야 함을 의미합니다.



1. 직렬화 대상이 된 객체의 클래스가 클래스 패스에 존재해야 하며 `import` 되어 있어야 합니다.

   - **중요한 점은 직렬화와 역직렬화를 진행하는 시스템이 서로 다를 수 있다는 것을 반드시 고려해야 합니다.**

2. 자바 직렬화 대상 객체는 동일한 `serialVersionUID` 를 가지고 있어야 합니다.  
   

```private static final long serialVersionUID = 1L; ```



왜 `1L` 라는 선뜻 의미가 없는 값을 가져야 되는걸까요? 실제로 DO 소스에도 1L 이라는 값을 종종 찾아볼 수 있었습니다. 



아래에서 좀 더 자세히 설명하도록 하겠습니다.



## 04. 1L의 의미는 무엇일까요?



(왜 1L을 붙였는지에 대한 라이브 코딩)



the serialization runtime will calculate a default serialVersionUID value for that class based on various aspects of the class, as described in the Java(TM) Object Serialization Specification



`UID`, `serialVerionUID` 라는 이름의 필드로, 이 번호를 명시하지 않으면 시스템이 런타임에 암호해시 함수(SHA-1)를 적용해 자동으로 클래스 안에 생성해 넣습니다. 이 후 클래스가 변경되면 직렬 버전 UID 값도 변경되면서, 자동 생성되는 값에 의존하면 쉽게 호환성이 깨져버려 런타임에 InvalidClassException 이 발생합니다.





## 05. 자바 직렬화를 언제, 어디서 사용되나?



 분산 객체를 사용하기 위해서 사용되었다고 했다. 이는 다시말해, 클라이언트가 JVM이 없는 상태에서도 자바의 프로그램을 실행시키기 위해서 사용하길 원한다는 말과 동일합니다.



 다시 말해, **JVM의 메모리에서만 상주되어야하는 객체 데이터(Bytes)** 를 그대로 영속화(Persistence)가 필요할 때 사용됨을 의미합니다. 



시스템이 종료되더라도 없어지지 않는 장점을 가지며, 영속화된 데이터이기 때문에 네트워크로 전송도 가능합니다.



- 서블릿 세션(Servelt Session)  
  세션을 서블릿 메모리 위에서 운용한다면 직렬화를 필요로 하지 않지만, 파일로 저장하거나 세션 클러스터링, DB를 저장하는 옵션 등을 선택하게 되면 세션 자체가 직렬화가 되어 저장되어 전달됩니다.

- 캐시(Cache)  
  Ehcache, Redis, Memcached 라이브러리 시스템을 많이 사용됩니다. 

  ![image-20200702235939858](https://tva1.sinaimg.cn/large/007S8ZIlgy1ggd0uo7f0bj31x20u0na4.jpg)

- 자바 RMI(Remote Method Invocation)  
  원격 시스템 간의 메시지 교환을 위해서 사용하는 자바에서 지원하는 기술입니다. 원격의 시스템의 메서드를 호출 에 전달하는 메시지(객체)를 자동으로 직렬화 시켜 사용됩니다.



https://docs.oracle.com/javase/8/docs/platform/serialization/spec/serial-arch.html

https://j.mearie.org/post/122845365013/serialization



## 06. 그러나 직렬화는 사용하지 말아야 합니다.



> 자바의 직렬화는 명백하게 현존하는 위험이다. 이 기술은 지금도 애플리케이션에서 직접 혹은, 자바 하부 시스템(RMI(Remote Method Invocation), JMX(Java Management Extension), JMS(Java Messaging System) 같은)을 통해 간접적으로 쓰이고 있기 때문이다. 신뢰할 수 없는 스트림을 역직렬화하면 원격 코드 실행(remote code execution), 서비스 거부(Dos) 등의 공격으로 이어질 수 있다. 잘못한 게 아무것도 없는 애플리케이션이라도 이런 공격에 취약해질 수 있다. - 이펙티브 자바 3판



한가지 예시를 보자.



```java
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
```



루트 HashSet에 담긴 두 원소는 각각 다른 HashSet 2개씩을 원소로 갖는 HashSet. 그리고 반복문에 의해 이 구조가 싶이 100단계까지 만들어진다.



 나중에 root에서 시작된 HashSet을 역직렬화하려면 hashCode를 계산합니다. 왜?

![image-20200703002758202](https://tva1.sinaimg.cn/large/007S8ZIlgy1ggd1o3wjpbj30u010eam3.jpg)



위 내용은 역직렬화시 Hashcode가 변경될 수 있음을 이야기하는 것으로, 이는 동등함을 증명하기 위해서 사용됩니다.



 

그렇다면 위 같은 문제를 어떻게 대처해야 할까요? 애초에 신뢰할 수 없는 바이트 스트림을 역직렬화하는 일 자체가 스스를 공격에 노출하는 행위입니다. 



### 그러므로, 결론은 **직렬화 위험을 회피하는 가장 좋은 방법은 아무것도 역직렬화하지 않는 것이 좋습니다.**

대신에, 객체와 바이트 시퀀스를 변환해주는 다른 매커니즘 사용하면 됩니다. 이런 메커니즘을 직렬화 시스템이라고 불리기도 하고, 이펙티브 자바에서는 자바 직렬화와 구분하기 위해 크로스-플랫폼 구조화된 데이터 표현라고 표현합니다.





## 07. 크로스-플랫폼 구조화된 데이터 표현(json/avro/protobuf)을 사용합시다.



그래서 선택할 수 있는 대안이 `avro`,`protobuf` 을 사용하라고 권장합니다.



**크로스-플랫폼 구조화된 데이터 표현?**



이것들은 객체를 직렬화/역직렬화하지 않습니다. 대신 속성-값 쌍의 집합으로 구성된 간단하고 구조화된 데이터 객체를 사용합니다.



여기서는 JSON과 Protobuf에 대한 설명만 이어하겠습니다.



Json은 텍스트 기반으으로 사람이 읽을 수 있고, 브라우저와 서버의 통신용으로 설계되었고,

Protobuf는 구글이 서버 사이에 데이터를 교환하고 저장하기 위해 설계 되었습니다.



Protobuf의 장점을 간략히 살펴보겠습니다.



흔히 protobuf는 XML과 비교됩니다.

우리는 XML이라는 문자열을 파싱 또는 생성하여 다른 클라이언트에게 보내줍니다. 이때 클라이언트와 클라이언트 사이에 메세지를 보냈지만 불안정하다고 말할 수 있습니다. 받는 쪽에서 잘못된 파싱을 이어가거나, 보내는 쪽에서도 XML을 잘못 보낼 수 있기 때문입니다.



 이런 문제를 Protobuf는 각각의 언어에 Protobuf 컴파일러를 실행시켜, 위와같은 불안정성을 Protobuf에 위임함으로써, 보내는쪽과 받는쪽 모두가 해피해피한 데이터를 주고받을 수 있게 됩니다.



좀더 자세한 내용은 해당 링크 참조해주세요.

https://www.bizety.com/2018/11/12/protocol-buffers-vs-json/



## 부록) 자바 직렬화의 위험성 이용해보기..

```java
숨은 생성자의 의미.

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
```



객체를 생성하는 건 `new` 뿐이라고 생각이 되는가? 아니다. **자바 직렬화를 활용하면 객체 생성 할 수 있다!!** 그 외 리플렉션도 존재한다.