package com.imooc.jvm.async;

import com.imooc.pojo.bo.AddressBO;
import com.imooc.utils.IMOOCJSONResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.ExecutionException;

/**
 * 异步化测试: 测试@Async
 */
@RestController
public class AsyncTestController {

    public static final Logger LOGGER = LoggerFactory.getLogger(AsyncTestController.class);

    @Autowired
    private ASyncDemoAsyncAnnotation aSyncDemoAsyncAnnotation;

    @GetMapping("/async-test")
    public String asyncTest() throws InterruptedException, ExecutionException {
        aSyncDemoAsyncAnnotation.biz();
        return "success";
    }

    @Autowired
    private AsyncRestTemplate asyncRestTemplate;

    @GetMapping("/test-async-rest-template")
    public String testAsyncRestTemplate() throws ExecutionException, InterruptedException {
        ListenableFuture<ResponseEntity<IMOOCJSONResult>> future = this.asyncRestTemplate.getForEntity(
                "http://localhost:8088/index/subCat/{rootCatId}",
                IMOOCJSONResult.class,
                1
        );

        // 同步阻塞获取
//        ResponseEntity<IMOOCJSONResult> imoocjsonResultResponseEntity = future.get();
//        IMOOCJSONResult body = imoocjsonResultResponseEntity.getBody();

        // 异步获取: 添加回调函数
        future.addCallback(new ListenableFutureCallback<ResponseEntity<IMOOCJSONResult>>() {
            @Override
            public void onFailure(Throwable ex) {
                LOGGER.error("请求失败", ex);
            }

            @Override
            public void onSuccess(ResponseEntity<IMOOCJSONResult> result) {
                LOGGER.info("调用成功, body={}", result.getBody().getData());
            }
        });

        return "suceess";
    }

    @GetMapping("/test-async-rest-template-post")
    public IMOOCJSONResult testAsyncRestTemplatePost() throws ExecutionException, InterruptedException {
        AddressBO addressBO = new AddressBO();
        addressBO.setAddressId("111");
        addressBO.setUserId("111");
        addressBO.setReceiver("111");
        addressBO.setMobile("15151816012");
        addressBO.setProvince("aaa");
        addressBO.setDistrict("aaa");
        addressBO.setDetail("aaa");
        addressBO.setCity("aaa");

        ListenableFuture<ResponseEntity<IMOOCJSONResult>> future = this.asyncRestTemplate.postForEntity(
                "http://localhost:8088/address/update",
                new HttpEntity<>(addressBO),
                IMOOCJSONResult.class
        );

        // 同步阻塞获取
        ResponseEntity<IMOOCJSONResult> imoocjsonResultResponseEntity = future.get();
        IMOOCJSONResult body = imoocjsonResultResponseEntity.getBody();

        // 异步获取: 添加回调函数
//        future.addCallback(new ListenableFutureCallback<ResponseEntity<IMOOCJSONResult>>() {
//            @Override
//            public void onFailure(Throwable ex) {
//                LOGGER.error("请求失败", ex);
//            }
//
//            @Override
//            public void onSuccess(ResponseEntity<IMOOCJSONResult> result) {
//                LOGGER.info("调用成功, body={}", result.getBody().getData());
//            }
//        });

        return body;
    }

    @Autowired
    private WebClient webClient;

    @GetMapping("test-web-client")
    public IMOOCJSONResult testWebClient() {
        Mono<IMOOCJSONResult> imoocjsonResultMono = this.webClient.get()
                .uri("http://localhost:8088/index/subCat/{rootCatId}", 1)
                .retrieve()
                .bodyToMono(IMOOCJSONResult.class);

        return imoocjsonResultMono.block();
    }

    @GetMapping("/test-web-client-post")
    public IMOOCJSONResult testWebClientPost() throws ExecutionException, InterruptedException {
        AddressBO addressBO = new AddressBO();
        addressBO.setAddressId("111");
        addressBO.setUserId("111");
        addressBO.setReceiver("111");
        addressBO.setMobile("15151816012");
        addressBO.setProvince("aaa");
        addressBO.setDistrict("aaa");
        addressBO.setDetail("aaa");
        addressBO.setCity("aaa");

        ListenableFuture<ResponseEntity<IMOOCJSONResult>> future = this.asyncRestTemplate.postForEntity(
                "http://localhost:8088/address/update",
                new HttpEntity<>(addressBO),
                IMOOCJSONResult.class
        );

        Mono<IMOOCJSONResult> imoocjsonResultMono = this.webClient.post()
                .uri("http://localhost:8088/address/update")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(addressBO))
                // syncBody(addressBO) = body(BodyInserters.fromObject(addressBO))
//                .syncBody(addressBO)
                .retrieve()
                .bodyToMono(IMOOCJSONResult.class);
        return imoocjsonResultMono.block();
    }
}
