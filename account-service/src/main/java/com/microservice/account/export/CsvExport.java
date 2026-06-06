package com.microservice.account.export;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import com.microservices.account.entity.AccountVO;

public class CsvExport {
	
	public static void exportAccountToCSV(PrintWriter writer, List<AccountVO> accountlist) {
        try {
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
            for (AccountVO account : accountlist) {
                csvPrinter.printRecord(account.getAccountName(),account.getAccountOwner(),account.getAnnualRevenue(),account.getCity(),account.getContactNumber(),account.getCountry(),account.getDescription(),account.getEmail(),account.getIndustry(),account.getNoOfEmploye(),account.getParentAccount(),account.getPincode(),account.getSalutation(),account.getState(),account.getStreet(),account.getType());
      
            }
            csvPrinter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
