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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javax.swing.JOptionPane;
import org.josecruz.bean.Medicamento;
import org.josecruz.db.Conexion;
import org.josecruz.report.GenerarReporte;
import org.josecruz.system.Principal;


public class MedicamentoController implements Initializable {
    private Principal escenarioPrincipal;
    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Medicamento> ListaMedicamento;
    @FXML private TextField txtNombreMedicamento;
    @FXML private TextField txtCodigoMedicamento;
    @FXML private GridPane grpMedicamentos;
    @FXML private TableView tblMedicamentos;
    @FXML private TableColumn colCodigoMedicamento;
    @FXML private TableColumn colNombreMedicamento;
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
        txtCodigoMedicamento.setEditable(false);
    }

    public void cargarDatos(){
       tblMedicamentos.setItems(getMedicamento());
       colCodigoMedicamento.setCellValueFactory(new PropertyValueFactory<Medicamento, Integer>("codigoMedicamento"));
       colNombreMedicamento.setCellValueFactory(new PropertyValueFactory<Medicamento, String>("nombreMedicamento"));
    }
    
    
    public void seleccionarElemento(){
           if(tblMedicamentos.getSelectionModel().getSelectedItem()!= null){
            txtCodigoMedicamento.setText(String.valueOf(((Medicamento)tblMedicamentos.getSelectionModel().getSelectedItem()).getCodigoMedicamento()));
            txtNombreMedicamento.setText(((Medicamento)tblMedicamentos.getSelectionModel().getSelectedItem()).getNombreMedicamento());
           }
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
                if(tblMedicamentos.getSelectionModel().getSelectedItem()!= null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Esta seguro de eliminar el registro?", "Eliminar Medicamento", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BorrarMedicamento(?)}");
                            procedimiento.setInt(1, ((Medicamento)tblMedicamentos.getSelectionModel().getSelectedItem()).getCodigoMedicamento());
                            procedimiento.execute();
                            ListaMedicamento.remove(tblMedicamentos.getSelectionModel().getSelectedIndex());
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
                if(tblMedicamentos.getSelectionModel().getSelectedItem() != null){
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    imgEditar.setImage(new Image("/org/josecruz/image/Editar.png"));
                    imgReporte.setImage(new Image("/org/josecruz/image/Cancelar.png"));
                    activarControles();
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
        parametros.put("codigoMedicamento", null);
        parametros.put("Fondo",GenerarReporte.class.getResource("/org/josecruz/image/Reporte Medicamento.png"));
        GenerarReporte.mostrarReporte("ReporteMedicamentos.jasper", "Reporte de Medicamentos", parametros);
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EditarMedicamentos(?,?)}");
            Medicamento registro = (Medicamento)tblMedicamentos.getSelectionModel().getSelectedItem();
            registro.setNombreMedicamento(txtNombreMedicamento.getText());
            procedimiento.setInt(1, registro.getCodigoMedicamento());
            procedimiento.setString(2, registro.getNombreMedicamento());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void guardar(){
        Medicamento registro = new Medicamento();
        registro.setNombreMedicamento(txtNombreMedicamento.getText());
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarMedicamento(?)}");
            procedimiento.setString(1, registro.getNombreMedicamento());
            procedimiento.execute();
            ListaMedicamento.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
    public void desactivarControles(){
        txtNombreMedicamento.setEditable(false);
    }
    
    public void activarControles(){
        txtNombreMedicamento.setEditable(true);
    }
    
    public void limpiarControles(){
        txtCodigoMedicamento.clear();
        txtNombreMedicamento.clear();
        tblMedicamentos.getSelectionModel().clearSelection();
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
