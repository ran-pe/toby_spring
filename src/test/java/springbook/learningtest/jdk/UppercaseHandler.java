package springbook.learningtest.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


public class UppercaseHandler implements InvocationHandler {
    /**
     * 6-25 확장된 UppercaseHandler
     */
    Object target;

    public UppercaseHandler(Object target) {   // 어떤 종류의 인터페이스를 구현한 타깃에도 적용 가능하도록 Object 타입으로 수정
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object ret = method.invoke(target, args);
        if (ret instanceof String && method.getName().startsWith("say")) {    // 호출한 메소드의 리턴타입이 String 이고, 메소드 이름이 일치하는 경우만 대문자 변경기능을 적용하도록 수정
            return ((String) ret).toUpperCase();
        } else {
            return ret;
        }
    }


    /**
     * 6-23 InvocationHandler 구현 클래스
     */
    /*
    Hello target;

    public UppercaseHandler(Hello target) { // 다이내믹 프록시로부터 전달받은 요청을 다시 타깃 오브젝트에 위임해야 하기 때문에 타깃 오브젝트를 주입받아둔다.
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String ret = (String)method.invoke(target, args);   // 타깃으로 위임. 인터페이스의 메소드 호출에 모두 적용된다.
        return ret.toUpperCase();   // 부가기능 제공
    }
    */
}
