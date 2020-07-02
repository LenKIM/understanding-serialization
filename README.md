# 직렬화



Index

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



직렬화 (또는 직렬화)는 데이터 구조 또는 객체 상태를 저장 (예 : 파일 또는 메모리 버퍼)하거나 전송 (예 : 네트워크 연결 링크를 통해)하고 나중에 재구성 (가능한 경우) 할 수있는 형식으로 변환하는 프로세스입니다. 다른 컴퓨터 환경에서).



## 02. 자바에서는 직렬화를 어떻게 사용하고, 왜 사용해야하는걸까요?



 자바에서 직렬화를 어떻게 사용해야 될까? 이를 이해하기 위해서 직렬화가 언제 출연되었는지 부터 살펴보자.

1997년, 자바에서 처음으로 직렬화 도입되었습니다. 

> 당시 도입시, 프로그래머가 어렵지 않게 **분산 객체**를 만들 수 있다는 구호는 매력적이였지만, 보이지 않는 생성자와, API와 구현 사이의 모호해진 경계, 잠재적인 정확성 문제, 성능, 보안, 유지보수성 등 그 대가가 컸습니다. 
>
> (이펙티브 자바 - 직렬화편)



> tip) 여기서 분산 객체란?
>
> ![image-20200630105221349](https://tva1.sinaimg.cn/large/007S8ZIlgy1gga2uv5f6lj31390u0tgz.jpg)



네, 직렬화가 필요하게 된 원인은 분산 객체에 있습니다. 분산 객체를 실행시키고 싶었던 시도가 오늘날 직렬화를 만들게 된 계기가 되었습니다.



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



실제로 객체에 



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



왜 `1L` 라는 선뜻 의미가 없는 값을 가져야 되는걸까? 아래에서 좀 더 자세히 설명하도록 하겠다.



## 04. 1L의 의미는 무엇일까?



(왜 1L을 붙였는지에 대한 라이브 코딩)



the serialization runtime will calculate a default serialVersionUID value
for that class based on various aspects of the class, as described in the
Java(TM) Object Serialization Specification



`UID`, `serialVerionUID` 라는 이름의 필드로, 이 번호를 명시하지 않으면 시스템이 런타임에 암호해시 함수(SHA-1)를 적용해 자동으로 클래스 안에 생성해 넣는다. 이 후 클래스가 변경되면 직렬 버전 UID 값도 변경되면서, 자동 생성되는 값에 의존하면 쉽게 호환성이 깨져버려 런타임에 InvalidClassException 이 발생



```java
Exception in thread "main" java.io.NotSerializableException: c1.Post
	at java.io.ObjectOutputStream.writeObject0(ObjectOutputStream.java:1184)
	at java.io.ObjectOutputStream.writeObject(ObjectOutputStream.java:348)
	at c1.Main.main(Main.java:23)
```



## 05. 자바 직렬화를 언제, 어디서 사용되나?



 분산 객체를 사용하기 위해서 사용되었다고 했다. 이는 다시말해, 클라이언트가 JVM이 없는 상태에 사용하길 원한다는 말과 동일하다. 다시 말해, JVM의 메모리에서만 상주되어이는 객체 데이터를 그대로 영속화(Persistence)가 필요할 때 사용됨을 의미한다. 시스템이 종료되더라도 없어지지 않는 장점을 가지며 영속화된 데이터이기 때문에 네트워크로 전송도 가능하다.



- 서블릿 세션(Servelt Session)  
  세션을 서블릿 메모리 위에서 운용한다면 직렬화를 필요로 하지 않지만, 파일로 저장하거나 세션 클러스터링, DB를 저장하는 옵션 등을 선택하게 되면 세션 자체가 직렬화가 되어 저장되어 전달됩니다.

- 캐시(Cache)  
  Ehcache, Redis, Memcached 라이브러리 시스템을 많이 사용됩니다. 사실 사용하지 않아도 동작된다.

- 자바 RMI(Remote Method Invocation)  
  원격 시스템 간의 메시지 교환을 위해서 사용하는 자바에서 지원하는 기술.



https://docs.oracle.com/javase/8/docs/platform/serialization/spec/serial-arch.html

https://j.mearie.org/post/122845365013/serialization



## 06. 그러나 직렬화는 사용하지 말아야 합니다.



1. UID
2. 버그와 보안 구멍이 생길 위험이 높아진다는 점
3. 해당 클래스의 신버전을 테스트할 것이 늘어난다는 점
4. Serializable 구현 여부는 가볍게 결정할 사안이 아니다. 



그래서 결론은 **직렬화 위험을 회피하는 가장 좋은 방법은 아무것도 역직렬화하지 않는 것이다.** 

단, 객체와 바이트 시퀀스를 변환해주는 다른 매커니즘 사용하면 된다.



결론은

자바 직렬화는 회피해야 합니다.

이펙티브 자바 책에서는 자바 직렬화를 대체할 수 있다면, 시간과 비용을 들여 변경하라고 요구하고 있습니다.



## 07. 크로스-플랫폼 구조화된 데이터 표현(avro/protobuf)을 사용합시다.



그래서 선택할 수 있는 대안이 `avro`,`protobuf` 을 사용하라고 권장합니다.



**크로스-플랫폼 구조화된 데이터 표현?**



Key, Value 로 이루어져 있는 것.



즉, 자바 직렬화보다 안전하다고 할 수 있는데, 어째서? 안전하다고 할 수 있는가?





RFC4648 / RFC4648_URLSAFE / RFC2045



Base64가 무엇인가? 



만약 시리얼 라이즈를 맞추지 않았을 경우.

```
Exception in thread "main" java.io.InvalidClassException: c1.Post; local class incompatible: stream classdesc serialVersionUID = 6200715765606536414, local class serialVersionUID = -6165664529577570928
```