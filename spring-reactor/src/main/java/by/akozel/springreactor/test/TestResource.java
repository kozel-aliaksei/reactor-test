package by.akozel.springreactor.test;

import com.mongodb.client.model.Filters;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/reactor")
public class TestResource {

    private HttpClient httpClient = HttpClient.create();
    private final String url = "";

    private final MongoCollection<Test> collection;

    public TestResource(MongoDatabase mongoDatabase) {
        this.collection = mongoDatabase.getCollection("test-data", Test.class);
    }

    @GetMapping("/helloWorld")
    public Mono<String> helloWorld() {
        return Mono.just("Hello World Reactor");
    }

    @GetMapping("/mongoSingle")
    public Mono<Test> single() {
        return Mono.from(collection.find(Filters.eq("_id", "1")).first());
    }

    @GetMapping("/mongoMultiple")
    public Mono<List<Test>> mongoMultiple() {
        return Flux.fromIterable(
                        IntStream.range(1, 16)
                                .mapToObj(String::valueOf)
                                .collect(Collectors.toList()))
                .flatMap(id -> Mono.from(collection.find(Filters.eq("_id", id)).first()))
                .collectList();
    }

    @GetMapping("apiSingle")
    public Mono<String> apiSingle() {
        return externalApiCall();
    }

    @GetMapping("apiMultiple")
    public Mono<String> apiMultiple() {
        return Mono.zip(externalApiCall(), anotherApiCall())
                .map(tuple -> tuple.getT1() + tuple.getT2());
    }

    @GetMapping("realApi")
    public Mono<String> realApi() {
        return httpClient.baseUrl(url)
                .get()
                .responseContent()
                .aggregate()
                .asString();
    }

    private Mono<String> externalApiCall() {
        return Mono.just("api response")
                .delayElement(Duration.ofMillis(200));
    }

    private Mono<String> anotherApiCall() {
        return Mono.just(" & one more")
                .delayElement(Duration.ofMillis(300));
    }

}
