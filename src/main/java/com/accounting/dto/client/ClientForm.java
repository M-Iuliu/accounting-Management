package com.accounting.dto.client;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ClientForm {

    private String name;
    private String title;
    private String surname;
    private String telephone;
    private String email;

}
