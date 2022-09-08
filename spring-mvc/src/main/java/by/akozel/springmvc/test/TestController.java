package by.akozel.springmvc.test;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/mvc")
public class TestController {


    private final RestTemplate restTemplate = new RestTemplate();
    private final String url = "";
    private final MongoCollection<Test> collection;

    public TestController(MongoDatabase mongoDatabase) {
        this.collection = mongoDatabase.getCollection("test-data", Test.class);
    }

    @GetMapping("/helloWorld")
    public String helloWorld() {
        return "Hello World MVC";
    }

    @GetMapping("/mongoSingle")
    public Test mongoSingle() {
        return collection.find(Filters.eq("_id", "1")).first();
    }

    @GetMapping("/mongoMultiple")
    public List<Test> multiple() {
        return IntStream.range(1, 16)
                .mapToObj(String::valueOf)
                .map(id -> collection.find(Filters.eq("_id", id)).first())
                .collect(Collectors.toList());
    }

    @GetMapping("apiSingle")
    public String apiSingle() {
        return externalApiCall();
    }

    @GetMapping("apiMultiple")
    public String apiMultiple() {
        return externalApiCall() + anotherApiCall();
    }

    @GetMapping("realApi")
    public String realApi() {
        return restTemplate.getForObject(url, String.class);
    }

    private String externalApiCall() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return "api response";
    }

    private String anotherApiCall() {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return " & one more";
    }

}
