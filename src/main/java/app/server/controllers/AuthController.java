package app.server.controllers;

import app.product.UserEntity;
import app.server.services.MessageResp;
import app.server.services.RequestHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final RequestHandler handler;

    @Autowired
    public AuthController(RequestHandler h) {
        this.handler = h;
    }

    @PostMapping("/auth")
    public ResponseEntity<String> auth(@RequestBody UserEntity user) {
        String r = handler.auth(user).getMessage();
        return ResponseEntity.ok(r);
    }

    @PostMapping("/reg")
    public ResponseEntity<String> reg(@RequestBody UserEntity user) {
        String resp = handler.reg(user);
        return ResponseEntity.ok(resp);
    }
}
