package com.actividades.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.actividades.modelo.Empleado;
import com.actividades.modelo.EmpleadoDAO;
import com.actividades.modelo.Permiso;
import com.actividades.modelo.TipoUsuario;

public class ServicioAutenticacion implements UserDetailsService {

	/**
	 * Este metodo es invocado en el momento de la autenticacion paraa recuperar 
	 * los datos del usuario.
	 * 
	 */
	@Override
	public UserDetails loadUserByUsername(String nombreUsuario) throws UsernameNotFoundException {
		try {
			EmpleadoDAO usuarioDAO = new EmpleadoDAO();
			Empleado empleado; 
			User usuarioSpring;
			List<GrantedAuthority> privilegios; 
			empleado = usuarioDAO.getUsuario(nombreUsuario);
			privilegios = obtienePrivilegios(empleado.getTipoUsuario());
			
			usuarioSpring = new User(empleado.getUsuario(), empleado.getClave(), privilegios);

			return usuarioSpring;
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
			return null;
		}
		
	}

	/**
	 * Elabora una lista de objetos del tipo GrantedAuthority en base a los permisos
	 * del usuario.
	 * 
	 * @param usuario
	 * @return
	 */
	private List<GrantedAuthority> obtienePrivilegios(TipoUsuario tipoUsuario) {
		List<GrantedAuthority> listaPrivilegios = new ArrayList<GrantedAuthority>(); 
		
	    for(Permiso permiso  : tipoUsuario.getPermisos())
	    	listaPrivilegios.add(new SimpleGrantedAuthority(permiso.getTipoUsuario().getDescripcion()));

		return listaPrivilegios;
	}

}
