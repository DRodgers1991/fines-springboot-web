package com.rodgers.fines.web.user;

import com.rodgers.fines.web.common.vo.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SignUpVo extends User {
    private String result;

    public SignUpVo(){
        super();
    }

    public SignUpVo(String username, String password) {
        super(username, password);
    }

    @Override
    public String toString() {
        return String.format("username : %s, password : %s, result %s",getUsername(),getPassword(),result);
    }
}
