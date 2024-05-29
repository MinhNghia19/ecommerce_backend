package com.example.ecommerce_backend.services.shop;

import com.example.ecommerce_backend.dtos.ShopDTO;
import com.example.ecommerce_backend.models.Shop;

public interface IShopService {


    Shop createShop(ShopDTO shopDTO) throws Exception ;
}
