package in.alekproduction.moneymanager.service;

import in.alekproduction.moneymanager.dto.ProfileDto;
import in.alekproduction.moneymanager.enitity.ProfileEntity;
import in.alekproduction.moneymanager.repository.ProfileRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl {

    private final ProfileRepo profileRepo;
    private final EmailService emailService;

    public ProfileDto registerProfile(ProfileDto profileDto) {
        log.info("Registering profile");

        ProfileEntity newProfile = toEntity(profileDto);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile = profileRepo.save(newProfile);
        //send activation email here if needed
        String activationLink = "http://localhost:8787/api/v1.0/profile/activate?token=" + newProfile.getActivationToken();
        String subject = "Activate ur Money manager account";
        String body = "Click on the link to activate your account: " + activationLink;
        emailService.sendEmail(newProfile.getEmail(), subject, body);
        return toDto(newProfile);
    }

    public ProfileEntity toEntity(ProfileDto profileDto) {
        return ProfileEntity.builder()
                .id(profileDto.getId())
                .fullName(profileDto.getFullName())
                .email(profileDto.getEmail())
                .password(profileDto.getPassword())
                .profileImageUrl(profileDto.getProfileImageUrl())
                .createdAt(profileDto.getCreatedAt())
                .updatedAt(profileDto.getUpdatedAt())
                .build();
    }

    public ProfileDto toDto(ProfileEntity profileEntity) {
        return ProfileDto.builder()
                .id(profileEntity.getId())
                .fullName(profileEntity.getFullName())
                .email(profileEntity.getEmail())
                .profileImageUrl(profileEntity.getProfileImageUrl())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .build();
    }
}
