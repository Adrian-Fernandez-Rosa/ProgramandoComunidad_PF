package com.egg.tpfinal.controlador;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.egg.tpfinal.entidades.Developer;
import com.egg.tpfinal.entidades.ONG;
import com.egg.tpfinal.entidades.Proyecto;
import com.egg.tpfinal.entidades.Usuario;
import com.egg.tpfinal.servicios.DeveloperService;
import com.egg.tpfinal.servicios.OngService;
import com.egg.tpfinal.servicios.ProyectoService;
import com.egg.tpfinal.servicios.UsuarioService;

@Controller
@PreAuthorize("isAuthenticated()")
@RequestMapping("/proyect")
public class ProyectoControlador {
	
	@Autowired
	private ProyectoService proyecServi;
	@Autowired
	private UsuarioService userServi;
	@Autowired
	private OngService OngServi;
	
	//@Autowired
	//private DeveloperService devServi;
	
	@GetMapping("/publicarproyecto")
	public String registrar() {
		return "publishproyectTest.html";
	}
	
	@PostMapping("/procesarform")
	public String crearProyecto(/*@RequestParam String email_usuario,*/ @RequestParam String cuerpo, @RequestParam String titulo,
			HttpSession session) {
		//el email es obtenido por la session del usuario logeado
		Usuario ongLogeada = (Usuario) session.getAttribute("usuariosession");
		String email_usuario = ongLogeada.getEmail();
		
		
		Usuario user = userServi.getUsuarioEmail(email_usuario);

		ONG ongaux = new ONG();
		//optimizar siguiente codigo en futuras versiones, y ver que funcion cumple aqui
		// aunque es una buena medida de seguridad, se prefiere hacer una sola consulta con email
		for (ONG ong : OngServi.listarONGactivas()) {
			if (ong.getUsuario().getId_usuario() == user.getId_usuario()) {
				ongaux = ong;
			} else { //redireccionar a sign up?
				
			}
		}
		//ONG ong = OngServi.buscarONGporidUsuario(user.getId_usuario()).get(); //arreglar en futuras versiones
		Date date = new Date();
		List<Developer> list = new ArrayList<Developer>(); //creo que no hace falta instanciar la lista
		proyecServi.crearProyecto(titulo, cuerpo, date, list, ongaux);
		return "redirect:/"; // falta vista
	}
	
	@GetMapping("/proyecto")
	public String mostrarproyectos(ModelMap mod) {
		List<Proyecto> lp = proyecServi.listarProyectosActivos();
		mod.addAttribute("listaProyecto", lp);
		return "listaproyectos"; // falta vista
	}
	
	@GetMapping("/eliminarproyecto/{id}")
	public String eliminarProyecto(@PathVariable Long id) {
		proyecServi.borrarProyecto(id);
		return "redirect:/"; // falta vista
	}
	
	/* EN SEGUNDA VERSION
	public String editarProyecto() {
		return "";
	}
	*/
	
	@GetMapping("/proyecto/{id}") // revisar
	public String devolverProyecto(ModelMap mod, @PathVariable Long id) {
		Proyecto proyecto = proyecServi.buscarPorID(id);
		mod.addAttribute("proyecto", proyecto);
		return ""; // falta vista
	}
	
}