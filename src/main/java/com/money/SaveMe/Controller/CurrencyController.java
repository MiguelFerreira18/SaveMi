package com.money.SaveMe.Controller;

import com.money.SaveMe.DTO.Currency.SaveCurrencyDto;
import com.money.SaveMe.DTO.Currency.CurrencyDtoOut;
import com.money.SaveMe.Model.Currency;
import com.money.SaveMe.Service.CurrencyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/currency")
public class CurrencyController {

    private final CurrencyService currencyService;

    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<CurrencyDtoOut>> getAllCurrencies() {
        Iterable<Currency> currencies = currencyService.getAllCurrenciesFromUser();

        if(currencies == null){
            return ResponseEntity.notFound().build();
        }

        List<CurrencyDtoOut> currencyDtos = StreamSupport.stream(currencies.spliterator(),false).map(
                currency -> new CurrencyDtoOut(
                        currency.getId(),
                        currency.getName(),
                        currency.getSymbol()
                )
        ).toList();

        return ResponseEntity.ok(currencyDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CurrencyDtoOut> getCurrencyById(@PathVariable Long id) {
        Currency currency = currencyService.getCurrencyById(id);

        if (currency == null) {
            return ResponseEntity.notFound().build();
        }

        CurrencyDtoOut currencyDtoOut = new CurrencyDtoOut(
                currency.getId(),
                currency.getName(),
                currency.getSymbol()
        );

        return ResponseEntity.ok(currencyDtoOut);
    }

    @PostMapping
    public ResponseEntity<CurrencyDtoOut> saveCurrency(@RequestBody SaveCurrencyDto currency) {
        Currency savedCurrency = currencyService.saveCurrency(currency);
        CurrencyDtoOut currencyDtoOut = new CurrencyDtoOut(
                savedCurrency.getId(),
                savedCurrency.getName(),
                savedCurrency.getSymbol()
        );

        return ResponseEntity.status(201).body(currencyDtoOut);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCurrency(@PathVariable Long id) {
        currencyService.deleteCurrency(id);
        return ResponseEntity.status(202).build();
    }

}
