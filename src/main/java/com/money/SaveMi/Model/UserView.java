package com.money.SaveMi.Model;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserView {
    String id;
    String name;
    List<String> authorities;
}
