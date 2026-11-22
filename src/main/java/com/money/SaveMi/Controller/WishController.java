package com.money.SaveMi.Controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.money.SaveMi.DTO.Shared.BulkDeleteDto;
import com.money.SaveMi.DTO.Wish.SaveWishDto;
import com.money.SaveMi.DTO.Wish.UpdateWishDto;
import com.money.SaveMi.DTO.Wish.WishOutputDto;
import com.money.SaveMi.Filter.WishFilter;
import com.money.SaveMi.Model.Wish;
import com.money.SaveMi.Service.WishService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/wishes")
public class WishController {

    private final WishService wishService;

    public WishController(WishService wishService) {
        this.wishService = wishService;
    }

    @GetMapping
    public ResponseEntity<Page<WishOutputDto>> getAllWishes(WishFilter filter,
            @PageableDefault(size = 5) Pageable pageable) {
        Page<WishOutputDto> result = wishService
                .getAllWishes(filter, pageable)
                .map(wish -> new WishOutputDto(wish.getId(),
                        wish.getCurrency().getSymbol(),
                        wish.getDescription(),
                        wish.getAmount(),
                        wish.getUser().getId(),
                        wish.getDate()));

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WishOutputDto> getWishById(@PathVariable Long id) {
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
    public ResponseEntity<WishOutputDto> saveWish(@RequestBody @Valid SaveWishDto saveWishDto) {
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
    public ResponseEntity<WishOutputDto> updateWish(@RequestBody @Valid UpdateWishDto updateWishDto) {
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

    @DeleteMapping
    public ResponseEntity<Void> deleteWishes(@RequestBody @Valid BulkDeleteDto bulkDeleteDto) {
        wishService.bulkDelete(bulkDeleteDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWish(@PathVariable Long id) {
        wishService.deleteWishById(id);
        return ResponseEntity.noContent().build();
    }

}
