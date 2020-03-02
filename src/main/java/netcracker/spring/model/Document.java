package netcracker.spring.model;

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
    private String outputPath;

    public Document(@Value("${docTemplatePath}") String templatePath,
                    @Value("${docCreatedPath}") String outputPath) {
        this.templatePath = templatePath;
        File folder = new File(outputPath.substring(0, outputPath.length() - 1));
        if (!folder.exists()) {
            folder.mkdir();
        }
        this.outputPath = outputPath;
    }

    public File createMovieDoc(Movie movie) throws IOException, InvalidFormatException {
        XWPFDocument document = new XWPFDocument(new FileInputStream((templatePath)));
        XWPFTable table = document.getTables().get(0);
        fillTable(table, movie);
        File file = new File(outputPath + movie.getTitle() + ".docx");
        FileOutputStream out = new FileOutputStream(file);
        document.write(out);
        out.flush();
        out.close();
        return file;
    }

    public File createMovieDoc(List<Movie> movies) throws IOException, InvalidFormatException {
        XWPFDocument document = new XWPFDocument(new FileInputStream((templatePath)));
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
        File file = new File(outputPath + "movies.docx");
        FileOutputStream out = new FileOutputStream(file);
        document.write(out);
        out.flush();
        out.close();
        return file;
    }

    private void fillTable(XWPFTable table, Movie movie) throws IOException, InvalidFormatException {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Resource> responseEntity = restTemplate.getForEntity(movie.getPoster(), Resource.class);
        InputStream inputStream = responseEntity.getBody().getInputStream();
        table.getRow(0).getCell(0).getParagraphs().get(0).removeRun(0);
        table.getRow(0).getCell(0).getParagraphs().get(0).createRun()
                .addPicture(inputStream, XWPFDocument.PICTURE_TYPE_JPEG, "",
                        Units.toEMU(180), Units.toEMU(230));
        table.getRow(0).getCell(1).getParagraphs().get(0).getRuns().get(0).setText(movie.getTitle(), 0);
        table.getRow(1).getCell(2).getParagraphs().get(0).getRuns().get(0).setText(String.valueOf(movie.getYear()), 0);
        table.getRow(2).getCell(2).getParagraphs().get(0).getRuns().get(0).setText(movie.getCountry(), 0);
        table.getRow(3).getCell(2).getParagraphs().get(0).getRuns().get(0).setText(movie.getDirector(), 0);
        table.getRow(4).getCell(2).getParagraphs().get(0).getRuns().get(0).setText(movie.getActors(), 0);
        table.getRow(5).getCell(2).getParagraphs().get(0).getRuns().get(0).setText(movie.getGenre(), 0);
        table.getRow(6).getCell(2).getParagraphs().get(0).getRuns().get(0).setText(movie.getType(), 0);
        table.getRow(7).getCell(2).getParagraphs().get(0).getRuns().get(0).setText(movie.getReleased().toString(), 0);
        table.getRow(8).getCell(2).getParagraphs().get(0).getRuns().get(0).setText(movie.getRuntime(), 0);
        String ratings = movie.getRatings().toString();
        String ratingsOut = ratings.substring(1, ratings.length() - 1);
        table.getRow(9).getCell(2).getParagraphs().get(0).getRuns().get(0).setText(ratingsOut, 0);
        table.getRow(10).getCell(2).getParagraphs().get(0).getRuns().get(0).setText(movie.getPlot(), 0);
    }
}
