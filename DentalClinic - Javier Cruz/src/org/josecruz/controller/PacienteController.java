package org.josecruz.controller;

import eu.schudt.javafx.controls.calendar.DatePicker;
import java.net.URL;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javax.swing.JOptionPane;
import org.josecruz.bean.Doctor;
import org.josecruz.bean.Paciente;
import org.josecruz.db.Conexion;
import org.josecruz.report.GenerarReporte;
import org.josecruz.system.Principal;


public class PacienteController implements Initializable {
    private Principal escenarioPrincipal;
    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Paciente> ListaPaciente;
    private DatePicker fNacimiento, fPV;
    @FXML private TextField txtCodigoPaciente;
    @FXML private TextField txtNombresPaciente;
    @FXML private TextField txtApellidosPaciente;
    @FXML private TextField txtSexo;
    @FXML private TextField txtDireccionPaciente;
    @FXML private TextField txtTelefonoPersonal;
    @FXML private GridPane grpFechas;
    @FXML private TableView tblPacientes;
    @FXML private TableColumn colCodigoPaciente;
    @FXML private TableColumn colNombresPaciente;
    @FXML private TableColumn colApellidosPaciente;
    @FXML private TableColumn colSexo;
    @FXML private TableColumn colFechaNacimiento;
    @FXML private TableColumn colDireccionPaciente;
    @FXML private TableColumn colTelefonoPersonal;
    @FXML private TableColumn colPrimerVisita;
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
        fNacimiento = new DatePicker(Locale.ENGLISH);
            fNacimiento.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            fNacimiento.getCalendarView().todayButtonTextProperty().set("Today");
            fNacimiento.getCalendarView().setShowWeeks(false);
            grpFechas.add(fNacimiento, 1, 4);
            fNacimiento.getStylesheets().add("/org/josecruz/resource/DatePicker.css");
            fPV = new DatePicker(Locale.ENGLISH);
            fPV.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            fPV.getCalendarView().todayButtonTextProperty().set("Today");
            fPV.getCalendarView().setShowWeeks(false);
            grpFechas.add(fPV, 1, 7);
            fPV.getStylesheets().add("/org/josecruz/resource/DatePicker.css");
    }
    
    public void cargarDatos(){
        tblPacientes.setItems(getPaciente());
        colCodigoPaciente.setCellValueFactory(new PropertyValueFactory<Paciente, Integer>("codigoPaciente"));
        colNombresPaciente.setCellValueFactory(new PropertyValueFactory<Paciente, String>("nombresPaciente"));
        colApellidosPaciente.setCellValueFactory(new PropertyValueFactory<Paciente, String>("apellidosPaciente"));
        colSexo.setCellValueFactory(new PropertyValueFactory<Paciente, String>("sexo"));
        colFechaNacimiento.setCellValueFactory(new PropertyValueFactory<Paciente, Date>("fechaNacimiento"));
        colDireccionPaciente.setCellValueFactory(new PropertyValueFactory<Paciente, String>("direccionPaciente"));
        colTelefonoPersonal.setCellValueFactory(new PropertyValueFactory<Paciente, String>("telefonoPersonal"));
        colPrimerVisita.setCellValueFactory(new PropertyValueFactory<Paciente, Date>("fechaPrimerVisita"));
    }
    
    
   
    public void seleccionarElemento(){
           if(tblPacientes.getSelectionModel().getSelectedItem()!= null){
            txtCodigoPaciente.setText(String.valueOf(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getCodigoPaciente()));
            txtNombresPaciente.setText(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getNombresPaciente());
            txtApellidosPaciente.setText(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getApellidosPaciente());
            txtSexo.setText(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getSexo());
            fNacimiento.selectedDateProperty().set(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getFechaNacimiento());
            txtDireccionPaciente.setText(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getDireccionPaciente());
            txtTelefonoPersonal.setText(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getTelefonoPersonal());
            fPV.selectedDateProperty().set(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getFechaPrimerVisita());}
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
    
    
    public void nuevo(){
        switch(tipoDeOperacion){
            case NINGUNO:
                limpiarControles();
                activarControles();
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
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
                if(tblPacientes.getSelectionModel().getSelectedItem()!= null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Esta seguro de eliminar el registro?", "Eliminar Paciente", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BorrarPaciente(?)}");
                            procedimiento.setInt(1, ((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getCodigoPaciente());
                            procedimiento.execute();
                            ListaPaciente.remove(tblPacientes.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }else if(respuesta == JOptionPane.NO_OPTION){
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
                if(tblPacientes.getSelectionModel().getSelectedItem() != null){
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    imgEditar.setImage(new Image("/org/josecruz/image/Editar.png"));
                    imgReporte.setImage(new Image("/org/josecruz/image/Cancelar.png"));
                    activarControles();
                    txtCodigoPaciente.setEditable(false);
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
            case NINGUNO:
                imprimirReporte();
                break;
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
    
    public void imprimirReporte(){
        Map parametros = new HashMap();
        parametros.put("codigoPaciente", null);
        parametros.put("Fondo",GenerarReporte.class.getResource("/org/josecruz/image/Fondo.png"));
        GenerarReporte.mostrarReporte("ReportePacientes.jasper", "Reporte de Pacientes", parametros);
    }
    
    
    public void actualizar(){
         try{
           PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EditarPaciente (?,?,?,?,?,?,?,?)}");
           Paciente registro = (Paciente)tblPacientes.getSelectionModel().getSelectedItem();
           registro.setNombresPaciente(txtNombresPaciente.getText());
           registro.setApellidosPaciente(txtApellidosPaciente.getText());
           registro.setSexo(txtSexo.getText());
           registro.setFechaNacimiento(fNacimiento.getSelectedDate());
           registro.setDireccionPaciente((txtDireccionPaciente.getText()));
           registro.setTelefonoPersonal(txtTelefonoPersonal.getText());
           registro.setFechaPrimerVisita(fPV.getSelectedDate());
           procedimiento.setInt(1, registro.getCodigoPaciente());
           procedimiento.setString(2, registro.getNombresPaciente());
           procedimiento.setString(3, registro.getApellidosPaciente());
           procedimiento.setString(4, registro.getSexo());
           procedimiento.setDate(5, new java.sql.Date(registro.getFechaNacimiento().getTime()));
           procedimiento.setString(6, registro.getDireccionPaciente());
           procedimiento.setString(7, registro.getTelefonoPersonal());
           procedimiento.setDate(8, new java.sql.Date(registro.getFechaPrimerVisita().getTime()));
           procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void guardar(){
        Paciente registro = new Paciente();
        registro.setCodigoPaciente(Integer.parseInt(txtCodigoPaciente.getText()));
        registro.setNombresPaciente(txtNombresPaciente.getText());
        registro.setApellidosPaciente(txtApellidosPaciente.getText());
        registro.setSexo(txtSexo.getText());
        registro.setFechaNacimiento(fNacimiento.getSelectedDate());
        registro.setDireccionPaciente(txtDireccionPaciente.getText());
        registro.setTelefonoPersonal(txtTelefonoPersonal.getText());
        registro.setFechaPrimerVisita(fPV.getSelectedDate());
        try{
           PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarPaciente (?,?,?,?,?,?,?,?)}");
           procedimiento.setInt(1, registro.getCodigoPaciente());
           procedimiento.setString(2, registro.getNombresPaciente());
           procedimiento.setString(3, registro.getApellidosPaciente());
           procedimiento.setString(4, registro.getSexo());
           procedimiento.setDate(5, new java.sql.Date(registro.getFechaNacimiento().getTime()));
           procedimiento.setString(6, registro.getDireccionPaciente());
           procedimiento.setString(7, registro.getTelefonoPersonal());
           procedimiento.setDate(8, new java.sql.Date(registro.getFechaPrimerVisita().getTime()));
           procedimiento.execute();
           ListaPaciente.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void desactivarControles(){
        txtCodigoPaciente.setEditable(false);
        txtNombresPaciente.setEditable(false);
        txtApellidosPaciente.setEditable(false);
        txtSexo.setEditable(false);
        txtDireccionPaciente.setEditable(false);
        txtTelefonoPersonal.setEditable(false);  
    }
    
    public void activarControles(){
        txtCodigoPaciente.setEditable(true);
        txtNombresPaciente.setEditable(true);
        txtApellidosPaciente.setEditable(true);
        txtSexo.setEditable(true);
        txtDireccionPaciente.setEditable(true);
        txtTelefonoPersonal.setEditable(true);  
    }
    
    public void limpiarControles(){
        txtCodigoPaciente.clear();
        txtNombresPaciente.clear();
        txtApellidosPaciente.clear();
        txtSexo.clear();
        fNacimiento.selectedDateProperty().setValue(null);
        txtDireccionPaciente.clear();
        txtTelefonoPersonal.clear();
        fPV.selectedDateProperty().setValue(null);
        tblPacientes.getSelectionModel().clearSelection();
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