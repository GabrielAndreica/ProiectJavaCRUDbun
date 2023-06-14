import net.proteanit.sql.DbUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Studenti {
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JTextField textField4;
    private JTextField textField5;
    private JTextField textField6;
    private JButton ADAUGAButton;
    private JTable table1;
    private JButton STERGEButton;
    private JPanel Main;
    private JButton MODIFICAButton;
    private JScrollPane table_1;
    private JTextField textField7;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Studenti");
        frame.setContentPane(new Studenti().Main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public Studenti() {
        connect();
        table_load();
        ADAUGAButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adaugaStudent();
                table_load();
            }
        });
        STERGEButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stergeStudent();
                table_load();
            }
        });
        MODIFICAButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modificaStudent();
                table_load();
            }
        });
    }

    Connection con;
    PreparedStatement pst;

    public void connect(){

        try{

            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/proiectjava", "root", "");
            System.out.println("Succes");

        }
        catch(ClassNotFoundException ex){

        }

        catch(SQLException ex){

        }

    }

    void table_load(){

        try{
            Statement statement = con.createStatement();
            String query = "SELECT * FROM studenti";
            ResultSet resultSet = statement.executeQuery(query);

            // Obținerea metadatelor rezultatului
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Crearea unui model de tabel
            DefaultTableModel tableModel = new DefaultTableModel();

            // Adăugarea numelor coloanelor în modelul de tabel
            for (int i = 1; i <= columnCount; i++) {
                tableModel.addColumn(metaData.getColumnName(i));
            }

            // Adăugarea datelor în modelul de tabel
            while (resultSet.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = resultSet.getObject(i);
                }
                tableModel.addRow(row);
            }

            // Setarea modelului de tabel pentru controlul tabel
            table1.setModel(tableModel);

            // Închiderea resurselor
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        }

    private void adaugaStudent() {

        String nume = textField1.getText();
        String prenume = textField2.getText();
        String specializare = textField3.getText();
        String anStudiu = textField4.getText();
        String telefon = textField5.getText();
        String email = textField6.getText();

        try {

            String query = "INSERT INTO studenti (Nume, Prenume, Specializare, An_Studiu, Telefon, Email) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            // Crearea unui obiect PreparedStatement pentru a executa instrucțiunea SQL
            PreparedStatement preparedStatement = con.prepareStatement(query);

            // Setarea valorilor parametrilor în instrucția SQL
            preparedStatement.setString(1, nume);
            preparedStatement.setString(2, prenume);
            preparedStatement.setString(3, specializare);
            preparedStatement.setString(4, anStudiu);
            preparedStatement.setString(5, telefon);
            preparedStatement.setString(6, email);

            // Executarea instrucțiunii SQL pentru inserarea datelor
            preparedStatement.executeUpdate();

            // Închiderea resurselor
            preparedStatement.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
            }
    }

    private void stergeStudent() {
        try {
            String nume = textField7.getText(); // obțineți numele studentului pe care doriți să-l ștergeți

            String query = "DELETE FROM studenti WHERE Id = ?";

            // Crearea unui obiect PreparedStatement pentru a executa instrucțiunea SQL
            PreparedStatement preparedStatement = con.prepareStatement(query);

            // Setarea valorii parametrului în instrucțiunea SQL
            preparedStatement.setString(1, nume);

            // Executarea instrucțiunii SQL pentru ștergerea datelor
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Studentul a fost șters cu succes
                System.out.println("Studentul a fost șters cu succes din baza de date.");
            } else {
                // Nu a fost găsit niciun student cu numele specificat
                System.out.println("Nu a fost găsit niciun student cu ID-ul specificat în baza de date.");
            }

            // Închiderea resurselor
            preparedStatement.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void modificaStudent() {
        try {
            String id = textField7.getText(); // obțineți ID-ul studentului pe care doriți să-l modificați

            // Verificați dacă există valori pentru celelalte câmpuri
            String nume = textField1.getText();
            String prenume = textField2.getText();
            String specializare = textField3.getText();
            String anStudiu = textField4.getText();
            String telefon = textField5.getText();
            String email = textField6.getText();

            // Construiți interogarea SQL bazată pe câmpurile completate
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("UPDATE studenti SET");

            List<String> setValues = new ArrayList<>();
            if (!nume.isEmpty()){
                setValues.add(" Nume = ?");
            }

            if (!prenume.isEmpty()) {
                setValues.add(" Prenume = ?");
            }
            if (!specializare.isEmpty()) {
                setValues.add(" Specializare = ?");
            }
            if (!anStudiu.isEmpty()) {
                setValues.add(" An_Studiu = ?");
            }
            if (!telefon.isEmpty()) {
                setValues.add(" Telefon = ?");
            }
            if (!email.isEmpty()) {
                setValues.add(" Email = ?");
            }

            if (setValues.isEmpty()) {
                // Niciun câmp nu a fost completat
                System.out.println("Nu ați completat niciun câmp pentru a actualiza.");
                return;
            }

            queryBuilder.append(String.join(",", setValues));
            queryBuilder.append(" WHERE Id = ?");

            // Crearea unui obiect PreparedStatement pentru a executa instrucțiunea SQL
            PreparedStatement preparedStatement = con.prepareStatement(queryBuilder.toString());

            // Setarea valorilor parametrilor în instrucțiunea SQL
            int paramIndex = 1;

            if (!nume.isEmpty()){
                preparedStatement.setString(paramIndex++, nume);
            }

            if (!prenume.isEmpty()) {
                preparedStatement.setString(paramIndex++, prenume);
            }
            if (!specializare.isEmpty()) {
                preparedStatement.setString(paramIndex++, specializare);
            }
            if (!anStudiu.isEmpty()) {
                preparedStatement.setString(paramIndex++, anStudiu);
            }
            if (!telefon.isEmpty()) {
                preparedStatement.setString(paramIndex++, telefon);
            }
            if (!email.isEmpty()) {
                preparedStatement.setString(paramIndex++, email);
            }
            preparedStatement.setString(paramIndex, id);

            // Executarea instrucțiunii SQL pentru modificarea datelor
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Studentul a fost modificat cu succes
                System.out.println("Datele studentului au fost actualizate cu succes în baza de date.");
            } else {
                // Nu a fost găsit niciun student cu numele specificat
                System.out.println("Nu a fost găsit niciun student cu id-ul specificat în baza de date.");
            }

            // Închiderea resurselor
            preparedStatement.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

}

