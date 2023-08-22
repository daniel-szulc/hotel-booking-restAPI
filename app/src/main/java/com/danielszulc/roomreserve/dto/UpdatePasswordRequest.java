package com.danielszulc.roomreserve.dto;

import com.danielszulc.roomreserve.utils.ValidPassword;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordRequest {

    @NotNull @ValidPassword
    private String newPassword;

    @NotNull
    private String currentPassword;

}
