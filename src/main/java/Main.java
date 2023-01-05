import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<String[]> list = new ArrayList<>();
        list.add("1,John,Smith,USA,25".split(","));
        list.add("2,Ivan,Petrov,RU,23".split(","));

        try {
            xmlCreator(list);
            List<Employee> listOfEmployees = parseXML("data.xml");
            String json = listToJson(listOfEmployees);
            writeString(json);
        } catch (ParserConfigurationException | TransformerException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }


    }

    public static void xmlCreator(List<String[]> list) throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();

        Element staff = document.createElement("staff");
        document.appendChild(staff);

        for (String[] empl : list) {
            Element employee = document.createElement("employee");
            staff.appendChild(employee);

            Element id = document.createElement("id");
            id.appendChild(document.createTextNode(empl[0]));
            employee.appendChild(id);

            Element firstName = document.createElement("firstName");
            firstName.appendChild(document.createTextNode(empl[1]));
            employee.appendChild(firstName);

            Element lastName = document.createElement("lastName");
            lastName.appendChild(document.createTextNode(empl[2]));
            employee.appendChild(lastName);

            Element country = document.createElement("country");
            country.appendChild(document.createTextNode(empl[3]));
            employee.appendChild(country);

            Element age = document.createElement("age");
            age.appendChild(document.createTextNode(empl[4]));
            employee.appendChild(age);
        }

        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(new File("data.xml"));
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(domSource, streamResult);
    }

    public static List<Employee> parseXML(String file) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(file));

        Node root = doc.getDocumentElement();
        NodeList rootList = root.getChildNodes();

        String[][] empl = new String[2][5];

        for (int i = 0; i < rootList.getLength(); i++) {
            NodeList employeeAttr = rootList.item(i).getChildNodes();
            for (int j = 0; j < employeeAttr.getLength(); j++) {
                empl[i][j] = employeeAttr.item(j).getTextContent();
            }
        }

        List<Employee> list = new ArrayList<>();
        list.add(new Employee(Long.parseLong(empl[0][0]), empl[0][1], empl[0][2], empl[0][3], Integer.parseInt(empl[0][4])));
        list.add(new Employee(Long.parseLong(empl[1][0]), empl[1][1], empl[1][2], empl[1][3], Integer.parseInt(empl[1][4])));

        return list;
    }

    public static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    public static void writeString(String json) {
        try (FileWriter file = new FileWriter("data2.json")) {
            file.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}