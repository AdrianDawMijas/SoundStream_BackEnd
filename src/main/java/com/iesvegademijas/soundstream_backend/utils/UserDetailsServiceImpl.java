package com.iesvegademijas.soundstream_backend.utils;

import com.iesvegademijas.soundstream_backend.repository.UserRepository;
import com.iesvegademijas.soundstream_backend.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

/**
 * Implementación personalizada del servicio {@link UserDetailsService},
 * utilizada por Spring Security para autenticar usuarios mediante su email.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Carga un usuario desde la base de datos usando su email.
     * Este método es invocado automáticamente por Spring Security durante el proceso de autenticación.
     *
     * @param email Email del usuario a buscar.
     * @return Instancia de {@link User} si se encuentra.
     * @throws UsernameNotFoundException Si no se encuentra un usuario con el email proporcionado.
     */
    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
    }
}
