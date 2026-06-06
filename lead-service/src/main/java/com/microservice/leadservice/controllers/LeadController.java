package com.microservice.leadservice.controllers;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.microservice.leadservice.bean.LeadBO;
import com.microservice.leadservice.export.CSVExpoter;
import com.microservice.leadservice.models.entity.LeadVO;
import com.microservice.leadservice.repository.LeadRepository;
import com.microservice.leadservice.services.LeadService;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import lombok.extern.log4j.Log4j2;
//@CrossOrigin(origins = "*")

@RestController
@RequestMapping("lead")
@Log4j2
@Endpoint(id="lead")
public class LeadController {

	@Autowired
	private LeadService leadService;
	@Autowired
	private LeadRepository  leadRepository;



	@PostMapping("/create-lead")
	public ResponseEntity<?> createLead(@RequestBody LeadBO leadBo,HttpServletRequest request)throws Exception{

		try {
			if(null!=request.getHeader("id")) {
				String id=request.getHeader("id");
				long number = Long.parseLong(id);
				leadBo.setCreatedBy(number);
			}

			leadBo.setDelete(false);
			leadBo.setCreated(new Date());
			leadBo.setModified(new Date());
			leadBo = leadService.createLead(leadBo);

		}catch (Exception e) {
			e.printStackTrace();
			if(log.isInfoEnabled()) {
				log.info(e.getMessage(),e);

			}

		}

		return ResponseEntity.ok(leadBo);

	}
	@ReadOperation
	@GetMapping("/get-all-leads")//
	public ResponseEntity<?> getAllLeads(
			)throws Exception{

		List<LeadVO> leadVo = new ArrayList<>();

		try {
			leadVo = leadService.getAllLeads();

			if( leadVo != null  && !leadVo.isEmpty()) {
				return ResponseEntity.ok(leadVo);

			}
		}catch (Exception e) {
			e.printStackTrace();
			if(log.isInfoEnabled()) {
				log.info(e.getMessage(),e);

			}
		}

		return ResponseEntity.ok(leadVo);

	}
	@PutMapping("/update-lead/{id}")
	public ResponseEntity<?> updateLead(@PathVariable("id") long leadId,@RequestBody LeadBO leadBo ,HttpServletRequest request)throws Exception{

		LeadBO leadBO =new LeadBO();

		try {

			if(null!=request.getHeader("id")) {
				String id=request.getHeader("id");
				long number = Long.parseLong(id);
				leadBo.setModifiedBy(number);
			}

			leadBO = leadService.getLeadById(leadId);
			
			leadBo.setLeadId(leadId);
			leadBo.setCreatedBy(leadBO.getCreatedBy());
			leadBo.setCreated(leadBO.getCreated());
			leadBo.setDelete(false);
			leadBo.setModified(new Date());
			
			leadBo = leadService.updateLead(leadBo);

		}catch (Exception e) {
			e.printStackTrace();
			if(log.isInfoEnabled()) {
				log.info(e.getMessage(),e);

			}
		}

		return ResponseEntity.ok(leadBo);

	}
	@GetMapping("/get-leadById/{id}")
	public ResponseEntity<?> getLeadById(@PathVariable("id") long leadId)throws Exception{

		LeadBO leadBO =new LeadBO();

		try {

			leadBO = leadService.getLeadById(leadId);

		}catch (Exception e) {
			e.printStackTrace();
			if(log.isInfoEnabled()) {
				log.info(e.getMessage(),e);

			}
		}

		return ResponseEntity.ok(leadBO);

	}

	@DeleteMapping("/delete-lead/{id}")
	public ResponseEntity<Boolean> deleteLead(@PathVariable("id") long leadId)throws Exception{

		boolean status = false;

		try {

			status = leadService.deleteLead(leadId);

		}catch (Exception e) {
			e.printStackTrace();
			if(log.isInfoEnabled()) {
				log.info(e.getMessage(),e);

			}
		}

		return ResponseEntity.ok(status);

	}

