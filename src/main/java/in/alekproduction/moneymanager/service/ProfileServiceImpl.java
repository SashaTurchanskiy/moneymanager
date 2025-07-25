package in.alekproduction.moneymanager.service;

import in.alekproduction.moneymanager.dto.AuthDto;
import in.alekproduction.moneymanager.dto.ProfileDto;
import in.alekproduction.moneymanager.enitity.ProfileEntity;
import in.alekproduction.moneymanager.repository.ProfileRepo;
import in.alekproduction.moneymanager.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl {

    private final ProfileRepo profileRepo;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public ProfileDto registerProfile(ProfileDto profileDto) {
        log.info("Registering profile");

        ProfileEntity newProfile = toEntity(profileDto);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile.setPassword(passwordEncoder.encode(profileDto.getPassword()));
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

    public boolean activateProfile(String activationToken) {
        log.info("Activating profile with token: {}", activationToken);

        return profileRepo.findByActivationToken(activationToken)
                .map(profile -> {
                    profile.setIsActive(true);
                    profileRepo.save(profile);
                    return true;
                })
                .orElse(false);
    }

    public boolean isAccountActive(String email) {
        log.info("Checking if account is active for email: {}", email);

        return profileRepo.findByEmail(email)
                .map(ProfileEntity::getIsActive)
                .orElse(false);
    }

    public ProfileEntity getCurrentProfile() {
        log.info("Fetching current profile for email: {}", "email");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return profileRepo.findByEmail(authentication.getName())
                        .orElseThrow(() -> new RuntimeException("Profile not found for email: " + authentication.getName()));
    }

    public ProfileDto getPublicProfile(String email){
        log.info("Fetching public profile for email: {}", email);

        ProfileEntity currentUser = null;
        if (email == null){
           currentUser = getCurrentProfile();
        }else {
           currentUser = profileRepo.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Profile not found for email: " + email));
        }
        return ProfileDto.builder()
                .id(currentUser.getId())
                .fullName(currentUser.getFullName())
                .email(currentUser.getEmail())
                .profileImageUrl(currentUser.getProfileImageUrl())
                .createdAt(currentUser.getCreatedAt())
                .updatedAt(currentUser.getUpdatedAt())
                .build();
    }

    public Map<String, Object> authenticateAndGenerateToken(AuthDto authDto) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDto.getEmail(), authDto.getPassword()));
            //Generate JWT token here
            String token = jwtUtils.generateToken(authDto.getEmail());
            return Map.of(
                    "token", token,
                    "user", getPublicProfile(authDto.getEmail())
            );
        }catch (Exception e) {
            log.error("Authentication failed for email: {}", authDto.getEmail(), e);
            throw new RuntimeException("Invalid credentials or account not activated");
        }
    }
}
