/* (C) 2024 AladdinSystem License */
package aladdinsys.api.task.utils.jwt;


import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class UserAuthentication extends UsernamePasswordAuthenticationToken {

  public UserAuthentication(
      String principal, String credentials, List<GrantedAuthority> authorities) {
    super(principal, credentials, authorities);
  }
}
