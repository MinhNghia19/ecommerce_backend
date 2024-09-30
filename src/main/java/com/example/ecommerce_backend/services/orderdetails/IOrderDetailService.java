package com.example.ecommerce_backend.services.orderdetails;



import com.example.ecommerce_backend.exception.DataNotFoundException;
import com.example.ecommerce_backend.models.OrderDetail;

import java.util.List;

public interface IOrderDetailService {

    List<OrderDetail> getOrderDetailByOrderId(Long orderId) throws DataNotFoundException;


}
