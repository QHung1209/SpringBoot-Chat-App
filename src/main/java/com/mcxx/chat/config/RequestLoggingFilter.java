package com.mcxx.chat.config;

import com.mcxx.chat.auth.dto.response.AuthUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    filterChain.doFilter(request, response);

    String userId = resolveUserId();
    String method = colorizeMethod(request.getMethod());
    String path = request.getRequestURI();
    int status = response.getStatus();

    log.info("userId={} {} {} {}", userId, method, path, status);
  }

  private String colorizeMethod(String method) {
    AnsiColor color = switch (method) {
      case "GET"    -> AnsiColor.GREEN;
      case "POST"   -> AnsiColor.BLUE;
      case "PUT"    -> AnsiColor.YELLOW;
      case "PATCH"  -> AnsiColor.MAGENTA;
      case "DELETE" -> AnsiColor.RED;
      default       -> AnsiColor.WHITE;
    };
    return AnsiOutput.toString(color, AnsiStyle.BOLD, method, AnsiStyle.NORMAL, AnsiColor.DEFAULT);
  }

  private String resolveUserId() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof AuthUser user) {
      return user.getId().toString();
    }
    return "anonymous";
  }
}
