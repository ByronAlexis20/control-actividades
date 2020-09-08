package com.actividades.modelo;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the permiso database table.
 * 
 */
@Entity
@Table(name="permiso")
@NamedQueries({
	@NamedQuery(name="Permiso.findAll", query="SELECT s FROM Permiso s"),
	@NamedQuery(name="Permiso.buscarPadrePorPerfil", query="SELECT s FROM Permiso s "
			+ "where s.tipoUsuario.idTipoUsuario = :idperfil and s.estado = 'A' and s.menu.idMenuPadre = 0"),
	@NamedQuery(name="Permiso.buscarTodosPorPerfil", query="SELECT s FROM Permiso s where s.tipoUsuario.idTipoUsuario = :idperfil "
			+ "and s.estado = 'A' and s.menu.idMenuPadre <> 0"),
})
public class Permiso implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_permiso")
	private Integer idPermiso;

	private String estado;

	//bi-directional many-to-one association to Menu
	@ManyToOne
	@JoinColumn(name="id_menu")
	private Menu menu;

	//bi-directional many-to-one association to TipoUsuario
	@ManyToOne
	@JoinColumn(name="id_tipo_usuario")
	private TipoUsuario tipoUsuario;

	public Permiso() {
	}

	public Integer getIdPermiso() {
		return this.idPermiso;
	}

	public void setIdPermiso(Integer idPermiso) {
		this.idPermiso = idPermiso;
	}

	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Menu getMenu() {
		return this.menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}

	public TipoUsuario getTipoUsuario() {
		return this.tipoUsuario;
	}

	public void setTipoUsuario(TipoUsuario tipoUsuario) {
		this.tipoUsuario = tipoUsuario;
	}

}