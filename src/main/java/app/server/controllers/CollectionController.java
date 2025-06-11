package app.server.controllers;

import app.product.Product;
import app.product.UserEntity;
import app.server.services.MessageResp;
import app.server.services.RequestHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Predicate;

@RestController
@RequestMapping("/collection")
public class CollectionController {
    private final RequestHandler handler;

    @Autowired
    public CollectionController(RequestHandler handler) {
        this.handler = handler;
    }

    @GetMapping("/info")
    public MessageResp getInfo() {
        return handler.getInfo();
    }

    @GetMapping("/show")
    public List<Product> show() {
        return handler.getIf(e -> true);
    }

    @GetMapping("/getIf")
    public List<Product> getIf(@RequestBody Predicate<Product> pred) {
        return handler.getIf(pred);
    }

    @PostMapping("/add")
    public MessageResp add(@RequestBody ProductAndUser req) {
        Product product = req.getProduct();
        UserEntity user = req.getUser();

        return handler.add(product, user);
    }

    @PostMapping("/remove/{id}")
    public MessageResp remove(@PathVariable int id, @RequestBody UserEntity user) {

        return handler.removeIf(e -> e.getId() == id, user);
    }
}
