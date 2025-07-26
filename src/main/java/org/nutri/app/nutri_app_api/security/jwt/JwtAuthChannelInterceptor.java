package org.nutri.app.nutri_app_api.security.jwt; // Use o seu pacote correto

import io.jsonwebtoken.Claims;
import org.nutri.app.nutri_app_api.security.models.users.RoleName;
import org.nutri.app.nutri_app_api.security.services.RoleUsernamePasswordAuthToken;
import org.nutri.app.nutri_app_api.security.services.UserDetailsImpl;
import org.nutri.app.nutri_app_api.security.services.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Optional;

@Component
public class JwtAuthChannelInterceptor implements ChannelInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthChannelInterceptor.class);

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthChannelInterceptor(JwtUtils jwtUtils, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) {
            return message;
        }

        // 1. Lógica de AUTENTICAÇÃO na conexão
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // Extrai o token do header 'Authorization' enviado pelo cliente STOMP
            Optional<String> tokenOptional = Optional.ofNullable(accessor.getFirstNativeHeader("Authorization"))
                    .filter(header -> header.startsWith("Bearer "))
                    .map(header -> header.substring(7));

            if (tokenOptional.isPresent()) {
                try {
                    String jwtToken = tokenOptional.get();

                    // Usa seu JwtUtils para validar e extrair as claims
                    Claims claims = jwtUtils.validateAndParseClaims(jwtToken);

                    String email = claims.getSubject();
                    RoleName role = RoleName.valueOf(claims.get("role", String.class));

                    // Usa seu UserDetailsService customizado para carregar o usuário
                    UserDetails userDetails = userDetailsService.loadUserByUsernameAndRole(email, role);

                    // Usa seu token de autenticação customizado para consistência
                    RoleUsernamePasswordAuthToken authentication = new RoleUsernamePasswordAuthToken(
                            userDetails,
                            null,
                            role,
                            userDetails.getAuthorities()
                    );

                    // Associa o usuário autenticado à sessão WebSocket
                    accessor.setUser(authentication);
                    logger.info("WebSocket user authenticated successfully: {}", email);

                } catch (Exception e) {
                    logger.error("WebSocket authentication failed: {}", e.getMessage());
                    // Retornar null impede a continuação do processamento da mensagem,
                    // efetivamente rejeitando a conexão.
                    return null;
                }
            }
        }
        // 2. Lógica de AUTORIZAÇÃO na inscrição
        else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            final Principal user = accessor.getUser();

            // Garante que o usuário está autenticado
            if (user == null || !(user instanceof RoleUsernamePasswordAuthToken)) {
                logger.warn("Unauthorized subscription attempt: User not authenticated.");
                throw new AccessDeniedException("Cannot subscribe. User is not authenticated.");
            }

            final String destination = accessor.getDestination();
            // Protege os tópicos de notificação
            if (destination != null && destination.startsWith("/topic/notificacoes/")) {
                // Extrai o ID do usuário do destino (ex: /topic/notificacoes/123 -> 123)
                String destinationUserIdStr = destination.substring(destination.lastIndexOf('/') + 1);

                // Obtém o UserDetailsImpl do token de autenticação para pegar o ID
                UserDetailsImpl userDetails = (UserDetailsImpl) ((RoleUsernamePasswordAuthToken) user).getPrincipal();
                String authenticatedUserIdStr = userDetails.getId().toString();

                // Compara o ID do usuário autenticado com o ID do tópico
                if (!authenticatedUserIdStr.equals(destinationUserIdStr)) {
                    logger.warn("Authorization failed. User '{}' tried to subscribe to topic of user '{}'.", authenticatedUserIdStr, destinationUserIdStr);
                    throw new AccessDeniedException("You are not authorized to subscribe to this topic.");
                }
                logger.info("User '{}' successfully subscribed to destination '{}'", authenticatedUserIdStr, destination);
            }
        }

        return message;
    }
}