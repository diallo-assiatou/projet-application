package ci.pigier.controllers.ui;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import ci.pigier.DatabaseConnection;
import ci.pigier.controllers.BaseController;
import ci.pigier.model.Note;
import ci.pigier.ui.FXMLPage;

import javafx.collections.ListChangeListener;

import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;


public class ListNotesUIController extends BaseController implements Initializable {

    @FXML
    private TableColumn<Note,String> descriptionTc;

    @FXML
    private Label notesCount;

    @FXML
    private TableView<Note> notesListTable;
    
    @FXML
    private TextField searchNotes;
    
    @FXML
    private TableColumn<Note, String> titleTc;
    
    

    @FXML
    void doDelete(ActionEvent event ) {
   	
    	 Note selectedNote = notesListTable.getSelectionModel().getSelectedItem();
         if (selectedNote != null) {
             deleteNotes(selectedNote.getId());
             selectedNotes();
         } else {
             System.out.println("Please select a note to delete.");
         }
   	
   }

    @FXML
    void doEdit(ActionEvent event) throws IOException {
    	Note selectedNote = notesListTable.getSelectionModel().getSelectedItem();
        if (selectedNote != null) {
            editNote = selectedNote;
            navigate(event, FXMLPage.ADD.getPage());
        } else {
            System.out.println("Please select a note to edit.");
        }
    }

    @FXML
    void newNote(ActionEvent event) throws IOException {
    	editNote = null;
    	navigate(event, FXMLPage.ADD.getPage());
    }
    @FXML
    public void selectedNotes() {
		String listNotes="SELECT  titre,description FROM notes";
		try{Connection cnx =DatabaseConnection.getConnection();
			Statement stamt=cnx.createStatement();
			ResultSet res=stamt.executeQuery(listNotes);{
				data.clear();
				while (res.next()) {
					
					String title = res.getString("titre");
					String description = res.getString("description");
					data.add(new Note (0,title,description));
					
				}
			}
				
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
	}

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
    	FilteredList<Note> filteredData = new FilteredList<>(data, n -> true);
        notesListTable.setItems(filteredData);
        titleTc.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionTc.setCellValueFactory(new PropertyValueFactory<>("description"));
        searchNotes.setOnKeyReleased(e -> {
            filteredData.setPredicate(n -> {
                if (searchNotes.getText() == null || searchNotes.getText().isEmpty())
                    return true;
                return n.getTitle().contains(searchNotes.getText()) || n.getDescription().contains(searchNotes.getText());
            });
        });
        notesListTable.getItems().addListener((ListChangeListener<Note>) change -> updateNotesCount());
        updateNotesCount();
        selectedNotes();
    }

    private void updateNotesCount() {
        int count = notesListTable.getItems().size();
        notesCount.setText(count + " Notes");
    }
    
    public void deleteNotes(int idNote) {
    	 String delQuery = "DELETE FROM notes WHERE id = ?";
         try (Connection conx = DatabaseConnection.getConnection();
              PreparedStatement pstmt = conx.prepareStatement(delQuery)) {
             pstmt.setInt(1, idNote);
             int affectedRows = pstmt.executeUpdate();
             if (affectedRows > 0) {
                 System.out.println("Note deleted successfully.");
             } else {
                 System.out.println("Note not found.");
             }
         } catch (SQLException e) {
             e.printStackTrace();
         }
     }
}