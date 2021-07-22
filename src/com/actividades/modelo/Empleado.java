package com.actividades.modelo;

import java.io.Serializable;
import javax.persistence.*;

import java.util.Date;
import java.util.List;


/**
 * The persistent class for the empleado database table.
 * 
 */
@Entity
@Table(name="empleado")
@NamedQueries({
	@NamedQuery(name="Empleado.findAll", query="SELECT e FROM Empleado e"),
	@NamedQuery(name="Empleado.buscaUsuario", 
	query="SELECT e FROM Empleado e WHERE e.usuario = :nombreUsuario and e.estado = 'A'"),
	@NamedQuery(name="Empleado.buscarPorPatron", query="SELECT e FROM Empleado e where (lower(e.persona.nombre) like(:patron) or "
			+ "lower(e.persona.apellido) like(:patron)) and e.estado = 'A' order by e.idEmpleado"),
	@NamedQuery(name="Empleado.buscarPorUsuario", query="SELECT e FROM Empleado e where e.usuario = :patron and e.idEmpleado <> :idUsuario and e.estado = 'A'"),
	@NamedQuery(name="Empleado.buscarEmpleado", query="SELECT e FROM Empleado e "
			+ "where ((lower(e.persona.nombre) like (:patron)) or (lower(e.persona.apellido) like (:patron))) and e.estado = 'A' order by e.idEmpleado"),
	@NamedQuery(name="Empleado.buscarPorCedula", query="SELECT e FROM Empleado e where e.persona.cedula = :cedula and e.estado = 'A'"),
	@NamedQuery(name="Empleado.buscarPorCedulaDiferenteAlUsuarioActual", query="SELECT e FROM Empleado e where e.persona.cedula = :cedula and e.estado = 'A' "
			+ "and e.idEmpleado <> :idEmpleado"),
	@NamedQuery(name="Empleado.buscarPorUsuarioNuevo", query="SELECT e FROM Empleado e where e.usuario = :usuario and e.estado = 'A'"),
	@NamedQuery(name="Empleado.buscarPorUsuarioRegistrado", query="SELECT e FROM Empleado e where e.persona.cedula <> :cedula and e.usuario = :usuario and e.estado = 'A'"),
	@NamedQuery(name="Empleado.buscarPorDepartamento", query="SELECT e FROM Empleado e where "
			+ "e.departamento.idDepartamento = :idDepartamento and e.tipoUsuario.idTipoUsuario = :idTipoUsuario and e.estado = 'A'"),
	@NamedQuery(name="Empleado.buscarJefeDepartamento", query="SELECT e FROM Empleado e where e.departamento.idDepartamento = :idDepartamento "
			+ "and e.estado = 'A' and e.tipoUsuario.idTipoUsuario = :idJefeArea"),
	@NamedQuery(name="Empleado.buscarPorDepartamentoLogueado", query="SELECT e FROM Empleado e where e.estado = 'A' and e.departamento.idDepartamento = :idDepartamento and e.tipoUsuario.idTipoUsuario = 4"),
	@NamedQuery(name="Empleado.buscarPorTipoUsuarioDepartamento", query="SELECT e FROM Empleado e where e.tipoUsuario.idTipoUsuario = :idTipoUsuario and e.estado = 'A' "
			+ "and e.departamento.idDepartamento = :idDepartamento"),
	@NamedQuery(name="Empleado.buscarPorTipoUsuario", query="SELECT e FROM Empleado e where e.tipoUsuario.idTipoUsuario = :idTipoUsuario and e.estado = 'A' "),
})
public class Empleado implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_empleado")
	private Integer idEmpleado;

	private String clave;

	private String estado;
	
	private String permiso;

	@ManyToOne
	@JoinColumn(name="id_departamento")
	private Departamento departamento;

	@Column(name="primera_vez")
	private String primeraVez;

	private String usuario;
	
	private String puesto;
	
	private String foto;

	@Column(name="clave_normal")
	private String claveNormal;
	
	@Temporal(TemporalType.DATE)
	@Column(name="fecha_ingreso")
	private Date fechaIngreso;
	
	//bi-directional many-to-one association to Agenda
	@OneToMany(mappedBy="empleado")
	private List<Agenda> agendas;
	
	@OneToMany(mappedBy="empleado")
	private List<Queja> quejas;

	//bi-directional many-to-one association to DepartamentoAsignado
	@OneToMany(mappedBy="empleado")
	private List<DepartamentoAsignado> departamentoAsignados;

	//bi-directional many-to-one association to Cargo
	@ManyToOne
	@JoinColumn(name="id_cargo")
	private Cargo cargo;

	//bi-directional many-to-one association to Persona
	@ManyToOne
	@JoinColumn(name="id_persona")
	private Persona persona;

	//bi-directional many-to-one association to TipoUsuario
	@ManyToOne
	@JoinColumn(name="id_tipo_usuario")
	private TipoUsuario tipoUsuario;

	//bi-directional many-to-one association to Publicacion
	@OneToMany(mappedBy="empleado")
	private List<Publicacion> publicacions;

	public Empleado() {
	}

	public Integer getIdEmpleado() {
		return this.idEmpleado;
	}

	public void setIdEmpleado(Integer idEmpleado) {
		this.idEmpleado = idEmpleado;
	}

	public String getClave() {
		return this.clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Departamento getDepartamento() {
		return departamento;
	}

	public void setDepartamento(Departamento departamento) {
		this.departamento = departamento;
	}

	public String getPrimeraVez() {
		return this.primeraVez;
	}

	public void setPrimeraVez(String primeraVez) {
		this.primeraVez = primeraVez;
	}

	public String getUsuario() {
		return this.usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public List<Agenda> getAgendas() {
		return this.agendas;
	}

	public void setAgendas(List<Agenda> agendas) {
		this.agendas = agendas;
	}

	public Agenda addAgenda(Agenda agenda) {
		getAgendas().add(agenda);
		agenda.setEmpleado(this);

		return agenda;
	}

	public Agenda removeAgenda(Agenda agenda) {
		getAgendas().remove(agenda);
		agenda.setEmpleado(null);

		return agenda;
	}

	public List<Queja> getQuejas() {
		return quejas;
	}

	public void setQuejas(List<Queja> quejas) {
		this.quejas = quejas;
	}
	
	public Queja addQueja(Queja queja) {
		getQuejas().add(queja);
		queja.setEmpleado(this);
		
		return queja;
	}
	
	public Queja removeQueja(Queja queja) {
		getQuejas().remove(queja);
		queja.setEmpleado(null);
		return queja;
	}

	public List<DepartamentoAsignado> getDepartamentoAsignados() {
		return this.departamentoAsignados;
	}

	public void setDepartamentoAsignados(List<DepartamentoAsignado> departamentoAsignados) {
		this.departamentoAsignados = departamentoAsignados;
	}

	public DepartamentoAsignado addDepartamentoAsignado(DepartamentoAsignado departamentoAsignado) {
		getDepartamentoAsignados().add(departamentoAsignado);
		departamentoAsignado.setEmpleado(this);

		return departamentoAsignado;
	}

	public DepartamentoAsignado removeDepartamentoAsignado(DepartamentoAsignado departamentoAsignado) {
		getDepartamentoAsignados().remove(departamentoAsignado);
		departamentoAsignado.setEmpleado(null);

		return departamentoAsignado;
	}

	public Cargo getCargo() {
		return this.cargo;
	}

	public void setCargo(Cargo cargo) {
		this.cargo = cargo;
	}

	public Persona getPersona() {
		return this.persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public TipoUsuario getTipoUsuario() {
		return this.tipoUsuario;
	}

	public void setTipoUsuario(TipoUsuario tipoUsuario) {
		this.tipoUsuario = tipoUsuario;
	}

	public List<Publicacion> getPublicacions() {
		return this.publicacions;
	}

	public void setPublicacions(List<Publicacion> publicacions) {
		this.publicacions = publicacions;
	}

	public Publicacion addPublicacion(Publicacion publicacion) {
		getPublicacions().add(publicacion);
		publicacion.setEmpleado(this);

		return publicacion;
	}

	public Publicacion removePublicacion(Publicacion publicacion) {
		getPublicacions().remove(publicacion);
		publicacion.setEmpleado(null);

		return publicacion;
	}

	public String getPuesto() {
		return puesto;
	}

	public void setPuesto(String puesto) {
		this.puesto = puesto;
	}

	public String getPermiso() {
		return permiso;
	}

	public void setPermiso(String permiso) {
		this.permiso = permiso;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	public String getClaveNormal() {
		return claveNormal;
	}

	public void setClaveNormal(String claveNormal) {
		this.claveNormal = claveNormal;
	}

	public Date getFechaIngreso() {
		return fechaIngreso;
	}

	public void setFechaIngreso(Date fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
	}

}