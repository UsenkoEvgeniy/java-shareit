package ru.practicum.shareit.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class UserDto {

    public UserDto(String name, String email) {
        this.name = name;
        this.email = email;
    }

    private Long id;
    @NotBlank
    private String name;
    @Email
    @NotNull
    private String email;
}
