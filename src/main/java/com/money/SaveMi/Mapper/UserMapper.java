package com.money.SaveMi.Mapper;

import com.money.SaveMi.DTO.Authentication.SignUpRequest;
import com.money.SaveMi.Model.User;
import com.money.SaveMi.Model.UserView;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class UserMapper {
    public UserView toUserView(User user) {
        if (user == null) {
            return null;
        }

        String id = user.getId();
        String name = user.getName();
        List<String> authorities = grantedAuthorityCollectionToRoleSet(user.getAuthorities());

        return new UserView(id, name, authorities);
    }

    public User toUser(SignUpRequest signUpRequest) {
        if (signUpRequest == null) {
            return null;
        }

        User user = new User();

        user.setEmail(signUpRequest.getEmail());
        user.setName(signUpRequest.getName());
        user.setPassword(signUpRequest.getPassword());



        return user;
    }

    protected String grantedAuthorityToRole(GrantedAuthority grantedAuthority) {
        if (grantedAuthority == null) {
            return null;
        }
        return grantedAuthority.getAuthority();
    }

    protected List<String> grantedAuthorityCollectionToRoleSet(Collection<? extends GrantedAuthority> collection) {
        if (collection == null) {
            return null;
        }

        List<String> list = new ArrayList<>();
        for (GrantedAuthority grantedAuthority : collection) {
            list.add(grantedAuthorityToRole(grantedAuthority));
        }

        return list;
    }
}
