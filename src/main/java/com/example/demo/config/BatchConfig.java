package com.example.demo.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.validator.OptionalValidator;
import com.example.demo.validator.RequiredValidator;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

	
	/** JobBuilderのFactoryクラス */
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	/** StepBuilderのFactoryクラス */
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	/** HelloTasklet */
	@Autowired
	@Qualifier("HelloTasklet")
	private Tasklet helloTasklet;

	/** HelloTasklet2 */
	@Autowired
	@Qualifier("HelloTasklet2")
	private Tasklet helloTasklet2;

	/** 必須入力チェックのValidator */
	@Bean
	public JobParametersValidator defaultValidator() {
		DefaultJobParametersValidator validator = new DefaultJobParametersValidator();
		
		// 必須入力
		String[] requiredKeys = new String[] {"run.id", "require1"};
		validator.setRequiredKeys(requiredKeys);
		
		// オプション入力
		String[] optionalKeys = new String[] {"option1","option2"};
		validator.setOptionalKeys(optionalKeys);
		
		// 必須キーとオプションキーの間に重複が無いことを確認
		validator.afterPropertiesSet();
		
		return validator;
	}
	
	/** 複数チェックのValidator */
	@Bean
	public JobParametersValidator compositeValidator() {
		
		// ValidatorのList生成
		List<JobParametersValidator> validators = new ArrayList<>();
		validators.add(defaultValidator());
		validators.add(new RequiredValidator());
		validators.add(new OptionalValidator());

		// CompositeにValidatorを入れる
		CompositeJobParametersValidator compositeValidator = new CompositeJobParametersValidator();
		compositeValidator.setValidators(validators);
		
		return compositeValidator;
	}
	
	
	/** TaskletのStepを生成 */
	@Bean
	public Step taskeltStep1() {
		return stepBuilderFactory.get("HelloTaskletStep1")//Builderの取得
				.tasklet(helloTasklet)//Taskletのセット
				.build();//Stepの生成
	}
	
	/** TaskletのStepを生成 */
	@Bean
	public Step taskeltStep2() {
		return stepBuilderFactory.get("HelloTaskletStep2")//Builderの取得
				.tasklet(helloTasklet2)//Taskletのセット
				.build();//Stepの生成
	}

	/** Jobを生成 */
	@Bean
	public Job taskletJob() throws Exception {
		return jobBuilderFactory.get("HelloWorldTaskletJob") //Builderの取得
				.incrementer(new RunIdIncrementer()) //IDのインクリメント
				.start(taskeltStep1()) //最初のStep
				.next(taskeltStep2()) //次のStep
//				.validator(defaultValidator()) //Validator
				.validator(compositeValidator()) //Validator
				.build();
	}
}
