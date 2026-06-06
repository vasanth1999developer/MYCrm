package com.microservices.account.controller;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.microservice.account.export.CsvExport;
import com.microservices.account.entity.AccountVO;
import com.microservices.account.model.AccountBO;
import com.microservices.account.repositry.AccountRepository;
import com.microservices.account.service.AccountService;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import lombok.extern.log4j.Log4j2;
 

//@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/account")
@Log4j2
public class AccountController {
	
	@Autowired
	private AccountService service;
	@Autowired
	private AccountRepository repository;
	
	
	 @PostMapping("/create-account")
		 public ResponseEntity<?> createAccount(@RequestBody AccountBO bo,HttpServletRequest request)throws Exception{
	        try {
	            if (request.getHeader("id") != null) {
	                String id = request.getHeader("id");
	                long number = Long.parseLong(id);
	                bo.setCreatedBy(number);
	            }
	            bo = service.createAccount(bo);

	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	        }
	        return ResponseEntity.ok(bo);
	    }
	
	@GetMapping("/getAllAccount")
	public ResponseEntity<?> getAllAccount(){
		List<AccountBO> accountlist=new ArrayList<>();
		try {
			accountlist=service.getAllAccount();
			if(null==accountlist||accountlist.size()==0||accountlist.isEmpty()) {
				return new ResponseEntity<List<AccountBO>>(accountlist,HttpStatus.NOT_FOUND);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
		return new ResponseEntity<List<AccountBO>>(accountlist,HttpStatus.OK);
		
	}
	
	@PutMapping("/update-account/{id}")
	public ResponseEntity<?> updateAccount(@RequestBody AccountBO bo,@PathVariable int id,HttpServletRequest request){
		try {
			if(null!=request.getHeader("id")) {
	            String id1=request.getHeader("id");
	             long number = Long.parseLong(id1);
	             bo.setModifiedBy(number);
	           }
			AccountBO acc=new AccountBO();
			acc=service.getAccountId(id);
			bo.setAccountId(id);
			if(0!=bo.getAccountId()) {
			bo=service.updateAccount(bo);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok(bo);
		
	}
	
	@GetMapping("/get-account/{id}")
	public ResponseEntity<?> getAccountById(@PathVariable("id") int id){
		AccountBO bo=new AccountBO();
		try {
			bo.setAccountId(id);
			bo=service.getAccountId(id);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return ResponseEntity.ok(bo);
		
	}
	
	@DeleteMapping("/delete-account/{id}")
	public ResponseEntity<?> deletAccountById(@PathVariable("id") int id){
		/* boolean status=false; */
		AccountBO bo=new AccountBO();
		try {
			
			bo.setAccountId(id);
			bo=service.deletAccount(bo);
			//if(null!=bo) {
//				return ResponseEntity.ok(true);
			//}
		
		}catch(Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok(bo);
		
	}
	
	
	@GetMapping("/searchbyowner")
    public List<AccountVO> searchAccountsByOwner(@RequestParam String owner) {
        return service.searchAccountsByOwner(owner);
    }
	
	@GetMapping("/searchbyaccountName")
    public List<AccountVO> searchAccountsName(@RequestParam String accountName) {
        return service.searchAccountName(accountName);
    }
	
	@PostMapping("/import")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws CsvValidationException {
        if (file.isEmpty()) {
            return new ResponseEntity<>("Please select a file to upload", HttpStatus.BAD_REQUEST);
        }

        try (InputStreamReader reader = new InputStreamReader(file.getInputStream());
                CSVReader csvReader = new CSVReader(reader)) {

               String[] nextRecord;
               while ((nextRecord = csvReader.readNext()) != null) {
                   if (nextRecord.length >0) {
                	   AccountVO entity = new AccountVO();
                          
                          entity.setAccountName(nextRecord[0]);
                          entity.setAccountOwner(nextRecord[1]);
                          entity.setAnnualRevenue(nextRecord[2]);
                          entity.setCity(nextRecord[3]);
                          long mobileNo=(Long.parseLong(nextRecord[4]));
                          entity.setContactNumber(mobileNo);
                          entity.setCountry(nextRecord[5]);
                          entity.setDescription(nextRecord[6]);
                          entity.setEmail(nextRecord[7]);
                          entity.setIndustry(nextRecord[8]);
                          entity.setNoOfEmploye(Long.parseLong(nextRecord[9]));
                          entity.setParentAccount(nextRecord[10]);
                          entity.setPincode(Long.parseLong(nextRecord[11]));
                          entity.setSalutation(nextRecord[12]);
                          entity.setState(nextRecord[13]);
                          entity.setStreet(nextRecord[14]);
                          entity.setType(nextRecord[15]);
                          repository.save(entity);
                   } else {
                       return ResponseEntity.badRequest().body("Invalid CSV format");
                   }
               }

               return ResponseEntity.ok("File uploaded successfully");

           } catch (IOException e) {
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file: " + e.getMessage());
           }
       }
	
	@GetMapping("/export")
    public void exportLeads(HttpServletResponse response) throws IOException {
        List<AccountVO> accountlist = new ArrayList<>();

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=leads.csv");

        try {
        	 
        	accountlist=repository.findAll();
        	 
              System.out.println(accountlist);
      
        CsvExport.exportAccountToCSV(response.getWriter(), accountlist);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
	
	@GetMapping("/list/{pageIndex}/{pageSize}")
    public ResponseEntity<?> listAccount(@PathVariable("pageIndex") int pageIndex,
            @PathVariable("pageSize") int pageSize, 
            @RequestParam(name ="sortOrder", required = false) String sorter,
            @RequestParam(name ="firstName", required = false) String firstName,
            @RequestParam(name ="accountName", required = false) String accountName,
            @RequestParam(name ="columnName", required = false) String columnName) {
        
        
        try {
            if ((0 <= pageIndex) && (0 < pageSize)) {
                Page<AccountBO> pageAccount = service.listAccount(pageIndex, pageSize, sorter,
                		firstName, accountName, columnName);
                if (null != pageAccount) {
                    return ResponseEntity.ok(pageAccount);
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
        return null;
    }



}
