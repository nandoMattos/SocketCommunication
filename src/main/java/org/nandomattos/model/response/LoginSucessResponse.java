package org.nandomattos.model.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginSucessResponse {
    int status;
    String token;
}
