package com.lambdaschool.shoppingcart.services;

import com.lambdaschool.shoppingcart.exceptions.ResourceNotFoundException;
import com.lambdaschool.shoppingcart.models.*;
import com.lambdaschool.shoppingcart.repositories.CartRepository;
import com.lambdaschool.shoppingcart.repositories.ProductRepository;
import com.lambdaschool.shoppingcart.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Transactional
@Service(value = "cartService")
public class CartServiceImpl
        implements CartService
{
    /**
     * Connects this service to the cart repository
     */
    @Autowired
    private CartRepository cartrepos;

    /**
     * Connects this service the user repository
     */
    @Autowired
    private UserRepository userrepos;

    /**
     * Connects this service to the product repository
     */
    @Autowired
    private ProductRepository productrepos;

    /**
     * Connects this service to the auditing service in order to get current user name
     */
    @Autowired
    private UserAuditing userAuditing;

    @Override
    public List<Cart> findAllByUserId(Long userid)
    {
        boolean isAdmin = false;

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        User authenticatedUser = userrepos.findById(userid)
                .orElseThrow(() -> new EntityNotFoundException("User" + userid + "Not Found"));

        User user = userrepos.findById(userid)
                .orElseThrow(() -> new EntityNotFoundException("User" + userid + "Not Found"));

        for(UserRole ur : user.getRoles())
        {
            if(ur.getRole().getName() == "ADMIN") isAdmin = true;
        }

        if(userid == authenticatedUser.getUserid() || isAdmin) {
            return cartrepos.findAllByUser_Userid(userid);
        } else throw new AuthenticationCredentialsNotFoundException("You are not authorized");
    }

    @Override
    public Cart findCartById(long id)
    {
        return cartrepos.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car id " + id + " not found!"));
    }

    @Transactional
    @Override
    public Cart save(User user,
                     Product product)
    {
        Cart newCart = new Cart();

        User dbuser = userrepos.findById(user.getUserid())
                .orElseThrow(() -> new ResourceNotFoundException("User id " + user.getUserid() + " not found"));
        newCart.setUser(dbuser);

        Product dbproduct = productrepos.findById(product.getProductid())
                .orElseThrow(() -> new ResourceNotFoundException("Product id " + product.getProductid() + " not found"));

        CartItem newCartItem = new CartItem();
        newCartItem.setCart(newCart);
        newCartItem.setProduct(dbproduct);
        newCartItem.setComments("");
        newCartItem.setQuantity(1);
        newCart.getProducts()
                .add(newCartItem);

        return cartrepos.save(newCart);
    }

    @Transactional
    @Override
    public Cart save(Cart cart,
                     Product product)
    {
        Cart updateCart = cartrepos.findById(cart.getCartid())
                .orElseThrow(() -> new ResourceNotFoundException("Cart Id " + cart.getCartid() + " not found"));
        Product updateProduct = productrepos.findById(product.getProductid())
                .orElseThrow(() -> new ResourceNotFoundException("Product id " + product.getProductid() + " not found"));

        if (cartrepos.checkCartItems(updateCart.getCartid(), updateProduct.getProductid())
                .getCount() > 0)
        {
            cartrepos.updateCartItemsQuantity(userAuditing.getCurrentAuditor()
                                                      .get(), updateCart.getCartid(), updateProduct.getProductid(), 1);
        } else
        {
            cartrepos.addCartItems(userAuditing.getCurrentAuditor()
                                           .get(), updateCart.getCartid(), updateProduct.getProductid());
        }

        return cartrepos.save(updateCart);
    }

    @Transactional
    @Override
    public void delete(Cart cart,
                       Product product)
    {
        Cart updateCart = cartrepos.findById(cart.getCartid())
                .orElseThrow(() -> new ResourceNotFoundException("Cart Id " + cart.getCartid() + " not found"));
        Product updateProduct = productrepos.findById(product.getProductid())
                .orElseThrow(() -> new ResourceNotFoundException("Product id " + product.getProductid() + " not found"));

        if (cartrepos.checkCartItems(updateCart.getCartid(), updateProduct.getProductid())
                .getCount() > 0)
        {
            cartrepos.updateCartItemsQuantity(userAuditing.getCurrentAuditor()
                                                      .get(), updateCart.getCartid(), updateProduct.getProductid(), -1);
            cartrepos.removeCartItemsQuantityZero();
            cartrepos.removeCartWithNoProducts();
        } else
        {
            throw new ResourceNotFoundException("Cart id " + updateCart.getCartid() + " Product id " + updateProduct.getProductid() + " combo not found");
        }
    }
}
