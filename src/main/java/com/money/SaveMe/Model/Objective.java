package com.money.SaveMe.Model;

import com.money.SaveMe.Validator.CurrentOrFutureYear.CurrentOrFutureYear;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "objective")
public class Objective {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "currency_id", nullable = false)
    private Currency currency;

    @Column(name = "amount", precision = 10, scale = 2)
    private BigDecimal amount;

    @Size(max = 255)
    @Column(name = "description")
    private String description;

    @NotNull
    @CurrentOrFutureYear
    @Column(name = "target", nullable = false)
    private Integer target;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;

    public Objective(User user, Currency currency, BigDecimal amount, String description, Integer target) {
        this.user = user;
        this.currency = currency;
        this.amount = amount;
        this.description = description;
        this.target = target;
    }
}