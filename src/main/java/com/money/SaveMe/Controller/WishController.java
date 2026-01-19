package com.money.SaveMe.Controller;

import com.money.SaveMe.DTO.Wish.SaveWishDto;
import com.money.SaveMe.DTO.Wish.UpdateWishDto;
import com.money.SaveMe.DTO.Wish.WishOutputDto;
import com.money.SaveMe.Model.Wish;
import com.money.SaveMe.Service.WishService;
import jakarta.validation.constraints.DecimalMin;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/wish")
public class WishController {

    private final WishService wishService;

    public WishController(WishService wishService) {
        this.wishService = wishService;
    }


    @GetMapping("/all")
    public ResponseEntity<Iterable<WishOutputDto>> getAllWishes(){
        Iterable<Wish> wishes = wishService.getAllWishes();

        if (wishes == null) {
            return ResponseEntity.notFound().build();
        }

        List<WishOutputDto> wishesDto = StreamSupport.stream(wishes.spliterator(),false)
                .map(wish -> new WishOutputDto(wish.getId(),
                        wish.getCurrency().getSymbol(),
                        wish.getDescription(),
                        wish.getAmount(),
                        wish.getUser().getId(),
                        wish.getDate()))
                .toList();

        return ResponseEntity.ok(wishesDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WishOutputDto> getWishById(@PathVariable Long id){
         Wish wish = wishService.getWishById(id);

        if (wish == null) {
            return ResponseEntity.notFound().build();
        }

        WishOutputDto wishDto = new WishOutputDto(wish.getId(),
                wish.getCurrency().getSymbol(),
                wish.getDescription(),
                wish.getAmount(),
                wish.getUser().getId(),
                wish.getDate());

        return ResponseEntity.ok(wishDto);
    }

    @PostMapping
    public ResponseEntity<WishOutputDto> saveWish(@RequestBody SaveWishDto saveWishDto){
        Wish wish = wishService.saveWish(saveWishDto);

        if (wish == null) {
            return ResponseEntity.badRequest().build();
        }

        WishOutputDto wishDto = new WishOutputDto(wish.getId(),
                wish.getCurrency().getSymbol(),
                wish.getDescription(),
                wish.getAmount(),
                wish.getUser().getId(),
                wish.getDate());

        return ResponseEntity.ok(wishDto);
    }

    @PutMapping
    public ResponseEntity<WishOutputDto> updateWish(@RequestBody UpdateWishDto updateWishDto){
        Wish wish = wishService.updateWish(updateWishDto);

        if (wish == null) {
            return ResponseEntity.badRequest().build();
        }

        WishOutputDto wishDto = new WishOutputDto(wish.getId(),
                wish.getCurrency().getSymbol(),
                wish.getDescription(),
                wish.getAmount(),
                wish.getUser().getId(),
                wish.getDate());

        return ResponseEntity.ok(wishDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWish(@PathVariable Long id){
        wishService.deleteWishById(id);
        return ResponseEntity.noContent().build();
    }

}
