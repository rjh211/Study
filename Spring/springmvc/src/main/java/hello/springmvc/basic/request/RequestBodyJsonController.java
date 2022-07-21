package hello.springmvc.basic.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.springmvc.basic.HelloData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Controller
public class RequestBodyJsonController {

    private ObjectMapper objectMapper = new ObjectMapper(); // JSON사용시 사용하는 객체

    @PostMapping("/request-body-json-v1")
    public void requestBodyJsonV1(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ServletInputStream inputStream = request.getInputStream();
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

        log.info("messageBody = {}", messageBody);
        HelloData helloData = objectMapper.readValue(messageBody, HelloData.class);
        log.info("username = {}, age = {}", helloData.getUsername(), helloData.getAge());

        response.getWriter().write("ok");
    }

    @ResponseBody
    @PostMapping("/request-body-json-v2")
    public String requestBodyJsonV2(@RequestBody String messageBody) throws IOException {
        log.info("messageBody = {}", messageBody);
        HelloData helloData = objectMapper.readValue(messageBody, HelloData.class);
        log.info("username = {}, age = {}", helloData.getUsername(), helloData.getAge());

        return "ok";
    }

    @PostMapping("/request-body-json-v3")
    public String requestBodyJsonV3(@RequestBody HelloData helloData) throws IOException { //Http Converter가 Http Content type을 확인하고 JsonData를 HelloData로 컨버팅해줌
        //@RequestBody 생략 불가 - 생략시엔 default 로 @ModelAttribute가 붙기 때문
        log.info("username = {}, age = {}", helloData.getUsername(), helloData.getAge());

        return "ok";
    }

    @PostMapping("/request-body-json-v4")
    public String requestBodyJsonV4(HttpEntity<HelloData> httpEntity) throws IOException { //Http Converter가 Http Content type을 확인하고 JsonData를 HelloData로 컨버팅해줌
        //@RequestBody 생략 불가 - 생략시엔 default 로 @ModelAttribute가 붙기 때문
        HelloData data = httpEntity.getBody();
        log.info("username = {}, age = {}", data.getUsername(), data.getAge());

        return "ok";
    }

    /**
     * 요청 : JSON -> 컨버터 -> 객체
     * 응답 : 객체 -> 컨버터 -> JSON
     * */
    @PostMapping("/request-body-json-v5")
    public HelloData requestBodyJsonV5(HttpEntity<HelloData> httpEntity) throws IOException { //Http Converter가 Http Content type을 확인하고 JsonData를 HelloData로 컨버팅해줌
        //@RequestBody 생략 불가 - 생략시엔 default 로 @ModelAttribute가 붙기 때문
        HelloData data = httpEntity.getBody();
        log.info("username = {}, age = {}", data.getUsername(), data.getAge());

        return data; //보낸 요청이 그대로 JSON으로 변환되어 전달된다.
    }
}
