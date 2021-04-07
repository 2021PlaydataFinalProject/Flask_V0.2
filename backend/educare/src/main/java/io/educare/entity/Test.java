package io.educare.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Builder
@Data
@AllArgsConstructor
@Entity
public class Test {
	
	@Id
	@Column(name = "test_num")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long testNum;
	
	@Column(name = "test_name")
	private String testName;
	
	@ManyToOne
	@JoinColumn(name = "ins_id")
	private Instructor insId;
	
	@Column(name = "start_time")
	private LocalDateTime startTime;

	@Column(name = "end_time")
	private LocalDateTime endTime;

	@Column(name = "test_guide")
	private String testGuide;
	
	//@JsonBackReference
	@OneToMany(mappedBy="testNum")
	List<StudentTest> studentList = new ArrayList<StudentTest>();
	
	//@JsonBackReference
	@OneToMany(mappedBy="testNum")
	List<TestProblem> problemList = new ArrayList<TestProblem>();
}
