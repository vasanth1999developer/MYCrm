package com.microservice.leadservice.export;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import com.microservice.leadservice.bean.LeadBO;
import com.microservice.leadservice.models.entity.LeadVO;

public class CSVExpoter {
	public static void exportLeadsToCSV(PrintWriter writer, List<LeadVO> leads) {
        try {

	        
	        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
	        for (LeadVO lead : leads) {
	        	csvPrinter.printRecord(lead.getLeadId(), lead.getAnnualRevenue(), lead.getCity(),lead.getCompanyName(),lead.getCountry(),lead.getDescriptions()
	        			,lead.getDesignation(),lead.getEmployeeName(),lead.getFirstName(),lead.getIndustry(),lead.getLastName(),lead.getLeadOwner(),lead.getLeadSourse(),

	        			//,lead.getDesignation(),lead.getEmail(),lead.getEmployeeName(),lead.getFirstName(),lead.getIndustry(),lead.getLastName(),lead.getLeadOwner(),lead.getLeadSourse(),
							lead.getMobileNo(),lead.getNoOfEmployee(),lead.getPostalCode(),lead.getProductName(),lead.getRating(),lead.getSalutation(),lead.getState(),
							lead.getStreet(),lead.getWebsite(),lead.getLeadType(),lead.getEmail(),lead.getEmployeeId(),lead.getProductId(),lead.getLeadSourceId());
				
	        }
	        csvPrinter.flush();
        } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
}
