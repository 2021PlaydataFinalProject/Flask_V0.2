package io.educare.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import io.educare.dto.LoginDto;
import io.educare.dto.UserDto;
import io.educare.entity.Instructor;
import io.educare.entity.Student;
import io.educare.entity.User;
import io.educare.jwt.TokenProvider;
import io.educare.repository.UserRepository;
import io.educare.util.CookieUtil;

@Service
public class UserServiceImpl implements UserService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private ModelMapper mapper;

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenProvider tokenProvider;
	private final AuthenticationManagerBuilder authenticationManagerBuilder;

	public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenProvider tokenProvider,
			AuthenticationManagerBuilder authenticationManagerBuilder, ModelMapper mapper) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.tokenProvider = tokenProvider;
		this.authenticationManagerBuilder = authenticationManagerBuilder;
		this.mapper = mapper;
	}

	public UserDto login(LoginDto loginDto, HttpServletResponse res) {
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
				loginDto.getUsername(), loginDto.getPassword());

		// authenticate(authenticationToken)?????? customeruserdetailsservice???
		// loaduserbyusername ?????????
		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

		// ?????? ????????? JwtFilter ???????????? doFilter ???????????? ???????????? ?????? ???????????? ????????? ( Security Context ) ???
		// ??????
		SecurityContextHolder.getContext().setAuthentication(authentication);
		// ?????? ?????? ????????? ???????????? jwt ????????? ??????
		String jwt = tokenProvider.createToken(authentication);
		// jwt ????????? ????????? ????????? ?????????
		Cookie accessToken = CookieUtil.createCookie(TokenProvider.AUTHORITIES_KEY, jwt);
		res.addCookie(accessToken);

		Optional<User> userOpt = userRepository.findById(authentication.getName());

		if (userOpt.isPresent()) {
			UserDto userDto = mapper.map(userOpt.get(), UserDto.class);
			return userDto;
		} else {
			logger.error("????????? ?????? ?????? ?????? ??????");
			return null;
		}
	}

	public Boolean logout(HttpServletResponse res) {
		try {
			Cookie resetToken = CookieUtil.createCookie(TokenProvider.AUTHORITIES_KEY, null); // ?????? auth ?????? null
			resetToken.setMaxAge(0); // ??????????????? ????????????
			res.addCookie(resetToken); // ?????? ????????? ???????????? ??????????????? ???
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("???????????? ??????");
			return false;
		}
	}
	
