package com.egg.tpfinal.entidades;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import enumeracion.Rol;

@Entity
public class Usuario {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id_usuario;
	
	@Email
	@NotEmpty(message = "El email no puede estar vacío")
	@NotNull(message = "Debes ingresar un email")
	private	String email;
	
	@NotNull(message = "Debes ingresar una contraseña")
	@NotEmpty(message = "La contraseña no puede estar vacía")
	@Size(min = 6, max = 20, message = "La contraseña debe tener entre 6 y 20 caracteres")
	private String	contrasena;
	private Boolean alta;

	@Enumerated (EnumType.STRING)
	private Rol	rol;
	
	public Long getId_usuario() {
		return id_usuario;
	}
	
	public void setId_usuario(Long id_usuario) {
		this.id_usuario = id_usuario;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getContrasena() {
		return contrasena;
	}
	
	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}
	
	public Boolean getAlta() {
		return alta;
	}
	
	public void setAlta(Boolean alta) {
		this.alta = alta;
	}
	
	public Rol getRol() {
		return rol;
	}
	
	public void setRol(Rol rol) {
		this.rol = rol;
	}
	
	@Override
	public String toString() {
		return "Usuario [id_usuario=" + id_usuario + ", email=" + email + ", contrasena=" + contrasena + ", alta="
				+ alta  + ", rol=" + rol + "]";
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(alta, contrasena, email, id_usuario, rol);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Usuario other = (Usuario) obj;
		return Objects.equals(alta, other.alta) && Objects.equals(contrasena, other.contrasena)
				&& Objects.equals(email, other.email) && Objects.equals(id_usuario, other.id_usuario)
				&& rol == other.rol;
	}
	
}
