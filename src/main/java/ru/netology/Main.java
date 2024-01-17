package ru.netology;

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
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {

        // CSV файл
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileNameCSV = "data.csv";
        List<Employee> listCSV = parseCSV(columnMapping, fileNameCSV);

        // XML файл
        String fileNameXML = "data.xml";
        List<Employee> listXML = parseXML(fileNameXML);

        // Создание файла JSON из CSV файла
        String jsonCSV = listToJson(listCSV);
        writeString(jsonCSV, "data.json");

//        Создание файла JSON из XML файла
        String jsonXML = listToJson(listXML);
        writeString(jsonXML, "data2.json");
    }

    private static List<Employee> parseXML(String fileNameXML) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileNameXML));
        NodeList nodeList = doc.getElementsByTagName("employee");
        List<Employee> employeeList = new ArrayList<>();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                Element element = (Element) node;
                NodeList elementNodeList = element.getChildNodes();
                Employee employee = new Employee();

                employee.id = Long.parseLong(elementNodeList.item(1).getTextContent());
                employee.firstName = elementNodeList.item(3).getTextContent();
                employee.lastName = elementNodeList.item(5).getTextContent();
                employee.country = elementNodeList.item(7).getTextContent();
                employee.age = Integer.parseInt(elementNodeList.item(9).getTextContent());
                
                employeeList.add(employee);
            }
        }
        return employeeList;
    }

    private static void writeString(String json, String fileName) {
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(json);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting().create();
        Gson gson = builder.create();
        return gson.toJson(list, listType);
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) throws IOException {
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            return csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}