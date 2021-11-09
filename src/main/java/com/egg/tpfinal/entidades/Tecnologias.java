package com.egg.tpfinal.entidades;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Entity
public class Tecnologias {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Long id_tecnologia;
	
	@NotEmpty(message = "El lenguaje no puede estar vacío")
	@NotNull(message = "Debes especificar un lenguaje")
	String lenguaje;
	
	public Long getId_tecnologia() {
		return id_tecnologia;
	}
	
	public void setId_tecnologia(Long id_tecnologia) {
		this.id_tecnologia = id_tecnologia;
	}
	
	public String getLenguaje() {
		return lenguaje;
	}
	
	public void setLenguaje(String lenguaje) {
		this.lenguaje = lenguaje;
	}
	
	@Override
	public String toString() {
		return "Tecnologias [id_tecnologia=" + id_tecnologia + ", lenguaje=" + lenguaje + "]";
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id_tecnologia, lenguaje);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tecnologias other = (Tecnologias) obj;
		return Objects.equals(id_tecnologia, other.id_tecnologia) && Objects.equals(lenguaje, other.lenguaje);
	}
	
}
