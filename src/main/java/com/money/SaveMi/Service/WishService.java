package com.money.SaveMi.Service;


import com.money.SaveMi.DTO.Shared.BulkDeleteDto;
import com.money.SaveMi.DTO.Wish.SaveWishDto;
import com.money.SaveMi.DTO.Wish.UpdateWishDto;
import com.money.SaveMi.Model.Currency;
import com.money.SaveMi.Model.User;
import com.money.SaveMi.Model.Wish;
import com.money.SaveMi.Repo.CurrencyRepo;
import com.money.SaveMi.Repo.UserRepo;
import com.money.SaveMi.Repo.WishRepo;
import com.money.SaveMi.Utils.AuthenticationServiceUtil;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.Optional;

@Service
public class WishService {
    private final WishRepo wishRepo;
    private final UserRepo userRepo;
    private final CurrencyRepo currencyRepo;
    private final AuthenticationServiceUtil authUtil;

    public WishService(WishRepo wishRepo, UserRepo userRepo, CurrencyRepo currencyRepo, AuthenticationServiceUtil authUtil) {
        this.wishRepo = wishRepo;
        this.userRepo = userRepo;
        this.currencyRepo = currencyRepo;
        this.authUtil = authUtil;
    }

    public Iterable<Wish> getAllWishes(Optional<YearMonth> month){

        String userId = authUtil.getCurrentUserUuid();
        return month.map( m->{
            int monthValue = m.getMonthValue();
            int yearValue = m.getYear();
            return wishRepo.findAllByUserIdFilteredByMonthAndYear(userId,yearValue,monthValue);
        }).orElse(wishRepo.findAllByUserId(userId));
    }

    public Wish getWishById(Long currencyId){
        String userId = authUtil.getCurrentUserUuid();
        return wishRepo.findByUserIdAndCurrencyId(userId,currencyId)
                .orElseThrow(() -> new RuntimeException("Wish not found with currency id: " + currencyId + " for user: " + userId));
    }

    public Wish saveWish(SaveWishDto wishDto){
        String userId = authUtil.getCurrentUserUuid();
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with UUID: " + userId));
        Currency currency = currencyRepo.findByIdAndUserId(wishDto.currencyId(), userId)
                .orElseThrow(() -> new IllegalArgumentException("Currency not found with id: " + wishDto.currencyId() + " for user: " + userId));

        Wish newWish = new Wish(user,currency,wishDto.amount(),wishDto.description(),wishDto.date());
        return wishRepo.save(newWish);
    }

    public Wish updateWish(UpdateWishDto updateWishDto){
        String userId = authUtil.getCurrentUserUuid();
        Wish existingWish = wishRepo.findByIdAndUserId(updateWishDto.id(),userId)
                .orElseThrow(() -> new RuntimeException("Wish not found with id: " + updateWishDto.id() + " for user: " + userId));

        Currency currency = currencyRepo.findByIdAndUserId(updateWishDto.currencyId(), userId)
                .orElseThrow(() -> new IllegalArgumentException("Currency not found with id: " + updateWishDto.currencyId() + " for user: " + userId));

        existingWish.setCurrency(currency);
        existingWish.setAmount(updateWishDto.amount());
        existingWish.setDescription(updateWishDto.description());
        existingWish.setDate(updateWishDto.date());

        return wishRepo.save(existingWish);
    }

    public void bulkDelete(BulkDeleteDto bulkdeleteDto){
        String userId = authUtil.getCurrentUserUuid();

        bulkdeleteDto.ids().forEach(id -> {
            if(wishRepo.findByIdAndUserId(id,userId).isEmpty()){
                throw new RuntimeException("Wish not found with id: " + id + " for user: " + userId + " in bulk");
            }
        });

        wishRepo.bulkDelete(bulkdeleteDto.ids(),userId);
    }
    public void deleteWishById(Long id){
        String userId = authUtil.getCurrentUserUuid();

        if (wishRepo.findByIdAndUserId(id,userId).isEmpty()){
            throw new RuntimeException("Wish not found with id: " + id + " for user: " +userId);
        }

        wishRepo.deleteByIdAndUserId(id,userId);
    }

}
