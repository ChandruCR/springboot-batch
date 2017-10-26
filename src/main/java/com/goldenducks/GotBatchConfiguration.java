package com.goldenducks;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@EnableBatchProcessing
public class GotBatchConfiguration {
	
	@Autowired
	public JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	public DataSource dataSource;
	
	// Read - Process - Write - Start
	@Bean
	public FlatFileItemReader<GotCharacter> reader(){
		// Flat file reader to read contents from a csv file
		FlatFileItemReader<GotCharacter> reader = new FlatFileItemReader<GotCharacter>();
		// set resource will provide path for flat file
		reader.setResource(new ClassPathResource("gotcharacters.csv"));
		// map line in file to members of GotCharacter class
		reader.setLineMapper(new DefaultLineMapper<GotCharacter>() {{
			setLineTokenizer(new DelimitedLineTokenizer() {{
				setNames(new String[] {"firstName", "lastName"});
			}});
			setFieldSetMapper(new BeanWrapperFieldSetMapper<GotCharacter>() {{
				setTargetType(GotCharacter.class);
			}});
		}});
		return reader;
	}
	
	@Bean
	public GotCharacterItemProcessor processor(){
		return new GotCharacterItemProcessor();
	}
	
	@Bean
	 public JdbcBatchItemWriter<GotCharacter> writer() {
		// Jdbc Item writer to write contents to database
        JdbcBatchItemWriter<GotCharacter> writer = new JdbcBatchItemWriter<GotCharacter>();
        // setting source sql query and datasource
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<GotCharacter>());
        writer.setSql("INSERT INTO gotcharacters (first_name, last_name) VALUES (:firstName, :lastName)");
        writer.setDataSource(dataSource);
        return writer;
     }
	// Read - Process - Write - End
	
	
	// Job and step
	@Bean
    public Job importUserJob(JobCompletionNotificationListener listener) {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1())
                .end()
                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<GotCharacter, GotCharacter> chunk(null)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

}
