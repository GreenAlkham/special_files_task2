package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};

        String fileCSVName = "data.csv";
        String fileXMLName = "data.xml";

        List<Employee> listCSV = parseCSV(columnMapping, fileCSVName);
        List<Employee> listXML = parseXML(fileXMLName);

        writeString(listCSV);
        writeString(listXML);
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileCSVName) {
        List<Employee> listCSV = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(fileCSVName))) {
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            listCSV = csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listCSV;
    }

    private static List<Employee> parseXML(String fileXMLName) {
        List<Employee> listXML = new ArrayList<>();
        List<String> elements = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(fileXMLName));

            Node root = doc.getDocumentElement();
            NodeList employees = root.getChildNodes();
            for (int i = 0; i < employees.getLength(); i++) {
                Node employee = employees.item(i);

                if (employee.getNodeName().equals("employee")) {
                    NodeList empl = employee.getChildNodes();
                    for (int j = 0; j < empl.getLength(); j++) {
                        Node node_ = empl.item(j);
                        if (Node.ELEMENT_NODE == node_.getNodeType()) {
                            elements.add(node_.getTextContent());
                        }
                    }

                    listXML.add(new Employee(Long.parseLong(elements.get(0)),
                            elements.get(1),
                            elements.get(2),
                            elements.get(3),
                            Integer.parseInt(elements.get(4))));
                    elements.clear();
                }
            }

        } catch (ParserConfigurationException ex) {
            ex.printStackTrace(System.out);
        } catch (SAXException ex) {
            ex.printStackTrace(System.out);
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
        }
        return listXML;
    }

    private static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        Type listType = new TypeToken<List<Employee>>() {
        }.getType();

        String json = gson.toJson(list, listType);
        return json;
    }

    public static void writeString(List<Employee> list) {

        String json = listToJson(list);

        try (FileWriter fileCSVName = new FileWriter("data1.json")) {
            fileCSVName.write(json);
            fileCSVName.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileWriter fileXMLName = new FileWriter("data2.json")) {
            fileXMLName.write(json);
            fileXMLName.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
