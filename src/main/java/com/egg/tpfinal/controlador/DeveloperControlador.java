package com.egg.tpfinal.controlador;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.egg.tpfinal.entidades.Developer;
import com.egg.tpfinal.entidades.Foto;
import com.egg.tpfinal.entidades.Tecnologias;
import com.egg.tpfinal.entidades.Usuario;
import com.egg.tpfinal.repositorios.FotoRepository;
import com.egg.tpfinal.servicios.DeveloperService;
import com.egg.tpfinal.servicios.FotoService;
import com.egg.tpfinal.servicios.TecnologiasService;
import com.egg.tpfinal.servicios.UsuarioService;
import enumeracion.Rol;

@Controller

@RequestMapping("/registrodev") // RequestMapping si esta debajo de Controller es la url padre
public class DeveloperControlador {
	@Autowired // Autowired instancia el objeto, esto evita hacer new Objeto()
	private TecnologiasService ServiTec;
	@Autowired
	private DeveloperService ServiDev;
	@Autowired
	private UsuarioService ServiUsu;
	@Autowired
	private FotoService ServiFoto;
	@Autowired
	private FotoRepository RepoFoto;

	@GetMapping() // GetMapping retorna siempre un html en este caso no lleva direccion porque es
					// /registrodev
	public String mostrardev(ModelMap mod) { // Por cada model/ModelMap se le envia informacion del back al front ej
												// datos de base de datos o errores
		List<Tecnologias> lt = ServiTec.listarTecnologias(); //
		mod.addAttribute("listaTec", lt); // Buscar Las tecnologias en la BASE DE DATOS
		return "registrodev.html";
	}

	/*
	 * @RequestParam es si envian desde un formulario con etiqueta name o si se usa
	 * Objetos con th (Lo que intentaron pame,fede y tami) El nombre en el parametro
	 * del controlador se tiene que llamar igual que el name en el html
	 * 
	 * @PathVariable Es si se le envia un valor desde el html como en el caso de un
	 * ID
	 * 
	 * Estas 2 etiquetas son fundamentales en los parametros de controladores
	 */

	@PostMapping("/cargardev")

	public String cargardev( @RequestParam(required=false, defaultValue = "") ArrayList<String> lenguajes , @RequestParam(required=false, defaultValue = "") String[] lenguajess
			,@RequestParam String user,@RequestParam String pass, @RequestParam String name,
			@RequestParam String apellido,@RequestParam String tel,@RequestParam(value="file", required=false) MultipartFile file, ModelMap modelo) throws Exception{
	
		
		//ineproyecto.getOng().getPublicaciones().forEach((e) -> System.out.println(e.getTitulo()));
	
		lenguajes.forEach((e) -> System.out.println(e)) ;

		if (lenguajes.isEmpty()) { 

			return "redirect:/registrodev";
		}
		try {
			Usuario u = ServiUsu.seteoUsuario(user, pass, Rol.DEVE); // Creo y guardo nuevo usuario
			Foto foto = null; // Es necesario porque ServiFoto retorna una foto

			if (file != null) {
				foto = ServiFoto.guardarfoto(file);// solo sube la foto al server(no persiste la url)
				// se hizo asi porque sino no se guarda relacionada al developer
			} /*else {
				foto=new Foto();
				foto.setUrl_foto("https://miro.medium.com/max/720/1*W35QUSvGpcLuxPo3SRTH4w.png");
				RepoFoto.save(foto);
				
			}*/
			
			ServiDev.crearDeveloper(u, name, apellido, tel, foto, lenguajes); // crea y guarda
			return "redirect:/login"; // Si todo funciono regresa al index
		} catch (Exception e) {
			modelo.put("nombre", name); // Esta linea y la de abajo esta para que vuelva a cargar las tecnologias sino
										// no lo hace
			modelo.put("apellido", apellido);
			modelo.put("tel", tel);
			modelo.put("file", file);
			modelo.put("user", user);
			// modelo.put("lenguajes", lenguajes);
			modelo.addAttribute("listaTec", lenguajes);
			modelo.put("error", e.getMessage()); // Mando error al html si existio, el 'error' debe coincidir en el html
			e.printStackTrace(); // Muestro error en consola
			return "registrodev.html";
		}

	}

	// Lista los Developer Activos (alta=true)
	@PreAuthorize("isAuthenticated()") // Es una etiqueta de Spring security no se puede acceder a la url si no se esta
										// Logueado
	@GetMapping("/listardev")

	public String listardev(Model mod,@RequestParam(required=false) String b) {
		if(b!=null) { //Si no llega nulo realiza la busqueda por el string en cambio si llega nulo muestra activos
		
			mod.addAttribute("listaDev", ServiDev.listarBusquedaDeveloperActivos(b));
		}else {
			//System.out.println("Entre Normal "+ b);

			mod.addAttribute("listaDev", ServiDev.listarDeveloperActivos());
		}

		return "listadevelop";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/eliminardev/{id_developer}")
	public String eliminardev(@PathVariable Long id_developer) {
	
		ServiDev.borrarDeveloper(id_developer);
		return "redirect:/listarTodo";
	}

	@PreAuthorize("isAuthenticated() && hasAnyRole('ROLE_ADMIN') || hasAnyRole('ROLE_DEVE')")
	@GetMapping("/editar/{id}")
	public String edi(@PathVariable Long id, ModelMap mod) {
		Developer dev = ServiDev.getDeveloper(id);
		mod.addAttribute(dev); // Esto esta para mostrar el valor del objeto en editar :)
		return "editardev";
	}

	@PreAuthorize("isAuthenticated() && hasAnyRole('ROLE_ADMIN')")
	@PostMapping("/editar/{id}")
	public String editar(@PathVariable Long id, @RequestParam String name, @RequestParam String apellido,
			@RequestParam String tel, ModelMap mod, HttpSession session,
			@RequestParam(value = "file", required = false) MultipartFile file) {
		// El post admin solo cambia nombre apellido y telefono
		try {
			// Permite editar perfil desde el rol de developer, verificando la sesión actual
			Foto foto = null;
			Developer d = ServiDev.getDeveloper(id);
			Usuario devLogeado = (Usuario) session.getAttribute("usuariosession");
			if (devLogeado.getRol().equals(Rol.DEVE) && (d.getUsuario().getEmail().equals(devLogeado.getEmail()))) { // editar developer desde perfil
				if (file != null) {
					foto = ServiFoto.guardarfoto(file);
					ServiDev.editarDeveloper(id, d.getUsuario(), name, apellido, tel, foto, d.getTecnologias());
				} else {
					ServiDev.editarDeveloper(id, d.getUsuario(), name, apellido, tel, d.getFoto(), d.getTecnologias());
				}
				return "redirect:/principal";
			} else {
				// Permite editar perfil desde el rol ADMIN
				Foto foto2 = null;
				Developer d2 = ServiDev.getDeveloper(id);
				if (devLogeado.getRol().equals(Rol.ADMIN)) {
					if (file != null) {
						foto2 = ServiFoto.guardarfoto(file);
						ServiDev.editarDeveloper(id, d2.getUsuario(), name, apellido, tel, foto2, d2.getTecnologias());
					} else {
						ServiDev.editarDeveloper(id, d2.getUsuario(), name, apellido, tel, d2.getFoto(), d2.getTecnologias());
					}
					return "redirect:/listarTodo";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			mod.put("error", e.getMessage());
			return "redirect:/principal";
		}
		return null; // Nunca debería llegar a este return

	}
}
