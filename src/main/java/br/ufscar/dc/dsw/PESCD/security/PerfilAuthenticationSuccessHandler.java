package br.ufscar.dc.dsw.PESCD.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PerfilAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private static final Map<String, String> DASHBOARD_POR_ROLE = new LinkedHashMap<>();

    static {
        DASHBOARD_POR_ROLE.put("ROLE_ADMIN", "/admin/dashboard");
        DASHBOARD_POR_ROLE.put("ROLE_SECRETARIO", "/secretario/ofertas");
        DASHBOARD_POR_ROLE.put("ROLE_RESPONSAVEL", "/responsavel/ofertas");
        DASHBOARD_POR_ROLE.put("ROLE_SUPERVISOR", "/supervisor/dashboard");
        DASHBOARD_POR_ROLE.put("ROLE_ALUNO", "/aluno/dashboard");
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        String dashboard = DASHBOARD_POR_ROLE.entrySet().stream()
                .filter(entry -> roles.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse("/login?error=role");

        response.sendRedirect(request.getContextPath() + dashboard);
    }
}
