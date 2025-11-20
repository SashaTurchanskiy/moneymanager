package in.alekproduction.moneymanager.service;

import in.alekproduction.moneymanager.dto.CategoryDto;
import in.alekproduction.moneymanager.enitity.Category;
import in.alekproduction.moneymanager.enitity.ProfileEntity;
import in.alekproduction.moneymanager.repository.CategoryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final ProfileServiceImpl profileService;
    private final CategoryRepo categoryRepo;

    //save category
    public CategoryDto saveCategory(CategoryDto categoryDto){
        ProfileEntity profile = profileService.getCurrentProfile();
        if (categoryRepo.existByNameAndProfileId(categoryDto.getName(), profile.getId())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category with the same name already exists");
        }
        Category category = toEntity(categoryDto, profile);
        Category savedCategory = categoryRepo.save(category);
        return toDto(savedCategory);
    }






    //helper method
    private Category toEntity(CategoryDto categoryDto, ProfileEntity profile){
        return Category.builder()
                .name(categoryDto.getName())
                .icon(categoryDto.getIcon())
                .type(categoryDto.getType())
                .profile(profile)
                .build();
    }
    private CategoryDto toDto(Category category){
        return CategoryDto.builder()
                .id(category.getId())
                .profileId(category.getProfile() != null ? category.getProfile().getId() : null)
                .name(category.getName())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .icon(category.getIcon())
                .type(category.getType())
                .build();
    }

}
