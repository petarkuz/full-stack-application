package mk.ukim.finki.application.auth;

import mk.ukim.finki.application.customer.Customer;
import mk.ukim.finki.application.customer.CustomerDTO;
import mk.ukim.finki.application.customer.CustomerDTOMapper;
import mk.ukim.finki.application.jwt.JWTUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final CustomerDTOMapper customerDTOMapper;

    public AuthenticationService(JWTUtil jwtUtil, AuthenticationManager authenticationManager, CustomerDTOMapper customerDTOMapper) {
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.customerDTOMapper = customerDTOMapper;
    }

    public AuthenticationResponse login(AuthenticationRequest request) {
        Authentication authentication = this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        Customer principal = (Customer) authentication.getPrincipal();//Returns the UserDetails
        CustomerDTO customerDTO = this.customerDTOMapper.apply(principal);

        String token = this.jwtUtil.issueToken(customerDTO.username(), customerDTO.roles());
        return new AuthenticationResponse(token, customerDTO);
    }
}
