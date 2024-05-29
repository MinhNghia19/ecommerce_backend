package com.example.ecommerce_backend.services.shop;

import com.example.ecommerce_backend.dtos.ShopDTO;
import com.example.ecommerce_backend.exception.DataNotFoundException;
import com.example.ecommerce_backend.exception.InvalidParamException;
import com.example.ecommerce_backend.exception.InvalidRoleException;
import com.example.ecommerce_backend.models.Role;
import com.example.ecommerce_backend.models.Shop;
import com.example.ecommerce_backend.models.User;
import com.example.ecommerce_backend.repositories.RoleRepository;
import com.example.ecommerce_backend.repositories.ShopRepository;
import com.example.ecommerce_backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShopService implements IShopService{
    private final UserRepository userRepository;

    private final ShopRepository shopRepository;

    private final RoleRepository roleRepository;

    @Override
    public Shop createShop(ShopDTO shopDTO) throws Exception {
         User user = userRepository.findById(shopDTO.getUserId())
                 .orElseThrow(()-> new DataNotFoundException("Cannot find user id"+shopDTO.getUserId()));


        Role role = roleRepository.findById(user.getRole().getId())
                .orElseThrow(() -> new DataNotFoundException("ROLE_DOES_NOT_EXISTS"));

        // Kiểm tra xem vai trò có phải là "SELLER" không
        if (role.getName().equalsIgnoreCase(Role.SELLER)) {
            throw new InvalidRoleException("User role is SELLER can not create shop");
        }

        if(shopRepository.existsByShopName(shopDTO.getShopName())) {
            throw new DataIntegrityViolationException("Shop name already exists");
        }

        Role sellerRole = roleRepository.findByName(Role.SELLER);

        // Cập nhật vai trò của người dùng
        user.setRole(sellerRole);
        userRepository.save(user);
         Shop newShop = Shop.builder()
                 .shopName(shopDTO.getShopName())
                 .description(shopDTO.getDescription())
                 .address(shopDTO.getAddress())
                 .active(true)
                 .user(user)
                 .build();

         return shopRepository.save(newShop);
    }
}
