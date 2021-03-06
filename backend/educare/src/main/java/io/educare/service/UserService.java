package io.educare.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import io.educare.dto.LoginDto;
import io.educare.dto.UserDto;

public interface UserService {
	public Boolean insertUser(UserDto userDto, MultipartFile mfile);

	public Boolean insertUserNoimg(UserDto userDto);

	public UserDto login(LoginDto loginDto, HttpServletResponse res);

	public Boolean logout(HttpServletResponse res);
	
	//public Boolean logout(HttpServletResponse res, HttpServletRequest req);
	
	public UserDto getMyUser(String username);
	
	public UserDto getStudent(String username);
	
	public List<UserDto> getStudentList();
	
	public Boolean updateUser(UserDto userDto, MultipartFile mfile);
	
	public Boolean updateUserNoimg(UserDto userDto);
	
	public Boolean deleteUser(String username, HttpServletResponse res);
}