	@GetMapping("/export")
	public void exportLeads(HttpServletResponse response) throws IOException {
	   
		List<LeadVO> leadVo = new ArrayList<>();

	    response.setContentType("text/csv");
	    response.setHeader("Content-Disposition", "attachment; filename=leads.csv");

	   
		try {
			leadVo = leadService.getAllLeads();
		System.out.println(leadVo);

	  
		CSVExpoter.exportLeadsToCSV(response.getWriter(), leadVo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@PostMapping("/import")
	public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) throws CsvValidationException {
	    if (file.isEmpty()) {
	        return new ResponseEntity<>("Please select a file to upload", HttpStatus.BAD_REQUEST);
	    }

	    try (InputStreamReader reader = new InputStreamReader(file.getInputStream());
	            CSVReader csvReader = new CSVReader(reader)) {

	           String[] nextRecord;
	           while ((nextRecord = csvReader.readNext()) != null) {
	               if (nextRecord.length >0) {
	                   LeadVO entity = new LeadVO();
	                
	                   
	                 entity.setAnnualRevenue(Integer.parseInt(nextRecord[1]));
	                entity.setCity(nextRecord[2]);
	                   entity.setCompanyName(nextRecord[3]) ;
	                   entity.setCountry(nextRecord[4]);
	                   entity.setDescriptions(nextRecord[5]);
	                   entity.setDesignation(nextRecord[6]);
	                   entity.setEmployeeName(nextRecord[7]);
	                   entity.setFirstName(nextRecord[8]);
	                   entity.setIndustry(nextRecord[9]);
	                   entity.setLastName(nextRecord[10]);
	                   entity.setLeadOwner(nextRecord[11]);
	                   entity.setLeadSourse(nextRecord[12]);
	                   entity.setMobileNo(Long.parseLong(nextRecord[13]));

	                   entity.setNoOfEmployee(Integer.parseInt(nextRecord[14]));

	                   entity.setPostalCode(Integer.parseInt(nextRecord[15]));
	                   entity.setProductName(nextRecord[16]);
	                   entity.setRating(nextRecord[17]);
	                   entity.setSalutation(nextRecord[18]);
	                   entity.setState(nextRecord[19]);
	                   entity.setStreet(nextRecord[20]);
	                   entity.setWebsite(nextRecord[21]);
	                   entity.setLeadType(nextRecord[22]);
	                   entity.setEmail(nextRecord[23]);
	                   entity.setEmployeeId(Long.parseLong(nextRecord[24]));
	                   entity.setProductId(Long.parseLong(nextRecord[25]));
	                   entity.setLeadSourceId(Integer.parseInt(nextRecord[26]));

	                   leadRepository.save(entity);
	               } else {
	                   return ResponseEntity.badRequest().body("Invalid CSV format");
	               }
	           }

	           return ResponseEntity.ok(true);

	       } catch (IOException e) {
	           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file: " + e.getMessage());
	       }
	   }
	
	@GetMapping("/leadList/{pageIndex}/{pageSize}")
    public ResponseEntity<Page<LeadVO>> listAccesses
    (@PathVariable("pageIndex") int pageIndex,
            @PathVariable("pageSize") int pageSize, 
            @RequestParam(name = "column", required = false) String column,
            @RequestParam(name = "order", required = false) String order,
            @RequestParam(name = "text", required = false) String text) {
        Page<LeadVO> pageRecord=null;
        try {
            
            pageRecord= leadService.listLeads(pageIndex, pageSize, column,order, text);
                
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(pageRecord);

    }
               
	@GetMapping("/checkedEmail/{email}")
	public ResponseEntity<?> checkingEmail(@PathVariable ("email") String email,LeadVO leadVo )throws Exception{
		boolean value=false;
		try {
			
			LeadVO lead= leadRepository.findByEmail(email);
			if(null !=lead) {
				value=true;
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok(value);
	}
	
	
	
}
