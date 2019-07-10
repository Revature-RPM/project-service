package com.revature.services;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.revature.util.FileHelper;

@Service
@Profile("local")
@PropertySource("classpath:credentials.properties")
public class StorageLocalMockImpl implements StorageService {
	
	@Value("${ACCESS_KEY_ID}")
	private String awsAccessKeyId;

	@Value("${SECRET_ACCESS_KEY}")
	private String awsSecretAccessKey;

	@Value("${BUCKET_NAME}")
	private String bucketName;

	@Value("${BUCKET_REGION}")
	private String bucketRegion;
	
	@Value("${S3_ENDPOINT}")
	private String s3EndPoint;

	private AWSCredentials credentials;
	private AmazonS3 s3Client;

	Logger logger = Logger.getLogger(StorageLocalMockImpl.class);

//	@Override
//	public void init() {
//		logger.warn("Running with mock S3 implementation. This should only be used for a local development environment.");
//	}
	
	
//	@Override
//	public String store(MultipartFile multipartFile) {
//		return "localhost:8080/unpersisted";
//	}
//
//	@Override
//	public String store(File file) {
//		return "localhost:8080/unpersisted";
//	}
	
	/**
	 * init draws on environment variables setting up an s3Client used to store objects
	 * Added @Transactional
	 * 
	 * @author Stuart Pratuch (190422-JAVA-SPARK-USF)
	 */
	@Transactional
	@PostConstruct
	public void init() {
		credentials = new BasicAWSCredentials(awsAccessKeyId, awsSecretAccessKey);
		System.out.println("awsAccessKeyId: " + awsAccessKeyId);
		System.out.println("awsSecretAccessKey: " + awsSecretAccessKey);
		s3Client = AmazonS3ClientBuilder
			.standard()
			.withCredentials(new AWSStaticCredentialsProvider(credentials))
			.withRegion(bucketRegion)
			.build();
	}
	
	/**
	 * store puts an object in the configured s3 bucket
	 * Added @Transactional
	 * 
	 * @param multipartFile the file representation of the object desired to store
	 * @return the link to the new object
	 * @author Stuart Pratuch (190422-JAVA-SPARK-USF)
	 */
	
	@Transactional
	public String store(MultipartFile multipartFile) {
		System.out.println("inside store(multipartFile) method");
		try {
			System.out.println("multipartFile: " + multipartFile);
			System.out.println("multipartFileOriginalFilename: " + multipartFile.getOriginalFilename());
			System.out.println("fileHelper.convert(): " + FileHelper.convert(multipartFile));
			System.out.println("s3Client: " + s3Client);
			System.out.println("bucketName: " + bucketName);
			s3Client.putObject(bucketName, multipartFile.getOriginalFilename(), FileHelper.convert(multipartFile));
			return  s3EndPoint + '/' + bucketName + '/' + multipartFile.getOriginalFilename();
		} catch (IOException e) {
			e.printStackTrace();
			 return "";
			
		}
	}

	/**
	 * store puts an object in the configured s3 bucket
	 * 
	 * @transactional added
	 * 
	 * TODO: Test this once implemented with AWS S3
	 * 
	 * @param file the file representation fo the object desired to store
	 * @return the link to the new object
	 *@author Stuart Pratuch (190422-JAVA-SPARK-USF)
	 */
	
	@Transactional
	public String store(File file) {
		s3Client.putObject(bucketName, file.getName(), file);
		return s3EndPoint + '/' + bucketName + '/' + file.getName();
	}

}
