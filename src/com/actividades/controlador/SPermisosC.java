package com.actividades.controlador;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;

import com.actividades.modelo.Menu;
import com.actividades.modelo.MenuDAO;
import com.actividades.modelo.Permiso;
import com.actividades.modelo.PermisoDAO;
import com.actividades.modelo.TipoUsuario;
import com.actividades.modelo.TipoUsuarioDAO;


@SuppressWarnings("serial")
public class SPermisosC extends SelectorComposer<Component>{
	TipoUsuarioDAO tipoUsuarioDAO = new TipoUsuarioDAO();
	@Wire private Combobox cboPerfiles;
	MenuDAO menuDAO = new MenuDAO();
	PermisoDAO permisoDAO = new PermisoDAO();
	
	@Wire private Listbox lstPermisosTodos;
	@Wire private Listbox lstPermisoPerfil;
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		cargarMenuDisponibles();
		
	}
	@Listen("onClick=#btnAgregar")
	public void agregar() {
		if(cboPerfiles.getSelectedItem() == null) {
			Clients.showNotification("Debe seleccionar un perfil");
			return;
		}
		if(lstPermisosTodos.getSelectedItem().getValue() == null){
			Clients.showNotification("Debe seleccionar un menu para agreagar");
			return;
		}
		TipoUsuario perfilSeleccionado = (TipoUsuario)cboPerfiles.getSelectedItem().getValue();
		Menu menuSeleccionado = (Menu)lstPermisosTodos.getSelectedItem().getValue();
		System.out.println(perfilSeleccionado.getTipoUsuario());
		System.out.println(menuSeleccionado.getDescripcion());
		//grabar 

		EventListener<ClickEvent> clickListener = new EventListener<Messagebox.ClickEvent>() {
			public void onEvent(ClickEvent event) throws Exception {
				if(Messagebox.Button.YES.equals(event.getButton())) {
					tipoUsuarioDAO.getEntityManager().getTransaction().begin();
					Permiso permisoGrabar = new Permiso();
					permisoGrabar.setIdPermiso(null);
					permisoGrabar.setEstado("A");
					permisoGrabar.setMenu(menuSeleccionado);
					permisoGrabar.setTipoUsuario(perfilSeleccionado);
					tipoUsuarioDAO.getEntityManager().persist(permisoGrabar);
					tipoUsuarioDAO.getEntityManager().getTransaction().commit();
					Messagebox.show("Datos grabados con exito");
					cargarPermisosPerfil(perfilSeleccionado.getIdTipoUsuario());
				}
			}
		};
		Messagebox.show("Desea Grabar los Datos?", "Confirmación", new Messagebox.Button[]{
				Messagebox.Button.YES, Messagebox.Button.NO }, Messagebox.QUESTION, clickListener);


	}
	@Listen("onChange = #cboPerfiles")
    public void cambioPerfil() {
		if(cboPerfiles.getSelectedItem() == null) {
			Clients.showNotification("Debe seleccionar un perfil");
			return;
		}
		TipoUsuario perfilSeleccionado = (TipoUsuario)cboPerfiles.getSelectedItem().getValue();
		cargarPermisosPerfil(perfilSeleccionado.getIdTipoUsuario());
    }
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void cargarPermisosPerfil(Integer idPerfil) {
		try {
			List<Permiso> listaPermisos = permisoDAO.getListaPermisosHijo(idPerfil);
			for(Permiso per : listaPermisos) {
				List<Menu> listadoPadre = menuDAO.getMenuPadre(per.getMenu().getIdMenuPadre());
				if(listadoPadre.size() > 0) {						
					per.getMenu().setPresentacion(listadoPadre.get(0).getDescripcion() + " --> " + per.getMenu().getDescripcion());
				}
			}
			lstPermisoPerfil.setModel(new ListModelList(listaPermisos));
			boolean bandera = false;
			//tambien se deben cargar los menu que quedan
			List<Menu> listaMenuDisponibles = menuDAO.getListaHijos();
			List<Menu> listaSobrentes = new ArrayList<>();
			for(Menu menu : listaMenuDisponibles) {
				bandera = false;
				for(Permiso per : listaPermisos) {
					if(menu.getIdMenu() == per.getMenu().getIdMenu()) {
						bandera = true;
					}
				}
				//no eciste
				if(bandera == false) {
					listaSobrentes.add(menu);
				}
			}
			
			if(listaSobrentes.size() > 0) {
				for(Menu mnu : listaSobrentes) {
					List<Menu> listadoPadre = menuDAO.getMenuPadre(mnu.getIdMenuPadre());
					if(listadoPadre.size() > 0) {						
						mnu.setPresentacion(listadoPadre.get(0).getDescripcion() + " --> " + mnu.getDescripcion());
					}
				}
			}
			lstPermisosTodos.setModel(new ListModelList(listaSobrentes));
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	
	@Listen("onClick=#btnQuitar")
	public void quitar() {
		if(cboPerfiles.getSelectedItem() == null) {
			Clients.showNotification("Debe seleccionar un perfil");
			return;
		}
		if(lstPermisoPerfil.getSelectedItem().getValue() == null){
			Clients.showNotification("Debe seleccionar un menu para quitar");
			return;
		}
		TipoUsuario perfilSeleccionado = (TipoUsuario)cboPerfiles.getSelectedItem().getValue();
		Permiso menuSeleccionado = (Permiso)lstPermisoPerfil.getSelectedItem().getValue();
		System.out.println(menuSeleccionado.getMenu().getDescripcion());
		System.out.println(perfilSeleccionado.getTipoUsuario());
		
		EventListener<ClickEvent> clickListener = new EventListener<Messagebox.ClickEvent>() {
			public void onEvent(ClickEvent event) throws Exception {
				if(Messagebox.Button.YES.equals(event.getButton())) {
					tipoUsuarioDAO.getEntityManager().getTransaction().begin();
					Permiso permisoGrabar = menuSeleccionado;
					permisoGrabar.setEstado("I");
					tipoUsuarioDAO.getEntityManager().merge(permisoGrabar);
					tipoUsuarioDAO.getEntityManager().getTransaction().commit();
					Messagebox.show("Menu Quitado con exito");
					cargarPermisosPerfil(perfilSeleccionado.getIdTipoUsuario());
				}
			}
		};
		Messagebox.show("Desea quitar el menu?", "Confirmación", new Messagebox.Button[]{
				Messagebox.Button.YES, Messagebox.Button.NO }, Messagebox.QUESTION, clickListener);
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void cargarMenuDisponibles() {
		try {
			List<Menu> listaMenuDisponibles = menuDAO.getListaHijos();
			if(listaMenuDisponibles.size() > 0) {
				for(Menu mnu : listaMenuDisponibles) {
					List<Menu> listadoPadre = menuDAO.getMenuPadre(mnu.getIdMenuPadre());
					if(listadoPadre.size() > 0) {						
						mnu.setPresentacion(listadoPadre.get(0).getDescripcion() + " --> " + mnu.getDescripcion());
					}
				}
			}
			lstPermisosTodos.setModel(new ListModelList(listaMenuDisponibles));
		}catch(Exception ex) {

		}
	}
	public List<TipoUsuario> getListaPerfiles(){
		return tipoUsuarioDAO.getTiposUsuariosActivos();
	}

}
