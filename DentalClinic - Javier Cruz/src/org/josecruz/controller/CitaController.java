
package org.josecruz.controller;

import com.jfoenix.controls.JFXTimePicker;
import eu.schudt.javafx.controls.calendar.DatePicker;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javax.swing.JOptionPane;
import org.josecruz.bean.Cita;
import org.josecruz.bean.Doctor;
import org.josecruz.bean.Paciente;
import org.josecruz.db.Conexion;
import org.josecruz.system.Principal;


public class CitaController implements Initializable {
    private Principal escenarioPrincipal;
    private ObservableList<Cita> ListaCita;
    private ObservableList<Doctor> ListaDoctor;
    private ObservableList<Paciente> ListaPaciente;
    private DatePicker fCitas;
    private JFXTimePicker HCita;
    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    @FXML private TextField txtCodigoCita;
    @FXML private TextField txtTratamiento;
    @FXML private TextField txtDescripcion;
    @FXML private GridPane grpCitas;
    @FXML private ComboBox cmbCodigoPaciente; 
    @FXML private ComboBox cmbNumeroColegiado;
    @FXML private TableView tblCitas;
    @FXML private TableColumn colCodigoCita;
    @FXML private TableColumn colFechaCita;
    @FXML private TableColumn colHoraCita;
    @FXML private TableColumn colTratamiento;
    @FXML private TableColumn colCondicionActual;
    @FXML private TableColumn colCodigoPaciente;
    @FXML private TableColumn colNumeroColegiado;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    @FXML private ImageView imgNuevo;
    @FXML private ImageView imgEliminar;
    @FXML private ImageView imgEditar;
    @FXML private ImageView imgReporte;
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        cmbCodigoPaciente.setItems(getPaciente());
        cmbNumeroColegiado.setItems(getDoctor());
        cmbCodigoPaciente.setDisable(true);
        cmbNumeroColegiado.setDisable(true);
        fCitas = new DatePicker(Locale.ENGLISH);
            fCitas.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            fCitas.getCalendarView().todayButtonTextProperty().set("Today");
            fCitas.getCalendarView().setShowWeeks(false);
            grpCitas.add(fCitas, 1, 1);
            fCitas.getStylesheets().add("/org/josecruz/resource/DatePicker.css");
            fCitas.setDisable(true);
        HCita = new JFXTimePicker();
        grpCitas.add(HCita, 1, 2);
        HCita.setDisable(true);
    }
    
    
    public void cargarDatos(){
        tblCitas.setItems(getCita());
        colCodigoCita.setCellValueFactory(new PropertyValueFactory<Cita, Integer>("codigoCita"));
        colFechaCita.setCellValueFactory(new PropertyValueFactory<Cita, Date>("fechaCita"));
        colHoraCita.setCellValueFactory(new PropertyValueFactory<Cita, Time>("horaCita"));
        colTratamiento.setCellValueFactory(new PropertyValueFactory<Cita, String>("tratamiento"));
        colCondicionActual.setCellValueFactory(new PropertyValueFactory<Cita, String>("descripCondActual"));
        colCodigoPaciente.setCellValueFactory(new PropertyValueFactory<Cita, Integer>("codigoPaciente"));
        colNumeroColegiado.setCellValueFactory(new PropertyValueFactory<Cita, Integer>("numeroColegiado"));
    }
    
    public void seleccionarElemento(){
        if(tblCitas.getSelectionModel().getSelectedItem() != null){
            txtCodigoCita.setText(String.valueOf(((Cita)tblCitas.getSelectionModel().getSelectedItem()).getCodigoCita()));
            fCitas.selectedDateProperty().set(((Cita)tblCitas.getSelectionModel().getSelectedItem()).getFechaCita());
            HCita.setValue(((Cita)tblCitas.getSelectionModel().getSelectedItem()).getHoraCita().toLocalTime());
            txtTratamiento.setText(((Cita)tblCitas.getSelectionModel().getSelectedItem()).getTratamiento());
            txtDescripcion.setText(((Cita)tblCitas.getSelectionModel().getSelectedItem()).getDescripCondActual());
            cmbCodigoPaciente.getSelectionModel().select((buscarPaciente(((Cita)tblCitas.getSelectionModel().getSelectedItem()).getCodigoPaciente())));
            cmbNumeroColegiado.getSelectionModel().select(buscarDoctor(((Cita)tblCitas.getSelectionModel().getSelectedItem()).getNumeroColegiado()));
        }
    }
    
    public ObservableList<Cita> getCita(){
        ArrayList<Cita> lista = new ArrayList<Cita>();
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarCitas()}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Cita(resultado.getInt("codigoCita"),
                                   resultado.getDate("fechaCita"),
                                   resultado.getTime("horaCita"),
                                   resultado.getString("tratamiento"),
                                   resultado.getString("descripCondActual"),
                                   resultado.getInt("codigoPaciente"),
                                   resultado.getInt("numeroColegiado")
                ));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return ListaCita = FXCollections.observableArrayList(lista);
    }
    
    public ObservableList<Doctor> getDoctor(){
        ArrayList<Doctor> lista = new ArrayList<Doctor>();
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarDoctores()}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Doctor(resultado.getInt("numeroColegiado"),
                                    resultado.getString("nombresDoctor"),
                                    resultado.getString("apellidosDoctor"),
                                    resultado.getString("telefonoContacto"),
                                    resultado.getInt("codigoEspecialidad")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return ListaDoctor = FXCollections.observableArrayList(lista);
    }
    
    
    public Doctor buscarDoctor(int numeroColegiado){
        Doctor resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarDoctor(?)}");
            procedimiento.setInt(1, numeroColegiado);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new Doctor(registro.getInt("numeroColegiado"),
                                       registro.getString("nombresDoctor"),
                                       registro.getString("apellidosDoctor"),
                                       registro.getString("telefonoContacto"),
                                       registro.getInt("codigoEspecialidad"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return resultado;
    }
    
    
    public ObservableList<Paciente> getPaciente(){
        ArrayList<Paciente> lista = new ArrayList<Paciente>();
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarPacientes}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Paciente(resultado.getInt("codigoPaciente"),
                                    resultado.getString("nombresPaciente"),
                                    resultado.getString("apellidosPaciente"),
                                    resultado.getString("sexo"),
                                    resultado.getDate("fechaNacimiento"),
                                    resultado.getString("direccionPaciente"),
                                    resultado.getString("telefonoPersonal"),
                                    resultado.getDate("fechaPrimerVisita")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return ListaPaciente = FXCollections.observableArrayList(lista);         
    }
    
    public Paciente buscarPaciente(int codigoPaciente){
        Paciente resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarPaciente(?)}");
            procedimiento.setInt(1, codigoPaciente);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new Paciente(registro.getInt("codigoPaciente"),
                                        registro.getString("nombresPaciente"),
                                        registro.getString("apellidosPaciente"),
                                        registro.getString("sexo"),
                                        registro.getDate("fechaNacimiento"),
                                        registro.getString("direccionPaciente"),
                                        registro.getString("telefonoPersonal"),
                                        registro.getDate("fechaPrimerVisita")
                );
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return resultado;
    }
    
    public void nuevo(){
        switch(tipoDeOperacion){
            case NINGUNO:
                activarControles();
                limpiarControles();
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                txtCodigoCita.setEditable(false);
                imgNuevo.setImage(new Image("/org/josecruz/image/Guardar.png"));
                imgEliminar.setImage(new Image("/org/josecruz/image/Cancelar.png"));
                tipoDeOperacion = operaciones.GUARDAR;
                break;
            case GUARDAR:
                guardar();
                desactivarControles();
                limpiarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                imgNuevo.setImage(new Image("/org/josecruz/image/Nuevo.png"));
                imgEliminar.setImage(new Image("/org/josecruz/image/Borrar.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                cargarDatos();
                break;
        }
    }
    
    public void eliminar(){
        switch(tipoDeOperacion){
            case GUARDAR:
                desactivarControles();
                limpiarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                imgNuevo.setImage(new Image("/org/josecruz/image/Nuevo.png"));
                imgEliminar.setImage(new Image("/org/josecruz/image/Borrar.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                break;
            default:
                if(tblCitas.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Esta seguro de eliminar el registro?", "Eliminar Cita", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BorrarCita(?)}");
                            procedimiento.setInt(1, ((Cita)tblCitas.getSelectionModel().getSelectedItem()).getCodigoCita());
                            procedimiento.execute();
                            ListaCita.remove(tblCitas.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    } else if(respuesta == JOptionPane.NO_OPTION){
                        limpiarControles();
                    }
                    
                }else {
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento");
                }
                
        }
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblCitas.getSelectionModel().getSelectedItem() != null){
                   btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    imgEditar.setImage(new Image("/org/josecruz/image/Editar.png"));
                    imgReporte.setImage(new Image("/org/josecruz/image/Cancelar.png"));
                    activarControles();
                    txtCodigoCita.setEditable(false);
                    cmbCodigoPaciente.setDisable(true);
                    cmbNumeroColegiado.setDisable(true);
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                }else
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento");
                break;
            case ACTUALIZAR:
                actualizar();
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                imgEditar.setImage(new Image("/org/josecruz/image/Editar.png"));
                imgReporte.setImage(new Image("/org/josecruz/image/Reporte.png"));
                desactivarControles();
                limpiarControles();
                cargarDatos();
                tipoDeOperacion = operaciones.NINGUNO;
                break;
                    
            }
        }
    
    public void reporte(){
        switch(tipoDeOperacion){
            case ACTUALIZAR:
                desactivarControles();
                limpiarControles();
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                imgEditar.setImage(new Image("/org/josecruz/image/Editar.png"));
                imgReporte.setImage(new Image("/org/josecruz/image/Reporte.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                break;        
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EditarCita(?, ?, ?, ?, ?, ?, ?)}");
            Cita registro = (Cita)tblCitas.getSelectionModel().getSelectedItem();
            java.sql.Time time =  java.sql.Time.valueOf(HCita.getValue());
            registro.setFechaCita(fCitas.getSelectedDate());
            registro.setHoraCita(time);
            registro.setTratamiento(txtTratamiento.getText());
            registro.setDescripCondActual(txtDescripcion.getText());
            registro.setCodigoPaciente(((Paciente)cmbCodigoPaciente.getSelectionModel().getSelectedItem()).getCodigoPaciente());
            registro.setNumeroColegiado(((Doctor)cmbNumeroColegiado.getSelectionModel().getSelectedItem()).getNumeroColegiado());
            procedimiento.setInt(1, registro.getCodigoCita());
            procedimiento.setDate(2, new java.sql.Date(registro.getFechaCita().getTime()));
            procedimiento.setTime(3, registro.getHoraCita());
            procedimiento.setString(4, registro.getTratamiento());
            procedimiento.setString(5, registro.getDescripCondActual());
            procedimiento.setInt(6, registro.getCodigoPaciente());
            procedimiento.setInt(7, registro.getNumeroColegiado());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void guardar(){
        Cita registro = new Cita();
        java.sql.Time time =  java.sql.Time.valueOf(HCita.getValue());
        registro.setFechaCita(fCitas.getSelectedDate());
        registro.setHoraCita(time);
        registro.setTratamiento(txtTratamiento.getText());
        registro.setDescripCondActual(txtDescripcion.getText());
        registro.setCodigoPaciente(((Paciente)cmbCodigoPaciente.getSelectionModel().getSelectedItem()).getCodigoPaciente());
        registro.setNumeroColegiado(((Doctor)cmbNumeroColegiado.getSelectionModel().getSelectedItem()).getNumeroColegiado());
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarCita(?, ?, ?, ?, ?, ?)}");
            procedimiento.setDate(1, new java.sql.Date(registro.getFechaCita().getTime()));
            procedimiento.setTime(2, registro.getHoraCita());
            procedimiento.setString(3, registro.getTratamiento());
            procedimiento.setString(4, registro.getDescripCondActual());
            procedimiento.setInt(5, registro.getCodigoPaciente());
            procedimiento.setInt(6, registro.getNumeroColegiado());
            procedimiento.execute();
            ListaCita.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void desactivarControles(){
        txtCodigoCita.setEditable(false);
        txtTratamiento.setEditable(false);
        txtDescripcion.setEditable(false);
        cmbCodigoPaciente.setDisable(true);
        cmbNumeroColegiado.setDisable(true);
        fCitas.setDisable(true);
        HCita.setDisable(true);
    }
    
    public void activarControles(){
        txtCodigoCita.setEditable(true);
        txtTratamiento.setEditable(true);
        txtDescripcion.setEditable(true);
        cmbCodigoPaciente.setDisable(false);
        cmbNumeroColegiado.setDisable(false);
        fCitas.setDisable(false);
        HCita.setDisable(false);
    }
    
    public void limpiarControles(){
        txtCodigoCita.clear();
        txtTratamiento.clear();
        txtDescripcion.clear();
        cmbCodigoPaciente.setValue(null);
        cmbNumeroColegiado.setValue(null);
        tblCitas.getSelectionModel().clearSelection();
        fCitas.selectedDateProperty().setValue(null);
        HCita.setValue(null);
    }
    
    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void menuPrincipal (){
        escenarioPrincipal.menuPrincipal();
    }
    
}

