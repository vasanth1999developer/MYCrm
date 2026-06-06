package com.microservice.opportunity.controller;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import com.microservice.opportunity.bo.OpportunityBo;
import com.microservice.opportunity.export.CsvExpoter;
import com.microservice.opportunity.models.entity.OpportunityVo;
import com.microservice.opportunity.repository.OpportunityRepository;
import com.microservice.opportunity.service.OpportunityService;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import lombok.extern.log4j.Log4j2;

@RequestMapping("/opportunity")
@RestController
@Log4j2
//@CrossOrigin(origins = "*")
public class OpportunityController {

	@Autowired
	OpportunityRepository opportunityRepository;

	@Autowired
	OpportunityService opportunityService;

	@PostMapping("/create-opportunity")
	public Boolean create(@RequestBody OpportunityBo opportunityBo, HttpServletRequest request) {

		boolean status = false;
		try {
			if (null != request.getHeader("id")) {
				String id = request.getHeader("id");
				long number = Long.parseLong(id);
				opportunityBo.setCreatedBy(number);
				opportunityBo.setCreatedTime(new Date());
				opportunityBo.setDelete(false);
			}
			status = opportunityService.save(opportunityBo);

		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.info(e.getMessage(), e);
			}
		}
		return status;
	}

	@GetMapping("/view-opportunity/{id}")
	public ResponseEntity<OpportunityBo> view(@PathVariable Long id) {

		OpportunityBo opportunity = null;
		try {
			opportunity = opportunityService.findById(id);
			if (null != opportunity) {
				return new ResponseEntity<OpportunityBo>(opportunity, HttpStatus.OK);
			}
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.info(e.getMessage(), e);
			}
		}
		return new ResponseEntity<OpportunityBo>(opportunity, HttpStatus.NOT_FOUND);
	}

	@PutMapping("/update-opportunity")
	public Boolean update(@RequestBody OpportunityVo opportunityVo, HttpServletRequest request) {
		try {
			if (null != request.getHeader("id")) {
				String id = request.getHeader("id");
				long number = Long.parseLong(id);
				opportunityVo.setModifiedBy(number);
				opportunityVo.setModifiedTime(new Date());
			}
			OpportunityVo opportunity = opportunityRepository.save(opportunityVo);

			if (null != opportunity && 0 < opportunity.getOpportunityId()) {
				return true;
			}
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.info(e.getMessage(), e);
			}
		}
		return false;
	}

	@DeleteMapping("/delete-opportunity/{id}")
	public ResponseEntity<?> deleteOpportunity(@PathVariable Long id) {
		try {
			boolean status = opportunityService.deleteById(id);
			if (status) {
				return ResponseEntity.ok().body("Opportunity successfully deleted.");
			} else {
				return ResponseEntity.badRequest()
						.body("Failed to delete opportunity: No opportunity found with ID " + id);
			}
		} catch (Exception e) {
			log.error("Error deleting opportunity with ID " + id, e);
			return ResponseEntity.internalServerError().body("Internal server error while deleting opportunity.");
		}
	}

	@GetMapping("/checkEmailId/{emailAddress}")
	public ResponseEntity<?> checkEmailId(@PathVariable("emailAddress") String emailAddress,
			OpportunityVo opportunityVo) throws Exception {
		boolean status = false;
		try {
			OpportunityVo opportunity = opportunityRepository.findByEmail(emailAddress);
			if (null != opportunity) {
				status = true;
			}
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.info(e.getMessage(), e);
			}
		}
		return ResponseEntity.ok(status);
	}

	@GetMapping("/pageRecord")
	public ResponseEntity<Page<OpportunityVo>> getItems(Pageable pageable) {
		Page<OpportunityVo> pageRecords = null;
		try {
			pageRecords = opportunityRepository.findAll(pageable);
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.info(e.getMessage(), e);
			}
		}
		return ResponseEntity.ok(pageRecords);
	}

	@GetMapping("/export")
	public void exportOpportunitys(HttpServletResponse response) throws IOException {

		List<OpportunityVo> opportunityVo = new ArrayList<>();

		response.setContentType("text/csv");
		response.setHeader("Content-Disposition", "attachment; filename=opportunitys.csv");

		try {
			opportunityVo = opportunityService.getAllOpportunity();
			System.out.println(opportunityVo);

			CsvExpoter.exportLeadsToCSV(response.getWriter(), opportunityVo);
		} catch (Exception e) {

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
				if (nextRecord.length > 0) {
					OpportunityVo opportunityVo = new OpportunityVo();

					opportunityVo.setAccountName(nextRecord[1]);
					opportunityVo.setAmount(Double.parseDouble(nextRecord[2]));
					opportunityVo.setAssignto(nextRecord[3]);
					opportunityVo.setDescription(nextRecord[4]);
					opportunityVo.setEmailAddress(nextRecord[5]);
					opportunityVo.setFirstName(nextRecord[7]);
					opportunityVo.setLastName(nextRecord[8]);
					opportunityVo.setLeadSource(nextRecord[9]);
					opportunityVo.setNextStep(nextRecord[10]);
					opportunityVo.setProbability(Double.parseDouble(nextRecord[11]));
					opportunityVo.setProductName(nextRecord[12]);
					opportunityVo.setSalesStage(nextRecord[13]);
					opportunityVo.setSalutation(nextRecord[14]);

					opportunityRepository.save(opportunityVo);
				} else {
					return ResponseEntity.badRequest().body("Invalid CSV format");
				}
			}

			return ResponseEntity.ok(true);

		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to upload file: " + e.getMessage());
		}
	}

	@GetMapping("/list/{pageIndex}/{pageSize}")
	public ResponseEntity<?> listOpportunity(@PathVariable("pageIndex") int pageIndex,
			@PathVariable("pageSize") int pageSize, @RequestParam(name = "sortOrder", required = false) String sorter,
			@RequestParam(name = "searchName", required = false) String searchName,
			@RequestParam(name = "searchEmailId", required = false) String searchEmailId,
			@RequestParam(name = "columnName", required = false) String columnName) {

		
		try {
			if ((0 <= pageIndex) && (0 < pageSize)) {
				Page<OpportunityBo> pageOpportunity = opportunityService.listOpportunity(pageIndex, pageSize, sorter,
						searchName, searchEmailId, columnName);
				if (null != pageOpportunity) {
					return ResponseEntity.ok(pageOpportunity);
				}
			} else {
				return ResponseEntity.badRequest().body("Invalid Page Index Or Size");
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (log.isDebugEnabled()) {
				log.info(e.getMessage(), e);
			}
		}
		return ResponseEntity.badRequest().body("Invalid Page Index Or Size");
	}

}