//	public Boolean logout(HttpServletResponse res, HttpServletRequest req) {
//		try {
//
//			System.out.println(req.getCookies());
//
//			Cookie resetToken = CookieUtil.createCookie(TokenProvider.AUTHORITIES_KEY, null); // ?????? auth ?????? null
//			resetToken.setMaxAge(0); // ??????????????? ????????????
//			res.addCookie(resetToken); // ?????? ????????? ???????????? ??????????????? ???
//			return true;
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("???????????? ??????");
//			return false;
//		}
//	}

	// ?????? ?????? username?????? ????????? ????????? ?????? ??? ????????????, UserDto ????????? ???????????? ???????????? ?????? ????????? ?????? ????????? ????????????
	// Database??? ??????
	@Transactional
	public Boolean insertUser(UserDto userDto, MultipartFile mfile) {
		Optional<User> findMyUser = userRepository.findById(userDto.getUsername());
		try {
			if (!findMyUser.isPresent()) {
				String imgname = null;

				try {
					imgname = String.valueOf(System.currentTimeMillis()) + mfile.getOriginalFilename();
					mfile.transferTo(
							new File(System.getProperty("user.dir") + "\\src\\main\\webapp\\userimg\\" + imgname));
					logger.info("{} ???????????? ????????? ??????", userDto.getUsername());
				} catch (IllegalStateException | IOException e) {
					e.printStackTrace();
					logger.error("{} ???????????? ????????? ?????? ??????", userDto.getUsername());
				}

				if (userDto.getRole().equals("student")) {

					Student student = new Student();
					student.setUsername(userDto.getUsername());
					student.setPassword(passwordEncoder.encode(userDto.getPassword()));
					student.setUserRealName(userDto.getUserRealname());
					student.setPhoneNumber(userDto.getPhoneNumber());
					student.setUserImage(imgname);
					userRepository.save(student);
					return true;
				} else {

					Instructor instructor = new Instructor();
					instructor.setUsername(userDto.getUsername());
					instructor.setPassword(passwordEncoder.encode(userDto.getPassword()));
					instructor.setUserRealName(userDto.getUserRealname());
					instructor.setPhoneNumber(userDto.getPhoneNumber());
					instructor.setUserImage(imgname);
					userRepository.save(instructor);
					return true;
				}
			} else {
				logger.error("{} ???????????? ???????????? ??????", userDto.getUsername());
				return false;
			}
		} catch (Exception e) {
			logger.error("{} ???????????? ???????????? ??????", userDto.getUsername());
			return false;
		}
	}

	@Transactional
	public Boolean insertUserNoimg(UserDto userDto) {
		Optional<User> findUser = userRepository.findById(userDto.getUsername());
		try {
			if (!findUser.isPresent()) {
				if (userDto.getRole().equals("student")) {
					Student student = new Student();
					student.setUsername(userDto.getUsername());
					student.setPassword(passwordEncoder.encode(userDto.getPassword()));
					student.setUserRealName(userDto.getUserRealname());
					student.setPhoneNumber(userDto.getPhoneNumber());
					student.setUserImage("default.png");
					userRepository.save(student);
					return true;
				} else {
					Instructor instructor = new Instructor();
					instructor.setUsername(userDto.getUsername());
					instructor.setPassword(passwordEncoder.encode(userDto.getPassword()));
					instructor.setUserRealName(userDto.getUserRealname());
					instructor.setPhoneNumber(userDto.getPhoneNumber());
					instructor.setUserImage("default.png");
					userRepository.save(instructor);
					return true;
				}
			} else {
				logger.error("{} ???????????? ???????????? ??????", userDto.getUsername());
				return false;
			}
		} catch (Exception e) {
			logger.error("{} ???????????? ???????????? ??????", userDto.getUsername());
			return false;
		}
	}

	public UserDto getMyUser(String username) {

		Optional<User> findMyUser = userRepository.findById(username);

		if (findMyUser.isPresent()) {
			User user = userRepository.findById(username).get();
			UserDto userDto = mapper.map(user, UserDto.class);
			logger.info("{} ?????? ?????? ?????? ??????", username);
			return userDto;
		} else {
			logger.error("????????? ?????? {} ?????? ?????? ??????", username);
			return null;
		}
	}

	public UserDto getStudent(String username) {

		Optional<User> findMyUser = userRepository.findById(username);

		if (findMyUser.isPresent()) {
			User user = userRepository.findById(username).get();
			UserDto userDto = mapper.map(user, UserDto.class);
			logger.info("{} ?????? ?????? ?????? ??????", username);
			return userDto;
		} else {
			logger.error("????????? ?????? {} ?????? ?????? ??????", username);
			return null;
		}
	}

	public List<UserDto> getStudentList() {

		List<User> userList = userRepository.findAllUserByRole("ROLE_STUDENT");
		List<UserDto> uDtoList = userList.stream().map(u -> new UserDto(u.getUsername(), null, u.getUserRealName(),
				u.getPhoneNumber(), u.getUserImage(), u.getRole())).collect(Collectors.toList());
		logger.info("?????? ?????? ?????? ?????? ??????");
		return uDtoList;
	}

	@Transactional
	public Boolean updateUser(UserDto userDto, MultipartFile mfile) {

		Optional<User> findUser = userRepository.findById(userDto.getUsername());
		try {
			if (findUser.isPresent()) {
				String imgname = null;

				try {
					imgname = String.valueOf(System.currentTimeMillis()) + mfile.getOriginalFilename();
					mfile.transferTo(
							new File(System.getProperty("user.dir") + "\\src\\main\\webapp\\userimg\\" + imgname));

					String filename = findUser.get().getUserImage();
					File file = new File(System.getProperty("user.dir") + "\\src\\main\\webapp\\userimg\\" + filename);

					if (file.exists() && !filename.equals("default.png")) {
						if (file.delete()) {
							logger.info("{} ?????? ?????? ????????? ?????? ??????", userDto.getUsername());
						} else {
							logger.debug("{} ?????? ?????? ????????? ?????? ??????", userDto.getUsername());
						}
					}
				} catch (IllegalStateException | IOException e) {
					e.printStackTrace();
					logger.error("{} ?????? ????????? ?????? ?????? ??????", userDto.getUsername());
					return false;
				}
				User finduser = findUser.get();
				finduser.setUsername(finduser.getUsername());
				finduser.setPassword(passwordEncoder.encode(userDto.getPassword()));
				finduser.setUserRealName(userDto.getUserRealname());
				finduser.setPhoneNumber(userDto.getPhoneNumber());
				finduser.setUserImage(imgname);
				userRepository.save(finduser);
				return true;
			} else {
				logger.error("????????? ?????? {} ?????? ?????? ??????", userDto.getUsername());
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("????????? ?????? {} ?????? ?????? ??????", userDto.getUsername());
			return false;
		}
	}

	@Transactional
	public Boolean updateUserNoimg(UserDto userDto) {
		Optional<User> findUser = userRepository.findById(userDto.getUsername());
		try {
			if (findUser.isPresent()) {

				User finduser = findUser.get();
				finduser.setUsername(finduser.getUsername());
				finduser.setPassword(passwordEncoder.encode(userDto.getPassword()));
				finduser.setUserRealName(userDto.getUserRealname());
				finduser.setPhoneNumber(userDto.getPhoneNumber());
				userRepository.save(finduser);
				return true;
			} else {
				logger.error("????????? ?????? {} ?????? ?????? ??????", userDto.getUsername());
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("????????? ?????? {} ?????? ?????? ??????", userDto.getUsername());
			return false;
		}
	}

	@Transactional
	public Boolean deleteUser(String username, HttpServletResponse res) {

		Optional<User> findUser = userRepository.findById(username);
		try {
			if (findUser.isPresent()) {

				String filename = findUser.get().getUserImage();
				File file = new File(System.getProperty("user.dir") + "\\src\\main\\webapp\\userimg\\" + filename);

				if (file.exists() && !filename.equals("default.png")) {
					if (file.delete()) {
						logger.info("{} ?????? ?????? ????????? ?????? ??????", findUser.get().getUsername());
					} else {
						logger.error("{} ?????? ?????? ????????? ?????? ??????", findUser.get().getUsername());
					}
				}
				userRepository.delete(findUser.get());
				// ????????????
				Cookie resetToken = CookieUtil.createCookie(TokenProvider.AUTHORITIES_KEY, null); // ?????? auth ?????? null
				resetToken.setMaxAge(0); // ??????????????? ????????????
				res.addCookie(resetToken); // ?????? ????????? ???????????? ??????????????? ???

				logger.info("{} ?????? ?????? ??????", username);
				return true;
			} else {
				logger.error("????????? ?????? {} ?????? ?????? ??????", username);
				return false;
			}
		} catch (Exception e) {
			logger.error("?????? {} ?????? ?????? ??????", username);
			return false;
		}
	}

}
