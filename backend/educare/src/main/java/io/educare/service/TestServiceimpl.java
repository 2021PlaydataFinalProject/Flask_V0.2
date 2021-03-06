package io.educare.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.educare.dto.TestDto;
import io.educare.entity.Instructor;
import io.educare.entity.Test;
import io.educare.entity.User;
import io.educare.repository.TestRepository;
import io.educare.repository.UserRepository;

@Service
public class TestServiceimpl implements TestService {

	private ModelMapper mapper;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final TestRepository testRepository;
	private final UserRepository userRepository;

	public TestServiceimpl(TestRepository testRepository, UserRepository userRepository, ModelMapper mapper) {
		this.testRepository = testRepository;
		this.userRepository = userRepository;
		this.mapper = mapper;
	}

	@Transactional
	public Boolean insertTest(String username, Test test) {
		Optional<User> userOpt = userRepository.findById(username);
		
		try {
		if (userOpt.isPresent()) {
			Instructor ins = (Instructor) userOpt.get();
			test.setInsId(ins);
			Test savedtest = testRepository.save(test);
			ins.getTestList().add(savedtest);
			logger.info("{} 강사 시험 등록 성공", username);
			return true;
		} else {
			logger.info("{} 강사 시험 등록 실패", username);
			return false;
		}
		} catch(Exception e) {
			e.printStackTrace();
			logger.info("{} 강사 시험 등록 실패", username);
			return false;
		}
	}

	public List<TestDto> getTestsByUsername(String username) {

		List<Test> testlist = testRepository.findAllTestByUsername(username);
		List<TestDto> collect = testlist.stream().map(t -> new TestDto(t.getTestNum(), t.getTestName(), 
				t.getStartTime(), t.getEndTime(), t.getTestGuide())).collect(Collectors.toList());
		logger.info("{} 강사 출제 시험 전체 조회 요청", username);
		return collect;
	}

	public TestDto getTest(Long testnum) {

		Optional<Test> testOpt = testRepository.findById(testnum);
		TestDto testDto = mapper.map(testOpt.get(), TestDto.class);
		logger.info("{} 시험 조회 요청", testnum);
		return testDto;
	}

	@Transactional
	public Boolean updateTest(Test test) {
		Optional<Test> testOpt = testRepository.findById(test.getTestNum());

		try {
			if (testOpt.isPresent()) {
				Test savedTest = testOpt.get();

				savedTest.setStartTime(test.getStartTime());
				savedTest.setEndTime(test.getEndTime());
				savedTest.setTestGuide(test.getTestGuide());
				savedTest.setTestName(test.getTestName());

				testRepository.save(savedTest);
				logger.info("{} 시험 수정 성공", test.getTestNum());
				return true;
			} else {
				logger.info("{} 강사 수정 실패", test.getTestNum());
				return false;
			}
		} catch (Exception e) {
			logger.error("{} 강사 수정 실패", test.getTestNum());
			e.printStackTrace();
			return false;
		}
	}

	@Transactional
	public Boolean deleteTest(String username, Long testnum) {

		Optional<User> userOpt = userRepository.findById(username);
		Optional<Test> findTest = testRepository.findById(testnum);

		try {
			if (findTest.isPresent() && userOpt.isPresent()) {
				Instructor ins = (Instructor) userOpt.get();
				List<Test> testlist = ins.getTestList();

				for (int i = 0; i < testlist.size(); i++) {
					Integer idx = testlist.indexOf(findTest.get());
					if (idx != -1) {
						testlist.remove(idx);
					} else {
						logger.error("{} 시험 삭제 실패", testnum);
						return false;
					}
				}
				testRepository.delete(findTest.get());
				logger.info("{} 시험 삭제 완료", testnum);
				return true;
			} else {
				logger.error("{} 시험 삭제 실패", testnum);
				return false;
			}
		} catch (Exception e) {
			logger.error("{} 시험 삭제 실패", testnum);
			return false;
		}
	}
}