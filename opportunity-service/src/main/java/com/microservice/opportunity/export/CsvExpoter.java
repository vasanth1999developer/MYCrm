package com.microservice.opportunity.export;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import com.microservice.opportunity.models.entity.OpportunityVo;

public class CsvExpoter {

	public static void exportLeadsToCSV(PrintWriter writer, List<OpportunityVo> opportunitys) {
		try {

			// Write CSV data using OpenCSV or any other library
			// Example:
			CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
			for (OpportunityVo opportunity : opportunitys) {
				csvPrinter.printRecord(opportunity.getOpportunityId(), opportunity.getAccountName(), opportunity.getAmount(), opportunity.getAssignto(),
						opportunity.getDescription(), opportunity.getEmailAddress(), opportunity.getExpectedClosingDate(),opportunity.getFirstName(), 
						opportunity.getLastName(),opportunity.getLeadSource(),opportunity.getNextStep(), opportunity.getProbability(),
						opportunity.getProductName(), opportunity.getSalesStage(),opportunity.getSalutation());

			}
			csvPrinter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
