package in.alekproduction.moneymanager.service;

import in.alekproduction.moneymanager.dto.CategoryDto;
import in.alekproduction.moneymanager.enitity.Category;
import in.alekproduction.moneymanager.enitity.ProfileEntity;
import in.alekproduction.moneymanager.repository.CategoryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final ProfileServiceImpl profileService;
    private final CategoryRepo categoryRepo;

    //save category
    public CategoryDto saveCategory(CategoryDto categoryDto){
        ProfileEntity profile = profileService.getCurrentProfile();
        if (categoryRepo.existsByNameAndProfileId(categoryDto.getName(), profile.getId())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category with the same name already exists");
        }
        Category category = toEntity(categoryDto, profile);
        Category savedCategory = categoryRepo.save(category);
        return toDto(savedCategory);
    }
    //get categories for current user
    public List<CategoryDto> getCategoriesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<Category> categories = categoryRepo.findByProfileId(profile.getId());
        return categories.stream().map(this::toDto).toList();
    }
    //get categories by type for current user
    public List<CategoryDto> getCategoriesByTypeForCurrentUser(String type){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<Category> entities = categoryRepo.findByTypeAndProfileId(type, profile.getId());
        return entities.stream().map(this::toDto).toList();
    }
    public CategoryDto updateCategory(Long categoryId, CategoryDto dto){
        ProfileEntity profile = profileService.getCurrentProfile();
        Category existingCategory = categoryRepo.findByIdAndProfileId(categoryId, profile.getId())
                .orElseThrow(()-> new RuntimeException("Category not found or not accessible"));
        existingCategory.setName(dto.getName());
        existingCategory.setIcon(dto.getIcon());
        existingCategory = categoryRepo.save(existingCategory);
        return toDto(existingCategory);
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
