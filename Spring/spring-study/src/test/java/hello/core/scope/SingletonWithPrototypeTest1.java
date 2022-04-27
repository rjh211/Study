package hello.core.scope;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class SingletonWithPrototypeTest1 {

    @Test
    void prototypeFind(){
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(Prototype.class);

        Prototype prototype1 = ac.getBean(Prototype.class);
        prototype1.addCount();
        Assertions.assertThat(prototype1.getCount()).isEqualTo(1);

        Prototype prototype2 = ac.getBean(Prototype.class);
        prototype2.addCount();
        Assertions.assertThat(prototype2.getCount()).isEqualTo(1);

    }

    @Scope("prototype")
    static class Prototype{
        private  int count = 0;

        public void addCount(){
            count++;
        }
        public int getCount(){
            return count;
        }
        @PostConstruct
        public void init(){
            System.out.println("PrototypeBean.init " + this);
        }

        @PreDestroy
        public void destroy(){
            System.out.println("Prototype.destroy");
        }
    }

    @Test
    void singletonClientUsePrototype(){
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(ClientBean.class, Prototype.class);
        ClientBean clientBean1 = ac.getBean(ClientBean.class, Prototype.class);
        int count1 = clientBean1.logic();
        Assertions.assertThat(count1).isEqualTo(1);

        ClientBean clientBean2 = ac.getBean(ClientBean.class, Prototype.class);
        int count2 = clientBean2.logic();
        Assertions.assertThat(count2).isEqualTo(2);
    }

    @Scope("singleton")
    static class ClientBean{
        //싱글톤객체에서 프로토타입 빈객체 생성
        private final Prototype prototype;//생성시점에 주입이 함께 완료되어있음

        @Autowired
        public ClientBean(Prototype prototype){
            this.prototype = prototype;
        }
        public int logic(){
            prototype.addCount();
            return prototype.getCount();
        }

        //ApplicationContext자체를 주입받아서 this.logic()이 호출될때마다 새로운 프로토타입을 호출해오는 방법
/*        @Autowired
        ApplicationContext applicationContext;

        public int logic(){
            Prototype prototype = applicationContext.getBean(Prototype.class);
            prototype.addCount();
            return prototype.getCount();
        }*/
    }


    //두번째 클라이언트가 생성되면 의존받는 Prototype객체는 Client1과 다른 객체를 주입받음
    @Scope("singleton")
    static class ClientBean2{
        //싱글톤객체에서 프로토타입 빈객체 생성
        private final Prototype prototype;//생성시점에 주입이 함께 완료되어있음

        @Autowired
        public ClientBean2(Prototype prototype){
            this.prototype = prototype;
        }
        public int logic(){
            prototype.addCount();
            return prototype.getCount();
        }
    }
}
