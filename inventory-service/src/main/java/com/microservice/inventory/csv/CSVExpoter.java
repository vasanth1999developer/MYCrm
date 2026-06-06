package com.microservice.inventory.csv;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import com.microservice.inventory.entity.PriceBookVO;
import com.microservice.inventory.entity.ProductVo;
import com.microservice.inventory.entity.SupplierVO;


public class CSVExpoter {
	
	public static void exportProductToCSV(PrintWriter writer, List<ProductVo> product) {
        try {

	        // Write CSV data using OpenCSV or any other library
	        // Example:
	        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
	        for (ProductVo pro : product) {
	        	csvPrinter.printRecord(pro.getProductName(),pro.getProductSpecification(), pro.getMinStocks(),
	        			pro.getMaxStocks(), pro.getAvaliablesStocks(), pro.getProductType(), pro.getStartDate(), pro.getEndDate(), pro.getCreatedBy(),
	        			pro.getModifiedBy());
				
	        }
	        csvPrinter.flush();
        } catch (IOException e) {
				e.printStackTrace();
			}
	    }
	
	public static void exportSupplierToCSV(PrintWriter writer, List<SupplierVO> supplierVO) {
		 try {

		        // Write CSV data using OpenCSV or any other library
		        // Example:
		        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
		        for (SupplierVO book : supplierVO) {
		        	csvPrinter.printRecord(book.getSupplierId(),book.getSupplierName(),book.getEmailId(),book.getMobileNo(),
		        			book.getAddress(),book.getCity(),book.getState(),book.getCountry(),book.getWebsite(),book.getTechOriented(),
		        			book.getFinancialAmount(), book.getRating(), book.getLocation(), book.getCreatedBy(), book.getModifyiedBy());
					
		        }
		        csvPrinter.flush();
	        } catch (IOException e) {
					e.printStackTrace();
				}
	}

}
