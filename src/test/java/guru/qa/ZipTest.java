package guru.qa;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;

import com.opencsv.exceptions.CsvException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.io.InputStreamReader;


public class ZipTest {

    ClassLoader cl = ZipTest.class.getClassLoader();

    @Test
    @DisplayName("Check CSV in ZIP")
    void csvTest() throws Exception {
        try (ZipInputStream zis = openZipStream()) {
            verifyZipEntryContent(zis, "testCSV.csv", inputStream -> {

                Reader reader = new InputStreamReader(inputStream);
                CSVReader csvReader = new CSVReader(reader);
                List<String[]> content = csvReader.readAll();
                Assertions.assertEquals(3, content.size());
                final String[] firstRow = content.get(0);
                final String[] secondRow = content.get(1);
                final String[] thirdRow = content.get(2);

                Assertions.assertArrayEquals(new String[]{"111", "test111"}, firstRow);
                Assertions.assertArrayEquals(new String[]{"222", "test222"}, secondRow);
                Assertions.assertArrayEquals(new String[]{"333", "test333"}, thirdRow);
            });
        }
    }

    @Test
    @DisplayName("Check PDF in ZIP")
    void testPdfTest() throws Exception {
        try (ZipInputStream zis = openZipStream()) {
            verifyZipEntryContent(zis, "testPDF.pdf", inputStream -> {
                PDF pdf = new PDF(inputStream);
                Assertions.assertTrue(pdf.text.contains("test"));
            });
        }
    }

    @Test
    @DisplayName("Check XLSX in ZIP")
    void testXlsxTest() throws Exception {
        try (ZipInputStream zis = openZipStream()) {
            verifyZipEntryContent(zis, "testXLSX.xlsx", inputStream -> {
                XLS xls = new XLS(inputStream);
                String cellValue = xls.excel.getSheetAt(0)
                        .getRow(0)
                        .getCell(1)
                        .getStringCellValue();
                Assertions.assertTrue(cellValue.contains("1"));
                assertThat(xls.excel.getSheetName(0), equalTo("Лист1"));
            });
        }
    }

    private ZipInputStream openZipStream() {
        InputStream stream = cl.getResourceAsStream("testZIP.zip");
        return new ZipInputStream(stream);
    }

    private void verifyZipEntryContent(ZipInputStream zis, String entryName, ZipEntryVerifier verifier)
            throws IOException, CsvException {
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            final String name = entry.getName();
            if (name.contains(entryName)) {
                verifier.verifyEntry(zis);
            }
        }
    }

    interface ZipEntryVerifier {
        void verifyEntry(InputStream inputStream) throws IOException, CsvException;
    }

}