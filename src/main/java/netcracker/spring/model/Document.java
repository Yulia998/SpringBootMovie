package netcracker.spring.model;

import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.List;

@Component
public class Document {
    private String templatePath;
    private static final Logger LOGGER = Logger.getLogger(Document.class);

    public Document(@Value("${docTemplatePath}") String templatePath) {
        this.templatePath = templatePath;
    }

    public byte[] createMovieDoc(List<Movie> movies) throws IOException, InvalidFormatException {
        try (XWPFDocument document = new XWPFDocument(new FileInputStream((templatePath)))) {
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            XWPFRun runTable;
            XWPFTable mainTable = document.getTables().get(0);
            for (int i = 1; i < movies.size(); i++) {
                fillTable(mainTable, movies.get(i));
                runTable = paragraphs.get(i - 1).createRun();
                runTable.addBreak(BreakType.PAGE);
                document.createTable();
                document.setTable(i, mainTable);
                document.createParagraph();
            }
            fillTable(mainTable, movies.get(0));
            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            document.write(byteArray);
            return byteArray.toByteArray();
        } catch (IOException | InvalidFormatException e) {
            LOGGER.error("Ошибка при считывании/записи данных", e);
            throw e;
        }
    }

    private void fillTable(XWPFTable table, Movie movie) throws IOException, InvalidFormatException {
        if (!movie.getPoster().equals("N/A")) {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Resource> responseEntity = restTemplate.getForEntity(movie.getPoster(), Resource.class);
            InputStream inputStream = responseEntity.getBody().getInputStream();
            table.getRow(0).getCell(0).getParagraphs().get(0).removeRun(0);
            table.getRow(0).getCell(0).getParagraphs().get(0).createRun()
                    .addPicture(inputStream, XWPFDocument.PICTURE_TYPE_JPEG, "",
                            Units.toEMU(180), Units.toEMU(230));
        }
        changeTable(table, 0, 1, movie.getTitle());
        changeTable(table, 1, 2, String.valueOf(movie.getYear()));
        changeTable(table, 2, 2, movie.getCountry());
        changeTable(table, 3, 2, movie.getDirector());
        changeTable(table, 4, 2, movie.getActors());
        changeTable(table, 5, 2, movie.getGenre());
        changeTable(table, 6, 2, movie.getType());
        changeTable(table, 7, 2, movie.getReleased());
        changeTable(table, 8, 2, movie.getRuntime());
        String ratings = movie.getRatings().toString();
        String ratingsOut = ratings.substring(1, ratings.length() - 1);
        changeTable(table, 9, 2, ratingsOut);
        changeTable(table, 10, 2, movie.getPlot());
    }

    private void changeTable(XWPFTable table, int row, int cell, String text) {
        table.getRow(row).getCell(cell).getParagraphs().get(0).getRuns().get(0).setText(text, 0);
    }
}
