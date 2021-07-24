package com.hoangducduy.airbnb.controller;

import com.hoangducduy.airbnb.constant.ERole;
import com.hoangducduy.airbnb.dto.request.LoginRequest;
import com.hoangducduy.airbnb.dto.request.SignupRequest;
import com.hoangducduy.airbnb.dto.response.JwtResponse;
import com.hoangducduy.airbnb.dto.response.MessageResponse;
import com.hoangducduy.airbnb.entity.Role;
import com.hoangducduy.airbnb.entity.User;
import com.hoangducduy.airbnb.repository.RoleRepository;
import com.hoangducduy.airbnb.repository.UserRepository;
import com.hoangducduy.airbnb.security.JwtUtils;
import com.hoangducduy.airbnb.service.impl.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Validated @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(),
                userDetails.getFullName(), userDetails.getEmail(), roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Validated @RequestBody SignupRequest signupRequest) {

        if (signupRequest.getUsername().contains("admin") || signupRequest.getUsername().contains("manager")
                || signupRequest.getUsername().contains("administrator")) {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid Username!"));
        }

        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Username is already taken!"));
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Email is already taken!"));
        }

        if (userRepository.existsByPhone(signupRequest.getPhone())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Phone Number is already taken!"));
        }

        // Creating user's account
        User user = new User(signupRequest.getUsername(), signupRequest.getFullName(), signupRequest.getPhone(),
                signupRequest.getEmail(), passwordEncoder.encode(signupRequest.getPassword()));

        Set<String> strRoleSet = signupRequest.getRole();
        Set<Role> roleSet = new HashSet<>();

        if (strRoleSet == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roleSet.add(userRole);
        } else {
            strRoleSet.forEach(role -> {
                switch (role) {
                    case "ROLE_ADMIN":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roleSet.add(adminRole);
                        break;
                    case "ROLE_MODERATOR":
                        Role pmRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roleSet.add(pmRole);
                        break;
                    default:
                            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roleSet.add(userRole);
                }
            });
        }
        user.setRoles(roleSet);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
