package com.tecsup.demo_Security_JWT.controller;

import com.tecsup.demo_Security_JWT.dto.JwtResponse;
import com.tecsup.demo_Security_JWT.dto.LoginRequest;
import com.tecsup.demo_Security_JWT.dto.MessageResponse;
import com.tecsup.demo_Security_JWT.dto.SignupRequest;
import com.tecsup.demo_Security_JWT.entity.Role;
import com.tecsup.demo_Security_JWT.entity.User;
import com.tecsup.demo_Security_JWT.jwt.JwtUtils;
import com.tecsup.demo_Security_JWT.repository.RoleRepository;
import com.tecsup.demo_Security_JWT.repository.UserRepository;
import com.tecsup.demo_Security_JWT.service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @Transactional
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username ya existe"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email ya existe"));
        }

        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            Role userRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new RuntimeException("Rol USER no encontrado"));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role.toUpperCase()) {
                    case "ADMIN":
                        Role adminRole = roleRepository.findByName("ADMIN")
                                .orElseThrow(() -> new RuntimeException("Rol ADMIN no encontrado"));
                        roles.add(adminRole);
                        break;
                    case "MODERATOR":
                        Role modRole = roleRepository.findByName("MODERATOR")
                                .orElseThrow(() -> new RuntimeException("Rol MODERATOR no encontrado"));
                        roles.add(modRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName("USER")
                                .orElseThrow(() -> new RuntimeException("Rol USER no encontrado"));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Usuario registrado correctamente"));
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                jwt,
                roles));
    }
}