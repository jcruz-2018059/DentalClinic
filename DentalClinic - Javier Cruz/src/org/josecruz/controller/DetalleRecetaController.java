package org.josecruz.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
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
import javax.swing.JOptionPane;
import org.josecruz.bean.DetalleReceta;
import org.josecruz.bean.Medicamento;
import org.josecruz.bean.Receta;
import org.josecruz.db.Conexion;
import org.josecruz.report.GenerarReporte;
import org.josecruz.system.Principal;


public class DetalleRecetaController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<DetalleReceta> ListaDetalleReceta;
    private ObservableList<Receta> ListaReceta;
    private ObservableList<Medicamento> ListaMedicamento;
    @FXML private TextField txtDetalleReceta;
    @FXML private TextField txtDosis;
    @FXML private ComboBox cmbCodigoReceta;
    @FXML private ComboBox cmbCodigoMedicamento;
    @FXML private TableView tblDetalleRecetas;
    @FXML private TableColumn colCodigoDetalleReceta;
    @FXML private TableColumn colDosis;
    @FXML private TableColumn colCodigoReceta;
    @FXML private TableColumn colCodigoMedicamento;
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
        cmbCodigoReceta.setItems(getReceta());
        cmbCodigoReceta.setDisable(true);
        cmbCodigoMedicamento.setItems(getMedicamento());
        cmbCodigoMedicamento.setDisable(true);
    }
    
    public void cargarDatos(){
        tblDetalleRecetas.setItems(getDetalleReceta());
        colCodigoDetalleReceta.setCellValueFactory(new PropertyValueFactory<DetalleReceta, Integer>("codigoDetalleReceta"));
        colDosis.setCellValueFactory(new PropertyValueFactory<DetalleReceta, String>("dosis"));
        colCodigoReceta.setCellValueFactory(new PropertyValueFactory<DetalleReceta, Integer>("codigoReceta"));
        colCodigoMedicamento.setCellValueFactory(new PropertyValueFactory<DetalleReceta, Integer>("codigoMedicamento"));
    }
    
    public void seleccionarElemento(){
        if(tblDetalleRecetas.getSelectionModel().getSelectedItem() != null){
           txtDetalleReceta.setText(String.valueOf(((DetalleReceta)tblDetalleRecetas.getSelectionModel().getSelectedItem()).getCodigoDetalleReceta()));
           txtDosis.setText(((DetalleReceta)tblDetalleRecetas.getSelectionModel().getSelectedItem()).getDosis());
           cmbCodigoReceta.getSelectionModel().select(buscarReceta(((DetalleReceta)tblDetalleRecetas.getSelectionModel().getSelectedItem()).getCodigoReceta()));
           cmbCodigoMedicamento.getSelectionModel().select(buscarMedicamento(((DetalleReceta)tblDetalleRecetas.getSelectionModel().getSelectedItem()).getCodigoMedicamento()));
        }
    }
    
    public Receta buscarReceta(int codigoReceta){
        Receta resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarReceta(?)}");
            procedimiento.setInt(1, codigoReceta);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new Receta(registro.getInt("codigoReceta"),
                                       registro.getDate("fechaReceta"),
                                       registro.getInt("numeroColegiado"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return resultado;
    }
    
    public Medicamento buscarMedicamento(int codigoMedicamento){
        Medicamento resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarMedicamento(?)}");
            procedimiento.setInt(1, codigoMedicamento);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new Medicamento(registro.getInt("codigoMedicamento"),
                                            registro.getString("nombreMedicamento"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
    
    public ObservableList<DetalleReceta> getDetalleReceta(){
        ArrayList <DetalleReceta> lista = new ArrayList<DetalleReceta>();
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarDetalleRecetas()}");
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                lista.add(new DetalleReceta(registro.getInt("codigoDetalleReceta"),
                                            registro.getString("dosis"),
                                            registro.getInt("codigoReceta"),
                                            registro.getInt("codigoMedicamento")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return ListaDetalleReceta = FXCollections.observableArrayList(lista);
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
    
    public ObservableList<Medicamento> getMedicamento(){
        ArrayList<Medicamento> lista = new ArrayList<Medicamento>();
        try{
            PreparedStatement procedimiento =Conexion.getInstance().getConexion().prepareCall("{call sp_ListarMedicamentos}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Medicamento(resultado.getInt("codigoMedicamento"),
                                        resultado.getString("nombreMedicamento")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ListaMedicamento = FXCollections.observableArrayList(lista);
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
                if(tblDetalleRecetas.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Esta seguro de eliminar el registro?", "Eliminar Detalle Receta", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BorrarDetalleReceta(?)}");
                            procedimiento.setInt(1, ((DetalleReceta)tblDetalleRecetas.getSelectionModel().getSelectedItem()).getCodigoDetalleReceta());
                            procedimiento.execute();
                            ListaDetalleReceta.remove(tblDetalleRecetas.getSelectionModel().getSelectedIndex());
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
                if(tblDetalleRecetas.getSelectionModel().getSelectedItem() != null){
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    imgEditar.setImage(new Image("/org/josecruz/image/Editar.png"));
                    imgReporte.setImage(new Image("/org/josecruz/image/Cancelar.png"));
                    activarControles();
                    cmbCodigoReceta.setDisable(true);
                    cmbCodigoMedicamento.setDisable(true);
                    txtDetalleReceta.setEditable(false);
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
//                imprimirReporte();
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
        parametros.put("codigoDetalleReceta", null);
        GenerarReporte.mostrarReporte("ReporteDetalleRecetas.jasper", "Reporte de Detalle Recetas", parametros);
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EditarDetalleReceta(?, ?, ?, ?)}");
            DetalleReceta registro = (DetalleReceta)tblDetalleRecetas.getSelectionModel().getSelectedItem();
            registro.setDosis(txtDosis.getText());
            registro.setCodigoReceta(((Receta)cmbCodigoReceta.getSelectionModel().getSelectedItem()).getCodigoReceta());
            registro.setCodigoMedicamento(((Medicamento)cmbCodigoMedicamento.getSelectionModel().getSelectedItem()).getCodigoMedicamento());
            procedimiento.setInt(1, registro.getCodigoDetalleReceta());
            procedimiento.setString(2, registro.getDosis());
            procedimiento.setInt(3, registro.getCodigoReceta());
            procedimiento.setInt(4, registro.getCodigoMedicamento());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void guardar(){
        DetalleReceta registro = new DetalleReceta();
        registro.setDosis(txtDosis.getText());
        registro.setCodigoReceta(((Receta)cmbCodigoReceta.getSelectionModel().getSelectedItem()).getCodigoReceta());
        registro.setCodigoMedicamento(((Medicamento)cmbCodigoMedicamento.getSelectionModel().getSelectedItem()).getCodigoMedicamento());
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarDetalleReceta(?, ?, ?)}");
            procedimiento.setString(1, registro.getDosis());
            procedimiento.setInt(2, registro.getCodigoReceta());
            procedimiento.setInt(3, registro.getCodigoMedicamento());
            procedimiento.execute();
            ListaDetalleReceta.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void desactivarControles(){
        txtDosis.setEditable(false);
        cmbCodigoReceta.setDisable(true);
        cmbCodigoMedicamento.setDisable(true);
    }
    
    public void activarControles(){
        txtDosis.setEditable(true);
        cmbCodigoReceta.setDisable(false);
        cmbCodigoMedicamento.setDisable(false);
    }
    
    public void limpiarControles(){
        txtDetalleReceta.clear();
        txtDosis.clear();
        cmbCodigoReceta.setValue(null);
        cmbCodigoMedicamento.setValue(null);
        tblDetalleRecetas.getSelectionModel().clearSelection();
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
