package in.alekproduction.moneymanager.dto;

import in.alekproduction.moneymanager.enitity.ProfileEntity;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CategoryDto {

    private Long id;
    private Long profileId;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String icon;
    private String type; // e.g., "income" or "expense"

}
