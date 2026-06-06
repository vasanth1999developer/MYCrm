package com.microservices.authorizationservice.controller;

import java.util.Set;

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

import com.microservices.authorizationservice.model.AccessBo;
import com.microservices.authorizationservice.model.PrivilegeBo;
import com.microservices.authorizationservice.service.PrivilegeService;
//@CrossOrigin(origins="*")
@RestController
@RequestMapping("/authorization/privilege")
public class PrivilegeController {

	@Autowired
	public PrivilegeService service;

	@GetMapping("check-duplicate")
	public ResponseEntity<?> checkDuplicateName(@RequestParam("privilegeName") String privilegeName) {
		try {
			if (privilegeName != null) {
				if (service.checkDuplicateName(privilegeName)) {
					return new ResponseEntity<Boolean>(true, HttpStatus.CONFLICT);
				} else {
					return ResponseEntity.ok(false);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.internalServerError().body("internal server error");
	}

	@PostMapping("create")
	public ResponseEntity<PrivilegeBo> createPrivilege(@RequestBody PrivilegeBo privilegeBo) {
		try {
			if (privilegeBo != null) {
				privilegeBo = service.createPrivilege(privilegeBo);
				return new ResponseEntity<PrivilegeBo>(privilegeBo, HttpStatus.OK);
			} else {
				return new ResponseEntity<PrivilegeBo>(privilegeBo, HttpStatus.NO_CONTENT);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<PrivilegeBo>(privilegeBo, HttpStatus.NO_CONTENT);

	}

	@GetMapping("list/{pageIndex}/{pageSize}")
	public ResponseEntity<Page<PrivilegeBo>> listPrivilege(@PathVariable("pageIndex") int pageIndex,
			@PathVariable("pageSize") int pageSize, @RequestParam(name = "sortOrder",required=false) String sortOrder,
			@RequestParam(name="searchText",required=false) String searchText) {
		Page<PrivilegeBo> privilegeList = null;
		try {
			if (pageIndex > 0 || pageSize > 0 && sortOrder != null || null != searchText) {
				privilegeList = service.listAllPrivileges(pageIndex, pageSize, sortOrder, searchText);
				if(!privilegeList.isEmpty() && privilegeList!=null) {
					return new ResponseEntity<Page<PrivilegeBo>>(privilegeList,HttpStatus.OK);
				}else {
					return new ResponseEntity<Page<PrivilegeBo>>(privilegeList,HttpStatus.NO_CONTENT);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ResponseEntity<Page<PrivilegeBo>>(privilegeList, HttpStatus.NO_CONTENT);
	}
	
	
   @GetMapping("view/{privilegeId}")
   public ResponseEntity<PrivilegeBo> viewPrivilege(@PathVariable("privilegeId") int privilegeId){
	   PrivilegeBo privilegeBo=null;
	   try {
		   if(privilegeId>0) {
			 privilegeBo=service.findIdByPrivilege(privilegeId);
			 if(null !=privilegeBo) {
				 return new ResponseEntity<PrivilegeBo>(privilegeBo,HttpStatus.OK);
			 }
		   }   
	   }catch (Exception e) {
		e.printStackTrace();
	}
	   return new ResponseEntity<PrivilegeBo>(privilegeBo,HttpStatus.NO_CONTENT);
	   
   }
   
   @PutMapping("update")
   public ResponseEntity<?> updatePrivilege(@RequestBody PrivilegeBo privilegeBo){ 
	   try {
		   if(privilegeBo!=null) { 
			   if(service.updatePrivilege(privilegeBo)) {
				   return new ResponseEntity<Boolean>(true,HttpStatus.OK);
			   }else {
				   return new ResponseEntity<Boolean>(false,HttpStatus.OK);
			   }
		   }
	   }catch (Exception e) {
	}
	   return ResponseEntity.internalServerError().body("internal server error");
   }
   
   @DeleteMapping("delete/{privilegeId}")
   public ResponseEntity<?> deletePrivilege(@PathVariable("privilegeId") int privilegeId){
	   
	 try {
		 if(privilegeId>0) {
			 if(service.deletePrivilege(privilegeId)) {
				 return new ResponseEntity<Boolean>(true,HttpStatus.OK);
			 }else {
				 return new ResponseEntity<Boolean>(false,HttpStatus.NO_CONTENT);
			 }
		 } 
	 }catch (Exception e) {
		e.printStackTrace();
	}  
	 return ResponseEntity.internalServerError().body("internal server error");
   }
   
   @GetMapping("list-all")
	public ResponseEntity<?> getAllPrivileges(){
		try {
				Set<PrivilegeBo> privileges = service.listAllPrivileges();
				if (privileges != null) {
					return ResponseEntity.ok(privileges);
				} else {
					return ResponseEntity.internalServerError().body("Error while fetching set of privileges");
				}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("Internal Server Error");
		}
	}
   
}
