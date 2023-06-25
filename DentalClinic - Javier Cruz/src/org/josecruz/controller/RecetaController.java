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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javax.swing.JOptionPane;
import org.josecruz.bean.Doctor;
import org.josecruz.bean.Receta;
import org.josecruz.db.Conexion;
import org.josecruz.report.GenerarReporte;
import org.josecruz.system.Principal;

public class RecetaController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Receta> ListaReceta;
    private ObservableList<Doctor> ListaDoctor;
    private DatePicker fReceta;
    @FXML private TextField txtCodigoReceta;
    @FXML private ComboBox cmbNumeroColegiado;
    @FXML private GridPane grpFechas;
    @FXML private TableView tblRecetas;
    @FXML private TableColumn colCodigoReceta;
    @FXML private TableColumn colFechaReceta;
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
        cmbNumeroColegiado.setItems(getDoctor());
        cmbNumeroColegiado.setDisable(true);
        fReceta = new DatePicker(Locale.ENGLISH);
            fReceta.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            fReceta.getCalendarView().todayButtonTextProperty().set("Today");
            fReceta.getCalendarView().setShowWeeks(false);
            grpFechas.add(fReceta, 1, 1);
            fReceta.getStylesheets().add("/org/josecruz/resource/DatePicker.css");
        fReceta.setDisable(true);
        
    }
    
    public void cargarDatos(){
        tblRecetas.setItems(getReceta());
        colCodigoReceta.setCellValueFactory(new PropertyValueFactory<Receta, Integer>("codigoReceta"));
        colFechaReceta.setCellValueFactory(new PropertyValueFactory<Receta, Date>("fechaReceta"));
        colNumeroColegiado.setCellValueFactory(new PropertyValueFactory<Receta, Integer>("numeroColegiado"));
    }
    
    public void seleccionarElemento(){
        if(tblRecetas.getSelectionModel().getSelectedItem() != null){
            txtCodigoReceta.setText(String.valueOf(((Receta)tblRecetas.getSelectionModel().getSelectedItem()).getCodigoReceta()));
            fReceta.selectedDateProperty().set(((Receta)tblRecetas.getSelectionModel().getSelectedItem()).getFechaReceta());
            cmbNumeroColegiado.getSelectionModel().select(buscarDoctor(((Receta)tblRecetas.getSelectionModel().getSelectedItem()).getNumeroColegiado()));
        }
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
   
    public ObservableList <Receta> getReceta(){
        ArrayList<Receta> lista = new ArrayList<Receta>();
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarRecetas}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Receta(resultado.getInt("codigoReceta"),
                                     resultado.getDate("fechaReceta"),
                                     resultado.getInt("numeroColegiado")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ListaReceta = FXCollections.observableArrayList(lista);
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
    
    public void Eliminar(){
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
                if(tblRecetas.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Esta seguro de eliminar el registro?", "Eliminar Receta", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BorrarReceta(?)}");
                            procedimiento.setInt(1, ((Receta)tblRecetas.getSelectionModel().getSelectedItem()).getCodigoReceta());
                            procedimiento.execute();
                            ListaReceta.remove(tblRecetas.getSelectionModel().getSelectedIndex());
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
                if(tblRecetas.getSelectionModel().getSelectedItem() != null){
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    imgEditar.setImage(new Image("/org/josecruz/image/Editar.png"));
                    imgReporte.setImage(new Image("/org/josecruz/image/Cancelar.png"));
                    activarControles();
                    txtCodigoReceta.setEditable(false);
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
            case NINGUNO:
                if(tblRecetas.getSelectionModel().getSelectedItem() != null)
                imprimirReporte();
                else{JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento");}
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
        int codReceta = ((Receta)tblRecetas.getSelectionModel().getSelectedItem()).getCodigoReceta();
        parametros.put("codReceta", codReceta);
        parametros.put("Fondo",GenerarReporte.class.getResource("/org/josecruz/image/Reporte Recetas.png"));
        GenerarReporte.mostrarReporte("ReporteReceta.jasper", "Reporte de Recetas", parametros);
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EditarReceta(?, ?, ?)}");
            Receta registro = (Receta)tblRecetas.getSelectionModel().getSelectedItem();
            registro.setFechaReceta(fReceta.getSelectedDate());
            registro.setNumeroColegiado(((Doctor)cmbNumeroColegiado.getSelectionModel().getSelectedItem()).getNumeroColegiado());
            procedimiento.setInt(1, registro.getCodigoReceta());
            procedimiento.setDate(2, new java.sql.Date(registro.getFechaReceta().getTime()));
            procedimiento.setInt(3, registro.getNumeroColegiado());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
    public void guardar(){
        Receta registro = new Receta();
        registro.setFechaReceta(fReceta.getSelectedDate());
        registro.setNumeroColegiado(((Doctor)cmbNumeroColegiado.getSelectionModel().getSelectedItem()).getNumeroColegiado());
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarReceta(?, ?)}");
            procedimiento.setDate(1, new java.sql.Date(registro.getFechaReceta().getTime()));
            procedimiento.setInt(2, registro.getNumeroColegiado());
            procedimiento.execute();
            ListaReceta.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void desactivarControles(){
        txtCodigoReceta.setEditable(false);
        fReceta.setDisable(true);
        cmbNumeroColegiado.setDisable(true);
    }
    
    public void activarControles(){
        fReceta.setDisable(false);
        cmbNumeroColegiado.setDisable(false);
    }
    
    public void limpiarControles(){
        txtCodigoReceta.clear();
        cmbNumeroColegiado.setValue(null);
        fReceta.selectedDateProperty().setValue(null);
        tblRecetas.getSelectionModel().clearSelection();
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
