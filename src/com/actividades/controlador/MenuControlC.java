package com.actividades.controlador;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.mail.MessagingException;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.image.AImage;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Image;
import org.zkoss.zul.Include;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;
import org.zkoss.zul.Window;

import com.actividades.modelo.Empleado;
import com.actividades.modelo.EmpleadoDAO;
import com.actividades.modelo.Menu;
import com.actividades.modelo.MenuDAO;
import com.actividades.modelo.Permiso;
import com.actividades.modelo.PermisoDAO;
import com.actividades.util.FileUtil;
import com.actividades.util.SecurityUtil;


public class MenuControlC {
	@Wire Tree menu;
	@Wire Window windowRoot;
	@Wire Include areaContenido;
	@Wire Image fotoUsuario;
	Menu opcionSeleccionado;
	EmpleadoDAO usuarioDAO = new EmpleadoDAO();
	PermisoDAO permisoDAO = new PermisoDAO();
	MenuDAO menuDAO = new MenuDAO();
	List<Menu> listaPermisosPadre = new ArrayList<Menu>();
	List<Permiso> listaPermisosHijo = new ArrayList<Permiso>();
	
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException, MessagingException{
		Selectors.wireComponents(view, this, false);
		loadTree();
		cargarFotoUsuario();
		//startLongOperation();
	}
	
	public void loadTree() throws IOException{		
		Empleado usuario = usuarioDAO.getUsuario(SecurityUtil.getUser().getUsername().trim()); 
		if (usuario != null){
			//listaPermisosPadre = permisoDAO.getListaPermisosPadre(usuario.getSegPerfil().getIdPerfil());
			listaPermisosHijo = permisoDAO.getListaPermisosHijo(usuario.getTipoUsuario().getIdTipoUsuario());
			
			//obtener la lista de menus padre de cada menu
			boolean bandera = false;
			for(Permiso per : listaPermisosHijo) {
				bandera = false;
				List<Menu> listaMenu = menuDAO.getMenuPadre(per.getMenu().getIdMenuPadre());
				if(listaMenu.size() > 0) {
					for(Menu mnu : listaPermisosPadre) {
						for(Menu mnu2 : listaMenu) {
							if(mnu.getIdMenu() == mnu2.getIdMenu())
								bandera = true;
						}
					}
					if(bandera == false)
						listaPermisosPadre.add(listaMenu.get(0));
				}
			}
			
			if (listaPermisosPadre.size() > 0) { //si tiene permisos el usuario
				//capturar solo los menus
				List<Menu> listaMenu = new ArrayList<Menu>();
				for(Menu permiso : listaPermisosPadre) {
					listaMenu.add(permiso);
				}
				//ordenar el menu por posiciones
				Collections.sort(listaMenu, new Comparator<Menu>() {
					@SuppressWarnings("deprecation")
					@Override
					public int compare(Menu p1, Menu p2) {
						return new Integer(p1.getPosicion()).compareTo(new Integer(p2.getPosicion()));
					}
				});
				menu.appendChild(getTreechildren(listaMenu));   
			}			
		}
	}
	public Treechildren getTreechildren(List<Menu> listaMenu) {
		Treechildren retorno = new Treechildren();
		for(Menu opcion : listaMenu) {
			Treeitem ti = getTreeitem(opcion);
			retorno.appendChild(ti);
			List<Menu> listaPadreHijo = new ArrayList<Menu>();
			for(Permiso permiso : listaPermisosHijo) {
				if(permiso.getMenu().getIdMenuPadre() == opcion.getIdMenu()) {
					listaPadreHijo.add(permiso.getMenu());
				}
			}
			if (!listaPadreHijo.isEmpty()) {
				Collections.sort(listaPadreHijo, new Comparator<Menu>() {
					@SuppressWarnings("deprecation")
					@Override
					public int compare(Menu p1, Menu p2) {
						return new Integer(p1.getPosicion()).compareTo(new Integer(p2.getPosicion()));
					}
				});
				ti.appendChild(getTreechildren(listaPadreHijo));
			}
		}
		return retorno;
	}
	@SuppressWarnings({ "rawtypes", "deprecation", "unchecked" })
	public Treeitem getTreeitem(Menu opcion) {
		Treeitem retorno = new Treeitem();
		Treerow tr = new Treerow();
		Treecell tc = new Treecell(opcion.getDescripcion());
		if (opcion.getIcono() != null) {
			//tc.setIconSclass(opcion.getImagen());
			tc.setSrc(opcion.getIcono());
		}
		tr.addEventListener(Events.ON_CLICK, new EventListener() {
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				opcionSeleccionado = opcion; 
				if(opcionSeleccionado.getUri() != null){
					loadContenido(opcionSeleccionado);
				}
			}
		});
		
		tr.appendChild(tc);
		retorno.appendChild(tr);
		retorno.setOpen(false);
		return retorno;
	}
	@NotifyChange({"areaContenido"})
	public void loadContenido(Menu opcion) {
		
		if (opcion.getUri().toLowerCase().substring(0, 2).toLowerCase().equals("http")) {
			Sessions.getCurrent().setAttribute("FormularioActual", null);
			Executions.getCurrent().sendRedirect(opcion.getUri(), "_blank");			
		} else {
			Sessions.getCurrent().setAttribute("FormularioActual", opcion);	
			areaContenido.setSrc(opcion.getUri());
		}	
		
	}
	public void cargarFotoUsuario() {
		Empleado us = usuarioDAO.getUsuario(SecurityUtil.getUser().getUsername());
		if(us != null) {
			if(us.getFoto() != null) {
				fotoUsuario.setContent(getImagenUsuario(us));
			}else {
				fotoUsuario.setSrc( "/imagenes/foto.png");
			}
		}else {
			fotoUsuario.setSrc("/imagenes/foto.png");
		}
	}
	public AImage getImagenUsuario(Empleado emp) {
		AImage retorno = null;
		if (emp.getFoto() != null) {
			try {
				retorno = FileUtil.getImagenTamanoFijo(new AImage(emp.getFoto()), 100, -1);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return retorno; 
	}
	public String getNombreUsuario() {
		Empleado us = usuarioDAO.getUsuario(SecurityUtil.getUser().getUsername());
		String[] partsNombre = us.getPersona().getNombre().split(" ");
		String[] partsApellido = us.getPersona().getApellido().split(" ");
		return partsNombre[0] + " " + partsApellido[0];
	}
	public Empleado getTipoUsuario() {
		Empleado us = usuarioDAO.getUsuario(SecurityUtil.getUser().getUsername());
		return us;
	}
}
