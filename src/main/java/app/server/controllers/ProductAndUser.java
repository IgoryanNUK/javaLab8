package app.server.controllers;

import app.product.Product;
import app.product.UserEntity;
import lombok.Data;

@Data
public class ProductAndUser {
    private Product product;
    private UserEntity user;
}
