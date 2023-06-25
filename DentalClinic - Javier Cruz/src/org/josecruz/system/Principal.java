/*
    Nombre: José Javier Cruz de la Cruz
    Codigo Técnico: IN5AM
    Carné: 2018059
    Fecha de Creación: 5-04-2022
    Fecha de modificación: 10-05-2022
*/

package org.josecruz.system;

import java.io.IOException;
import java.io.InputStream;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.josecruz.controller.CitaController;
import org.josecruz.controller.DetalleRecetaController;
import org.josecruz.controller.DoctorController;
import org.josecruz.controller.EspecialidadController;
import org.josecruz.controller.LoginController;
import org.josecruz.controller.MedicamentoController;
import org.josecruz.controller.MenuPrincipalController;
import org.josecruz.controller.PacienteController;
import org.josecruz.controller.ProgramadorController;
import org.josecruz.controller.RecetaController;
import org.josecruz.controller.UsuarioController;

/**
 *
 * @author DELL
 */
public class Principal extends Application {
    private final String PAQUETE_VISTA = "/org/josecruz/view/";
    private Stage escenarioPrincipal;
    private Scene escena;
    boolean salir = false;
    
    
    @Override
    public void start(Stage escenarioPrincipal) throws Exception {
       this.escenarioPrincipal = escenarioPrincipal;
       this.escenarioPrincipal.setTitle("Dental Clinic");
       escenarioPrincipal.getIcons().add(new Image("/org/josecruz/image/Icono.png"));
//       Parent root = FXMLLoader.load(getClass().getResource("/org/josecruz/view/MenuPrincipalView.fxml"));
//       Scene escena = new Scene(root);
//       escenarioPrincipal.setScene(escena);
       ventanaLogin();
       escenarioPrincipal.show();
    }
    
    
    
    public void ventanaLogin(){
        try{
            LoginController login = (LoginController)cambiarEscena("LoginView.fxml",883 ,541);
            login.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaUsuario(){
        try{
            UsuarioController usuario = (UsuarioController)cambiarEscena("UsuarioView.fxml",575,541);
            usuario.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void menuPrincipal(){
        try{
            MenuPrincipalController ventanaMenu = (MenuPrincipalController)cambiarEscena("MenuPrincipalView.fxml", 907, 541);
            ventanaMenu.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaProgramador(){
        try{
            ProgramadorController ventanaProgramador = (ProgramadorController)cambiarEscena("ProgramadorView.fxml", 401,541);
            ventanaProgramador.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
          
    public void ventanaPacientes(){
        try{
            PacienteController ventanaPaciente = (PacienteController)cambiarEscena("PacientesView.fxml", 1165, 541);
            ventanaPaciente.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaMedicamentos(){
        try{
            MedicamentoController ventanaMedicamento = (MedicamentoController)cambiarEscena("MedicamentosView.fxml",789 ,541);
            ventanaMedicamento.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaEspecialidades(){
        try{
            EspecialidadController ventanaEspecialidad = (EspecialidadController)cambiarEscena("EspecialidadesView.fxml", 789, 541);
            ventanaEspecialidad.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
      

    public void ventanaDoctores(){
        try{
            DoctorController ventanaDoctor = (DoctorController)cambiarEscena("DoctoresView.fxml", 1001,541);
            ventanaDoctor.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaRecetas(){
        try{
            RecetaController ventanaReceta = (RecetaController)cambiarEscena("RecetasView.fxml", 838, 541 );
            ventanaReceta.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaDetalleRecetas(){
        try{
            DetalleRecetaController ventanaDetalleReceta = (DetalleRecetaController)cambiarEscena("DetalleRecetasView.fxml", 1111, 541 );
            ventanaDetalleReceta.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventaCitas(){
        try{
            CitaController ventanaCita = (CitaController)cambiarEscena("CitasView.fxml", 1272 ,541);
            ventanaCita.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
               
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    public Initializable cambiarEscena(String fxml, int ancho, int alto)throws Exception{
        Initializable resultado = null;
        FXMLLoader cargadorFXML = new FXMLLoader();
        InputStream archivo = Principal.class.getResourceAsStream(PAQUETE_VISTA+fxml);
        cargadorFXML.setBuilderFactory(new JavaFXBuilderFactory());
        cargadorFXML.setLocation(Principal.class.getResource(PAQUETE_VISTA+fxml));
        escena = new Scene((AnchorPane)cargadorFXML.load(archivo), ancho, alto);
        escenarioPrincipal.setScene(escena);
        escenarioPrincipal.sizeToScene();
        resultado = (Initializable)cargadorFXML.getController();
        return resultado;
    }
}
