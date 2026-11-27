package com.money.SaveMi.Controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.money.SaveMi.DTO.Currency.CurrencyDtoOut;
import com.money.SaveMi.DTO.Currency.SaveCurrencyDto;
import com.money.SaveMi.DTO.Shared.BulkDeleteDto;
import com.money.SaveMi.Model.Currency;
import com.money.SaveMi.Service.CurrencyService;

@RestController
@RequestMapping("/api/currencies")
public class CurrencyController {

    private final CurrencyService currencyService;

    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping
    public ResponseEntity<Page<CurrencyDtoOut>> getAllCurrencies(@PageableDefault(5) Pageable pageable) {
        Page<CurrencyDtoOut> result = currencyService
                .getAllCurrenciesFromUser(pageable)
                .map(
                        currency -> new CurrencyDtoOut(
                                currency.getId(),
                                currency.getName(),
                                currency.getSymbol()));
        return ResponseEntity.ok(result);
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
                currency.getSymbol());

        return ResponseEntity.ok(currencyDtoOut);
    }

    @PostMapping
    public ResponseEntity<CurrencyDtoOut> saveCurrency(@RequestBody SaveCurrencyDto currency) {
        Currency savedCurrency = currencyService.saveCurrency(currency);
        CurrencyDtoOut currencyDtoOut = new CurrencyDtoOut(
                savedCurrency.getId(),
                savedCurrency.getName(),
                savedCurrency.getSymbol());

        return ResponseEntity.status(201).body(currencyDtoOut);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteCurrencies(@RequestBody BulkDeleteDto bulkDeleteDto) {
        currencyService.bulkDelete(bulkDeleteDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCurrency(@PathVariable Long id) {
        currencyService.deleteCurrency(id);
        return ResponseEntity.status(202).build();
    }

}
